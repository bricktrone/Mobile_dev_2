package ru.mirea.khrechko.foodscan;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.models.User;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private ProductRecyclerViewAdapter adapter;
    private TextView textProfile;
    private TextView textDataSources;
    private Button buttonAccount;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textProfile = findViewById(R.id.textProfile);
        textDataSources = findViewById(R.id.textDataSources);
        buttonAccount = findViewById(R.id.buttonAccount);
        RecyclerView recyclerProducts = findViewById(R.id.recyclerProducts);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductRecyclerViewAdapter(this::openProduct);
        recyclerProducts.setAdapter(adapter);

        viewModel = new ViewModelProvider(this, new FoodScanViewModelFactory(this))
                .get(MainViewModel.class);
        viewModel.getCurrentUser().observe(this, user -> {
            currentUser = user;
            updateProfileHeader();
        });
        // Catalog arrives through LiveData from MainViewModel (NetworkApi mock -> repository -> use case).
        viewModel.getCatalog().observe(this, adapter::setItems);
        viewModel.getCatalogStorageSummary().observe(this, textDataSources::setText);
        viewModel.getFavoriteDialogItems().observe(this, this::showFavoritesResult);
        viewModel.getProductFromBarcode().observe(this, product -> {
            if (product != null) openProduct(product);
        });
        viewModel.getMessage().observe(this, message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });
        viewModel.getLoggedOut().observe(this, loggedOut -> {
            if (Boolean.TRUE.equals(loggedOut)) {
                viewModel.clearLoggedOut();
                startActivity(new Intent(this, AuthActivity.class));
                finish();
            }
        });

        buttonAccount.setOnClickListener(v -> onAccountClicked());
        findViewById(R.id.buttonFavorites).setOnClickListener(v -> viewModel.loadFavoritesForDialog());
        findViewById(R.id.buttonBarcode).setOnClickListener(v -> askBarcode());
        findViewById(R.id.buttonRecognize).setOnClickListener(v -> viewModel.runDemoRecognition());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null) viewModel.refreshFavoritesSource();
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
        viewModel.logout();
    }

    private void showFavoritesResult(List<Product> products) {
        if (products == null) return;
        List<String> names = new ArrayList<>();
        for (Product product : products) names.add(product.getName());
        if (names.isEmpty()) names.add("Избранное пока пусто");
        new AlertDialog.Builder(this).setTitle("Избранные продукты")
                .setItems(names.toArray(new String[0]), (dialog, which) -> {
                    if (which < products.size()) openProduct(products.get(which));
                }).setPositiveButton("Закрыть", null).show();
        viewModel.clearFavoriteDialogItems();
    }

    private void askBarcode() {
        EditText input = new EditText(this);
        input.setHint("Например, 460000000001");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(this).setTitle("Поиск по штрихкоду").setView(input)
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Найти", (dialog, which) ->
                        viewModel.findProductByBarcode(input.getText().toString()))
                .show();
    }

    private void openProduct(Product product) {
        if (product == null) return;
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }
}
