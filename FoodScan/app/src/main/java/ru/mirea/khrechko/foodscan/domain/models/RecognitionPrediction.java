package ru.mirea.khrechko.foodscan.domain.models;

/** One category predicted by the on-device food image classifier. */
public class RecognitionPrediction {
    private final String label;
    private final float confidence;

    public RecognitionPrediction(String label, float confidence) {
        this.label = label;
        this.confidence = confidence;
    }

    public String getLabel() { return label; }
    public float getConfidence() { return confidence; }
}
