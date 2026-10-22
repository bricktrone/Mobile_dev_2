package ru.mirea.khrechko.foodscan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import ru.mirea.khrechko.foodscan.domain.models.Product;

public class ProductDetailActivity extends AppCompatActivity {
    private ProductDetailViewModel viewModel;
    private TextView textName;
    private TextView textBrand;
    private TextView textIngredients;
    private TextView textCalories;
    private ImageView image;
    private Button buttonFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        textName = findViewById(R.id.textDetailName);
        textBrand = findViewById(R.id.textDetailBrand);
        textIngredients = findViewById(R.id.textDetailIngredients);
        textCalories = findViewById(R.id.textDetailCalories);
        image = findViewById(R.id.imageDetailProduct);
        buttonFavorite = findViewById(R.id.buttonToggleFavorite);

        viewModel = new ViewModelProvider(this, new FoodScanViewModelFactory(this))
                .get(ProductDetailViewModel.class);
        viewModel.getProduct().observe(this, this::renderProduct);
        viewModel.getCanSaveFavorite().observe(this, canSave -> {
            boolean allowed = Boolean.TRUE.equals(canSave);
            buttonFavorite.setEnabled(allowed);
            buttonFavorite.setText(allowed ? "Добавить / удалить избранное" : "Войдите, чтобы сохранить");
        });
        viewModel.getMessage().observe(this, message -> {
            if (message != null && !message.trim().isEmpty())
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
        buttonFavorite.setOnClickListener(v -> viewModel.toggleFavorite());
        viewModel.loadProduct(getIntent().getIntExtra("product_id", -1));
    }

    private void renderProduct(Product product) {
        if (product == null) {
            textName.setText("Продукт не найден");
            buttonFavorite.setEnabled(false);
            return;
        }
        textName.setText(product.getName());
        textBrand.setText("Бренд: " + product.getBrand());
        textIngredients.setText("Состав: " + product.getIngredients());
        textCalories.setText("Энергетическая ценность: " + product.getCalories() + " ккал");
        Glide.with(this).load(product.getImageUrl()).placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery).centerCrop().into(image);
    }
}
