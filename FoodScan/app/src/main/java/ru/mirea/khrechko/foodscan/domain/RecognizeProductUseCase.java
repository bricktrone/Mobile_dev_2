package ru.mirea.khrechko.foodscan.domain;

import ru.mirea.khrechko.foodscan.domain.models.RecognitionResult;
import ru.mirea.khrechko.foodscan.domain.repository.RecognitionRepository;

public class RecognizeProductUseCase {

    private final RecognitionRepository recognitionRepository;

    public RecognizeProductUseCase(
            RecognitionRepository recognitionRepository
    ) {
        this.recognitionRepository = recognitionRepository;
    }

    public RecognitionResult execute(String imagePath) {

        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }

        return recognitionRepository.recognize(imagePath);
    }
}
