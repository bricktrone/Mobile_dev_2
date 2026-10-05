package ru.mirea.khrechko.foodscan.domain.repository;

import ru.mirea.khrechko.foodscan.domain.models.RecognitionResult;

public interface RecognitionRepository {

    RecognitionResult recognize(String imagePath);
}