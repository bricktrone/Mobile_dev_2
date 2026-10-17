package ru.mirea.khrechko.foodscan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.khrechko.foodscan.di.ServiceLocator;
import ru.mirea.khrechko.foodscan.domain.AddToFavoritesUseCase;
import ru.mirea.khrechko.foodscan.domain.GetFavoritesUseCase;
import ru.mirea.khrechko.foodscan.domain.RemoveFromFavoritesUseCase;
import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.models.User;
import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;
import ru.mirea.khrechko.foodscan.domain.repository.FavoriteRepository;

public class ProductDetailActivity extends AppCompatActivity {
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private AuthRepository authRepository;
    private FavoriteRepository favoriteRepository;
    private Product product;
    private Button buttonFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ServiceLocator locator = ServiceLocator.get(getApplicationContext());
        authRepository = locator.authRepository();
        favoriteRepository = locator.favoriteRepository();

        product = new Product(getIntent().getIntExtra("product_id", -1),
                getIntent().getStringExtra("product_name"),
                getIntent().getStringExtra("product_brand"),
                getIntent().getStringExtra("product_image"),
                getIntent().getStringExtra("product_ingredients"),
                getIntent().getIntExtra("product_calories", 0));

        ((TextView) findViewById(R.id.textDetailName)).setText(product.getName());
        ((TextView) findViewById(R.id.textDetailBrand)).setText("Бренд: " + product.getBrand());
        ((TextView) findViewById(R.id.textDetailIngredients)).setText("Состав: " + product.getIngredients());
        ((TextView) findViewById(R.id.textDetailCalories)).setText("Энергетическая ценность: " + product.getCalories() + " ккал");
        ImageView image = findViewById(R.id.imageDetailProduct);
        Glide.with(this).load(product.getImageUrl()).placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery).centerCrop().into(image);

        buttonFavorite = findViewById(R.id.buttonToggleFavorite);
        User user = authRepository.getProfile();
        buttonFavorite.setEnabled(user != null && !user.isGuest());
        if (user == null || user.isGuest()) buttonFavorite.setText("Войдите, чтобы сохранить");
        else buttonFavorite.setText("Добавить / удалить избранное");
        buttonFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void toggleFavorite() {
        User user = authRepository.getProfile();
        if (user == null || user.isGuest()) {
            Toast.makeText(this, "Войдите в аккаунт, чтобы использовать избранное", Toast.LENGTH_SHORT).show();
            return;
        }
        buttonFavorite.setEnabled(false);
        io.execute(() -> {
            List<Product> favorites = new GetFavoritesUseCase(authRepository, favoriteRepository).execute();
            boolean exists = false;
            for (Product item : favorites) if (item.getId() == product.getId()) exists = true;
            boolean changed = exists
                    ? new RemoveFromFavoritesUseCase(authRepository, favoriteRepository).execute(product)
                    : new AddToFavoritesUseCase(authRepository, favoriteRepository).execute(product);
            boolean wasAdded = !exists;
            runOnUiThread(() -> {
                buttonFavorite.setEnabled(true);
                Toast.makeText(this, changed
                        ? (wasAdded ? "Добавлено в избранное" : "Удалено из избранного")
                        : "Не удалось изменить избранное", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    protected void onDestroy() { io.shutdown(); super.onDestroy(); }
}
