package ru.mirea.khrechko.scrollviewapp;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int COUNT = 100;
    private static final int RATIO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout wrapper = findViewById(R.id.wrapper);
        BigInteger value = BigInteger.ONE;
        for (int i = 0; i < COUNT; i++) {
            View item = getLayoutInflater().inflate(R.layout.item, wrapper, false);
            TextView index = item.findViewById(R.id.textViewIndex);
            TextView number = item.findViewById(R.id.textViewValue);
            index.setText(String.format(Locale.getDefault(), "Элемент %d · b%d = 2^%d", i + 1, i + 1, i));
            number.setText(value.toString());
            wrapper.addView(item);
            value = value.multiply(BigInteger.valueOf(RATIO));
        }
    }
}
