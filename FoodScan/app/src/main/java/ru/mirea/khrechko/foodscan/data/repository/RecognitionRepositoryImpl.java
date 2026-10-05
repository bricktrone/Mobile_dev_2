package ru.mirea.khrechko.foodscan.data.repository;

import android.content.Context;

import ru.mirea.khrechko.foodscan.domain.models.RecognitionResult;
import ru.mirea.khrechko.foodscan.domain.repository.RecognitionRepository;

public class RecognitionRepositoryImpl implements RecognitionRepository {

    public RecognitionRepositoryImpl(Context context) {
    }

    @Override
    public RecognitionResult recognize(String imagePath) {

        // Тестовый результат.
        // В дальнейшем здесь будет TensorFlow Lite.

        return new RecognitionResult(
                "banana",
                0.93f
        );
    }
}