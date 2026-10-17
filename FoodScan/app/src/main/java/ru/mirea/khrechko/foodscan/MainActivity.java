package ru.mirea.khrechko.foodscan;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.khrechko.foodscan.di.ServiceLocator;
import ru.mirea.khrechko.foodscan.domain.AddToFavoritesUseCase;
import ru.mirea.khrechko.foodscan.domain.FindProductByBarcodeUseCase;
import ru.mirea.khrechko.foodscan.domain.GetFavoritesUseCase;
import ru.mirea.khrechko.foodscan.domain.GetProductCatalogUseCase;
import ru.mirea.khrechko.foodscan.domain.RecognizeProductUseCase;
import ru.mirea.khrechko.foodscan.domain.RemoveFromFavoritesUseCase;
import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.models.RecognitionResult;
import ru.mirea.khrechko.foodscan.domain.models.User;
import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;
import ru.mirea.khrechko.foodscan.domain.repository.FavoriteRepository;
import ru.mirea.khrechko.foodscan.domain.repository.ProductRepository;
import ru.mirea.khrechko.foodscan.domain.repository.RecognitionRepository;

public class MainActivity extends AppCompatActivity {
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private AuthRepository authRepository;
    private FavoriteRepository favoriteRepository;
    private ProductRepository productRepository;
    private RecognitionRepository recognitionRepository;
    private User currentUser;
    private ProductAdapter adapter;
    private TextView textProfile;
    private Button buttonAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ServiceLocator locator = ServiceLocator.get(getApplicationContext());
        authRepository = locator.authRepository();
        favoriteRepository = locator.favoriteRepository();
        productRepository = locator.productRepository();
        recognitionRepository = locator.recognitionRepository();

        textProfile = findViewById(R.id.textProfile);
        buttonAccount = findViewById(R.id.buttonAccount);
        ListView listProducts = findViewById(R.id.listProducts);
        adapter = new ProductAdapter(this, new ArrayList<>());
        listProducts.setAdapter(adapter);
        listProducts.setOnItemClickListener((parent, view, position, id) -> openProduct(adapter.getItem(position)));

        currentUser = authRepository.getProfile();
        updateProfileHeader();

        List<Product> products = new GetProductCatalogUseCase(productRepository).execute();
        adapter.clear();
        adapter.addAll(products);
        adapter.notifyDataSetChanged();

        buttonAccount.setOnClickListener(v -> onAccountClicked());
        findViewById(R.id.buttonFavorites).setOnClickListener(v -> showFavorites());
        findViewById(R.id.buttonBarcode).setOnClickListener(v -> askBarcode());
        findViewById(R.id.buttonRecognize).setOnClickListener(v -> runDemoRecognition());
    }

    private void updateProfileHeader() {
        boolean guest = currentUser == null || currentUser.isGuest();
        textProfile.setText(guest ? "Режим гостя" : "Пользователь: " + currentUser.getLogin());
        buttonAccount.setText(guest ? "Войти" : "Выйти");
    }

    private void onAccountClicked() {
        if (currentUser == null || currentUser.isGuest()) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }
        io.execute(() -> {
            authRepository.logout();
            runOnUiThread(() -> {
                startActivity(new Intent(this, AuthActivity.class));
                finish();
            });
        });
    }

    private void showFavorites() {
        if (currentUser == null || currentUser.isGuest()) {
            Toast.makeText(this, "Избранное доступно после входа в аккаунт", Toast.LENGTH_SHORT).show();
            return;
        }
        io.execute(() -> {
            List<Product> products = new GetFavoritesUseCase(authRepository, favoriteRepository).execute();
            List<String> names = new ArrayList<>();
            for (Product product : products) names.add(product.getName());
            runOnUiThread(() -> {
                if (names.isEmpty()) names.add("Избранное пока пусто");
                new AlertDialog.Builder(this).setTitle("Избранные продукты")
                        .setItems(names.toArray(new String[0]), (dialog, which) -> {
                            if (which < products.size()) openProduct(products.get(which));
                        }).setPositiveButton("Закрыть", null).show();
            });
        });
    }

    private void askBarcode() {
        EditText input = new EditText(this);
        input.setHint("Например, 460000000001");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(this).setTitle("Поиск по штрихкоду").setView(input)
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Найти", (dialog, which) -> {
                    Product product = new FindProductByBarcodeUseCase(productRepository)
                            .execute(input.getText().toString());
                    if (product == null) {
                        Toast.makeText(this, "Продукт не найден в тестовом каталоге", Toast.LENGTH_SHORT).show();
                    } else {
                        openProduct(product);
                    }
                }).show();
    }

    private void runDemoRecognition() {
        RecognitionResult result = new RecognizeProductUseCase(recognitionRepository).execute("demo-image");
        if (result == null) {
            Toast.makeText(this, "Не удалось распознать изображение", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this).setTitle("Демонстрация распознавания")
                .setMessage("Тестовый результат: " + result.getProductType() + " " +
                                "Уверенность: "
                        + Math.round(result.getConfidence() * 100) + "% " +
                        "Сейчас используется заглушка. Для настоящего распознавания нужно подключить модель TensorFlow Lite.")
                .setPositiveButton("ОК", null).show();
    }

    private void openProduct(Product product) {
        if (product == null) return;
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_id", product.getId());
        intent.putExtra("product_name", product.getName());
        intent.putExtra("product_brand", product.getBrand());
        intent.putExtra("product_image", product.getImageUrl());
        intent.putExtra("product_ingredients", product.getIngredients());
        intent.putExtra("product_calories", product.getCalories());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        io.shutdown();
        super.onDestroy();
    }
}
