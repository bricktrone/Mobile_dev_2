package ru.mirea.khrechko.foodscan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import ru.mirea.khrechko.foodscan.databinding.ActivityMainBinding;
import ru.mirea.khrechko.foodscan.domain.models.Product;

/** Main host for FoodScan navigation. Navigation Component manages all Fragment destinations and the back stack. */
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private NavController navController;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this, new FoodScanViewModelFactory(this)).get(MainViewModel.class);
        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHost == null) throw new IllegalStateException("NavHostFragment не найден");
        navController = navHost.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

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
    }

    /** Opens a destination represented in the BottomNavigation menu. */
    public void selectTab(int menuItemId) {
        if (binding != null) binding.bottomNavigation.setSelectedItemId(menuItemId);
    }

    public void openProduct(@NonNull Product product) {
        viewModel.selectProduct(product.getId());
        Bundle args = new Bundle();
        args.putInt("product_id", product.getId());
        NavOptions options = new NavOptions.Builder().setLaunchSingleTop(true).build();
        navController.navigate(R.id.productDetailFragment, args, options);
    }

    public void openRecognition() {
        navController.navigate(R.id.recognitionFragment, null,
                new NavOptions.Builder().setLaunchSingleTop(true).build());
    }

    public void openAuth() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public MainViewModel getMainViewModel() { return viewModel; }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (viewModel != null) viewModel.refreshProfile();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
