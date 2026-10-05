package ru.mirea.khrechko.foodscan;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import ru.mirea.khrechko.foodscan.data.repository.AuthRepositoryImpl;
import ru.mirea.khrechko.foodscan.data.repository.FavoriteRepositoryImpl;
import ru.mirea.khrechko.foodscan.data.repository.ProductRepositoryImpl;
import ru.mirea.khrechko.foodscan.data.repository.RecognitionRepositoryImpl;
import ru.mirea.khrechko.foodscan.domain.GetProductCatalogUseCase;
import ru.mirea.khrechko.foodscan.domain.LoginUseCase;
import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.models.User;
import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;
import ru.mirea.khrechko.foodscan.domain.repository.FavoriteRepository;
import ru.mirea.khrechko.foodscan.domain.repository.ProductRepository;
import ru.mirea.khrechko.foodscan.domain.repository.RecognitionRepository;

public class MainActivity extends AppCompatActivity {

    private AuthRepository authRepository;
    private ProductRepository productRepository;
    private FavoriteRepository favoriteRepository;
    private RecognitionRepository recognitionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Создание реализаций репозиториев
        authRepository = new AuthRepositoryImpl(this);
        productRepository = new ProductRepositoryImpl(this);
        favoriteRepository = new FavoriteRepositoryImpl(this);
        recognitionRepository = new RecognitionRepositoryImpl(this);

        // Создание use-case
        LoginUseCase loginUseCase =
                new LoginUseCase(authRepository);

        GetProductCatalogUseCase catalogUseCase =
                new GetProductCatalogUseCase(productRepository);

        // Тест авторизации
        boolean loginResult =
                loginUseCase.execute(
                        "student",
                        "1234"
                );

        User user =
                authRepository.getProfile();

        // Получение каталога
        List<Product> products =
                catalogUseCase.execute();

        TextView textView =
                findViewById(R.id.textViewResult);

        if (loginResult && user != null) {

            StringBuilder result =
                    new StringBuilder();

            result.append("Пользователь: ")
                    .append(user.getLogin())
                    .append("\n\n");

            result.append("Каталог продуктов:\n");

            for (Product product : products) {

                result.append("- ")
                        .append(product.getName())
                        .append("\n");
            }

            textView.setText(result.toString());
        }
    }
}