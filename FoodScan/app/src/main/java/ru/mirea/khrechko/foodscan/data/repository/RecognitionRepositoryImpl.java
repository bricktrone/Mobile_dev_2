package ru.mirea.khrechko.foodscan.data.repository;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.vision.classifier.Classifications;
import org.tensorflow.lite.task.vision.classifier.ImageClassifier;
import org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions;
import org.tensorflow.lite.support.image.TensorImage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.mirea.khrechko.foodscan.domain.models.RecognitionPrediction;
import ru.mirea.khrechko.foodscan.domain.models.RecognitionResult;
import ru.mirea.khrechko.foodscan.domain.repository.RecognitionRepository;

/**
 * On-device food image classification. The AIY Food V1 MobileNet model is downloaded
 * once from TensorFlow Hub and then cached in app-private storage.
 * Inference itself is performed locally by the TensorFlow Lite Task Vision library.
 */
public class RecognitionRepositoryImpl implements RecognitionRepository {
    private static final String MODEL_URL =
            "https://tfhub.dev/google/lite-model/aiy/vision/classifier/food_V1/1?lite-format=tflite";
    private static final String MODEL_FILE_NAME = "aiy_food_v1.tflite";
    private static final int MAX_IMAGE_SIDE = 1600;
    private static final int MAX_RESULTS = 5;
    private static final float MIN_SCORE = 0.08f;

    private final Context appContext;
    private volatile ImageClassifier classifier;

    public RecognitionRepositoryImpl(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Override
    public RecognitionResult recognize(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Не передана фотография для распознавания.");
        }

        Bitmap bitmap = null;
        try {
            bitmap = loadBitmap(Uri.parse(imagePath));
            if (bitmap == null) {
                throw new IOException("Не удалось открыть выбранное изображение.");
            }

            ImageClassifier imageClassifier = getClassifier();
            TensorImage tensorImage = TensorImage.fromBitmap(bitmap);
            List<Classifications> classifications = imageClassifier.classify(tensorImage);
            List<RecognitionPrediction> predictions = new ArrayList<>();

            if (classifications != null) {
                for (Classifications group : classifications) {
                    if (group == null || group.getCategories() == null) continue;
                    for (Category category : group.getCategories()) {
                        String label = category.getDisplayName();
                        if (label == null || label.trim().isEmpty()) label = category.getLabel();
                        if (label == null || label.trim().isEmpty()) label = "Класс " + category.getIndex();
                        if ("background".equalsIgnoreCase(label.trim())) continue;
                        predictions.add(new RecognitionPrediction(translateLabel(label), category.getScore()));
                    }
                }
            }

            predictions.sort((left, right) -> Float.compare(right.getConfidence(), left.getConfidence()));
            if (predictions.isEmpty()) {
                throw new IOException("Модель не вернула ни одного результата. Попробуйте другое фото.");
            }
            if (predictions.size() > MAX_RESULTS) {
                predictions = new ArrayList<>(predictions.subList(0, MAX_RESULTS));
            }

            RecognitionPrediction best = predictions.get(0);
            if (best.getConfidence() < MIN_SCORE) {
                throw new IOException("Не удалось уверенно определить продукт. Сфотографируйте его крупнее и при хорошем освещении.");
            }
            return new RecognitionResult(best.getLabel(), best.getConfidence(), predictions);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
        }
    }

    private ImageClassifier getClassifier() throws IOException {
        ImageClassifier current = classifier;
        if (current != null) return current;
        synchronized (this) {
            if (classifier != null) return classifier;
            File modelFile = getModelFile();
            if (!modelFile.exists() || modelFile.length() < 500_000L || !hasTfliteHeader(modelFile)) {
                downloadModel(modelFile);
            }
            ImageClassifierOptions options = ImageClassifierOptions.builder()
                    .setMaxResults(MAX_RESULTS)
                    .setScoreThreshold(MIN_SCORE)
                    .build();
            try {
                classifier = ImageClassifier.createFromFileAndOptions(modelFile, options);
            } catch (RuntimeException e) {
                // A corrupt/incompatible cache must not permanently block subsequent attempts.
                //noinspection ResultOfMethodCallIgnored
                modelFile.delete();
                throw new IOException("Не удалось загрузить модель TensorFlow Lite. Повторите попытку при подключении к интернету.", e);
            }
            return classifier;
        }
    }

    private File getModelFile() throws IOException {
        File directory = new File(appContext.getFilesDir(), "foodscan_models");
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Не удалось создать папку для модели распознавания.");
        }
        return new File(directory, MODEL_FILE_NAME);
    }

    private void downloadModel(File destination) throws IOException {
        File tempFile = new File(destination.getParentFile(), MODEL_FILE_NAME + ".download");
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(MODEL_URL).openConnection();
            connection.setConnectTimeout(25_000);
            connection.setReadTimeout(90_000);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("User-Agent", "FoodScan Android app");
            connection.setRequestProperty("Accept", "application/octet-stream");
            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) {
                throw new IOException("Сервер модели ответил HTTP " + status + ".");
            }
            try (InputStream input = new BufferedInputStream(connection.getInputStream());
                 FileOutputStream fileOutput = new FileOutputStream(tempFile);
                 BufferedOutputStream output = new BufferedOutputStream(fileOutput)) {
                byte[] buffer = new byte[16 * 1024];
                int count;
                while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
                output.flush();
            }
            if (tempFile.length() < 500_000L || !hasTfliteHeader(tempFile)) {
                throw new IOException("Скачанный файл не похож на модель TensorFlow Lite.");
            }
            if (destination.exists() && !destination.delete()) {
                throw new IOException("Не удалось заменить предыдущую модель.");
            }
            if (!tempFile.renameTo(destination)) {
                throw new IOException("Не удалось сохранить модель TensorFlow Lite.");
            }
        } finally {
            if (connection != null) connection.disconnect();
            //noinspection ResultOfMethodCallIgnored
            tempFile.delete();
        }
    }

    private static boolean hasTfliteHeader(File file) {
        try (java.io.RandomAccessFile random = new java.io.RandomAccessFile(file, "r")) {
            if (random.length() < 8) return false;
            random.seek(4);
            byte[] header = new byte[4];
            random.readFully(header);
            return header[0] == 'T' && header[1] == 'F' && header[2] == 'L' && header[3] == '3';
        } catch (IOException ignored) {
            return false;
        }
    }

    private Bitmap loadBitmap(Uri uri) throws IOException {
        if (uri == null) throw new IOException("Адрес изображения не задан.");
        ContentResolver resolver = appContext.getContentResolver();
        int orientation = ExifInterface.ORIENTATION_NORMAL;
        try (InputStream exifInput = openInputStream(uri, resolver)) {
            if (exifInput != null) {
                ExifInterface exif = new ExifInterface(exifInput);
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            }
        } catch (Exception ignored) {
            // Some content providers do not expose EXIF. The image can still be classified.
        }

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream boundsInput = openInputStream(uri, resolver)) {
            if (boundsInput == null) throw new IOException("Не удалось открыть изображение.");
            BitmapFactory.decodeStream(boundsInput, null, bounds);
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw new IOException("Файл не является изображением.");

        BitmapFactory.Options decode = new BitmapFactory.Options();
        decode.inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, MAX_IMAGE_SIDE);
        decode.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap;
        try (InputStream imageInput = openInputStream(uri, resolver)) {
            if (imageInput == null) throw new IOException("Не удалось прочитать изображение.");
            bitmap = BitmapFactory.decodeStream(imageInput, null, decode);
        }
        if (bitmap == null) throw new IOException("Не удалось декодировать изображение.");

        Matrix transform = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90: transform.postRotate(90); break;
            case ExifInterface.ORIENTATION_ROTATE_180: transform.postRotate(180); break;
            case ExifInterface.ORIENTATION_ROTATE_270: transform.postRotate(270); break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL: transform.setScale(-1, 1); break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL: transform.setScale(1, -1); break;
            case ExifInterface.ORIENTATION_TRANSPOSE: transform.setRotate(90); transform.postScale(-1, 1); break;
            case ExifInterface.ORIENTATION_TRANSVERSE: transform.setRotate(270); transform.postScale(-1, 1); break;
            default: break;
        }
        if (!transform.isIdentity()) {
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), transform, true);
            if (rotated != bitmap) bitmap.recycle();
            bitmap = rotated;
        }
        return bitmap;
    }

    private InputStream openInputStream(Uri uri, ContentResolver resolver) throws IOException {
        String scheme = uri.getScheme();
        if (scheme == null || "file".equalsIgnoreCase(scheme)) {
            String path = "file".equalsIgnoreCase(scheme) ? uri.getPath() : uri.toString();
            return new FileInputStream(path);
        }
        return resolver.openInputStream(uri);
    }

    private static int calculateInSampleSize(int width, int height, int maxSide) {
        int sample = 1;
        while (width / sample > maxSide || height / sample > maxSide) sample *= 2;
        return Math.max(1, sample);
    }

    private static String translateLabel(String source) {
        String value = source.trim();
        String key = value.toLowerCase(Locale.ROOT);
        switch (key) {
            case "banana": return "Банан";
            case "granny smith": return "Зелёное яблоко";
            case "orange": return "Апельсин";
            case "lemon": return "Лимон";
            case "strawberry": return "Клубника";
            case "pineapple": return "Ананас";
            case "pomegranate": return "Гранат";
            case "broccoli": return "Брокколи";
            case "cauliflower": return "Цветная капуста";
            case "cucumber": return "Огурец";
            case "bell pepper": return "Болгарский перец";
            case "corn": case "ear": return "Кукуруза";
            case "pizza": return "Пицца";
            case "cheeseburger": return "Чизбургер";
            case "hamburger": return "Гамбургер";
            case "hotdog": case "hot dog": return "Хот-дог";
            case "burrito": return "Буррито";
            case "ice cream": return "Мороженое";
            case "bagel": return "Бейгл";
            case "pretzel": return "Крендель";
            case "spaghetti": return "Спагетти";
            case "carbonara": return "Паста карбонара";
            case "soup": return "Суп";
            case "meat loaf": return "Мясной рулет";
            default: return value;
        }
    }
}
