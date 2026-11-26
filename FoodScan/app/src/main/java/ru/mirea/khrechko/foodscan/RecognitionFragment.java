package ru.mirea.khrechko.foodscan;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.List;
import java.util.Locale;

import ru.mirea.khrechko.foodscan.domain.models.RecognitionPrediction;
import ru.mirea.khrechko.foodscan.domain.models.RecognitionResult;

public class RecognitionFragment extends Fragment {
    private MainViewModel viewModel;
    private ImageView preview;
    private View placeholder;
    private TextView result;
    private MaterialButton runButton;
    private Uri selectedImage;
    private Uri pendingCameraUri;
    private File pendingCameraPhoto;

    private final ActivityResultLauncher<String> imagePicker = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && isAdded()) {
                    selectedImage = uri;
                    preview.setImageURI(uri);
                    preview.setVisibility(View.VISIBLE);
                    placeholder.setVisibility(View.GONE);
                    viewModel.clearRecognitionResult();
                    result.setText("Фотография выбрана. Нажмите «Распознать продукт».");
                }
            });

    private final ActivityResultLauncher<Uri> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(), success -> {
                if (!isAdded()) return;
                if (Boolean.TRUE.equals(success) && pendingCameraUri != null
                        && pendingCameraPhoto != null && pendingCameraPhoto.length() > 0) {
                    selectedImage = pendingCameraUri;
                    preview.setImageURI(selectedImage);
                    preview.setVisibility(View.VISIBLE);
                    placeholder.setVisibility(View.GONE);
                    viewModel.clearRecognitionResult();
                    result.setText("Фото сделано. Нажмите «Распознать продукт».");
                } else {
                    if (pendingCameraPhoto != null) pendingCameraPhoto.delete();
                    result.setText("Фотография не была сделана. Попробуйте ещё раз или выберите изображение из галереи.");
                }
            });

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_recognition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        viewModel = new ViewModelProvider(requireActivity(), new FoodScanViewModelFactory(requireContext())).get(MainViewModel.class);
        preview = view.findViewById(R.id.imageRecognitionPreview);
        placeholder = view.findViewById(R.id.recognitionPlaceholder);
        result = view.findViewById(R.id.textRecognitionResult);
        runButton = view.findViewById(R.id.buttonRunRecognition);

        view.findViewById(R.id.buttonBackRecognition).setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        view.findViewById(R.id.buttonTakePhoto).setOnClickListener(v -> capturePhoto());
        view.findViewById(R.id.buttonChooseImage).setOnClickListener(v -> imagePicker.launch("image/*"));
        runButton.setOnClickListener(v -> {
            if (selectedImage == null) {
                result.setText("Сначала сфотографируйте продукт или выберите изображение из галереи.");
                return;
            }
            result.setText("Анализ изображения… При первом запуске приложение загрузит модель TensorFlow Lite.");
            viewModel.runRecognition(selectedImage.toString());
        });

        viewModel.getRecognitionResult().observe(getViewLifecycleOwner(), this::showResult);
        viewModel.getRecognitionError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.trim().isEmpty()) result.setText(error);
        });
        viewModel.getRecognitionInProgress().observe(getViewLifecycleOwner(), running -> {
            boolean isRunning = Boolean.TRUE.equals(running);
            runButton.setEnabled(!isRunning);
            runButton.setText(isRunning ? "Распознавание…" : "Распознать продукт");
        });
    }

    private void capturePhoto() {
        if (!isAdded()) return;
        try {
            pendingCameraPhoto = new File(requireContext().getCacheDir(),
                    "foodscan_capture_" + System.currentTimeMillis() + ".jpg");
            pendingCameraUri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".fileprovider", pendingCameraPhoto);
            cameraLauncher.launch(pendingCameraUri);
        } catch (Exception e) {
            result.setText("Не удалось открыть камеру: " + e.getLocalizedMessage());
        }
    }

    private void showResult(RecognitionResult recognition) {
        if (recognition == null || !isAdded()) return;
        StringBuilder text = new StringBuilder("Результат распознавания\n");
        List<RecognitionPrediction> predictions = recognition.getPredictions();
        for (int i = 0; i < predictions.size(); i++) {
            RecognitionPrediction item = predictions.get(i);
            text.append(i + 1).append(". ").append(item.getLabel())
                    .append(" — ").append(String.format(Locale.getDefault(), "%.1f%%", item.getConfidence() * 100f))
                    .append('\n');
        }
        text.append("\nМодель определяет категорию блюда, но не бренд, ингредиенты или пищевую ценность. Для поиска конкретного упакованного товара используйте штрихкод.");
        result.setText(text.toString());
    }
}
