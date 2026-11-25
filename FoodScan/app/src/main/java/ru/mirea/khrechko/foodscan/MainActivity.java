package ru.mirea.khrechko.foodscan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import ru.mirea.khrechko.foodscan.domain.models.Product;

/** Single-activity host. Each application screen is a Fragment managed by FragmentManager. */
public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private BottomNavigationView bottomNavigation;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this, new FoodScanViewModelFactory(this)).get(MainViewModel.class);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) showRoot(new HomeFragment());
            else if (id == R.id.nav_scan) showRoot(new ScanFragment());
            else if (id == R.id.nav_favorites) showRoot(new FavoritesFragment());
            else if (id == R.id.nav_profile) showRoot(new ProfileFragment());
            else return false;
            return true;
        });
        viewModel.getProductFromBarcode().observe(this, product -> {
            if (product != null) {
                viewModel.clearProductFromBarcode();
                openProduct(product);
            }
        });
        viewModel.getMessage().observe(this, message -> {
            if (message != null && !message.trim().isEmpty()) Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
        viewModel.getLoggedOut().observe(this, loggedOut -> {
            if (Boolean.TRUE.equals(loggedOut)) {
                viewModel.clearLoggedOut();
                Intent intent = new Intent(this, AuthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    private void showRoot(@NonNull Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();

        manager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
        );

        manager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(
                        R.id.fragmentContainer,
                        fragment,
                        fragment.getClass().getSimpleName()
                )
                .commit();
    }

    public void navigateTo(@NonNull Fragment fragment, @NonNull String backStackName) {
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(backStackName).commit();
    }

    public void openProduct(@NonNull Product product) {
        viewModel.selectProduct(product.getId());
        navigateTo(ProductDetailFragment.newInstance(product.getId()), "product_detail");
    }

    public void openRecognition() { navigateTo(new RecognitionFragment(), "recognition"); }
    public void openAuth() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public MainViewModel getMainViewModel() { return viewModel; }

    public void selectTab(int menuItemId) {
        bottomNavigation.setSelectedItemId(menuItemId);
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (viewModel != null) viewModel.refreshProfile();
    }
}
