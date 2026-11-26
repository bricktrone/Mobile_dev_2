package ru.mirea.khrechko.foodscan.domain.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Result of local TensorFlow Lite image classification. */
public class RecognitionResult {
    private final String productType;
    private final float confidence;
    private final List<RecognitionPrediction> predictions;

    public RecognitionResult(String productType, float confidence) {
        this(productType, confidence, Collections.singletonList(
                new RecognitionPrediction(productType, confidence)));
    }

    public RecognitionResult(String productType, float confidence, List<RecognitionPrediction> predictions) {
        this.productType = productType;
        this.confidence = confidence;
        this.predictions = Collections.unmodifiableList(new ArrayList<>(predictions));
    }

    public String getProductType() { return productType; }
    public float getConfidence() { return confidence; }
    public List<RecognitionPrediction> getPredictions() { return predictions; }
}
