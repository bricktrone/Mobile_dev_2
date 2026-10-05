package ru.mirea.khrechko.foodscan.domain.models;

public class RecognitionResult {

    private String productType;
    private float confidence;

    public RecognitionResult(String productType, float confidence) {
        this.productType = productType;
        this.confidence = confidence;
    }

    public String getProductType() {
        return productType;
    }

    public float getConfidence() {
        return confidence;
    }
}
