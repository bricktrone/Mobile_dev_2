package ru.mirea.khrechko.foodscan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.lifecycle.ViewModelProvider;
import com.squareup.picasso.Picasso;
import ru.mirea.khrechko.foodscan.domain.models.Product;

public class ProductDetailFragment extends Fragment {
    private static final String ARG_PRODUCT_ID = "product_id";
    private ProductDetailViewModel detailViewModel; private MainViewModel mainViewModel;
    private TextView name,brand,calories,ingredients,info,protein,fat;
    private ImageView image; private Button favorite;
    public static ProductDetailFragment newInstance(int id) {
        Bundle args=new Bundle(); args.putInt(ARG_PRODUCT_ID,id);
        ProductDetailFragment fragment=new ProductDetailFragment(); fragment.setArguments(args); return fragment;
    }
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_product_detail,container,false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        name=view.findViewById(R.id.textDetailName); brand=view.findViewById(R.id.textDetailBrand);
        calories=view.findViewById(R.id.textDetailCalories); ingredients=view.findViewById(R.id.textDetailIngredients);
        image=view.findViewById(R.id.imageDetailProduct); favorite=view.findViewById(R.id.buttonToggleFavorite);
        info=view.findViewById(R.id.textDetailInfo); protein=view.findViewById(R.id.textDetailProtein); fat=view.findViewById(R.id.textDetailFat);
        mainViewModel=new ViewModelProvider(requireActivity(),new FoodScanViewModelFactory(requireContext())).get(MainViewModel.class);
        detailViewModel=new ViewModelProvider(this,new FoodScanViewModelFactory(requireContext())).get(ProductDetailViewModel.class);
        detailViewModel.getProduct().observe(getViewLifecycleOwner(),this::renderProduct);
        detailViewModel.getCanSaveFavorite().observe(getViewLifecycleOwner(),allowed->{
            favorite.setEnabled(Boolean.TRUE.equals(allowed));
            if (!Boolean.TRUE.equals(allowed)) favorite.setText("Войдите, чтобы использовать избранное");
        });
        detailViewModel.getIsFavorite().observe(getViewLifecycleOwner(),saved->{
            if (Boolean.TRUE.equals(detailViewModel.getCanSaveFavorite().getValue())) favorite.setText(Boolean.TRUE.equals(saved)?"♥  Убрать из избранного":"♡  Добавить в избранное");
        });
        detailViewModel.getMessage().observe(getViewLifecycleOwner(),message->{
            if (message != null && !message.trim().isEmpty()) {
                android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show();
                if (message.contains("Добавлено") || message.contains("Удалено")) mainViewModel.refreshFavoritesSource();
            }
        });
        favorite.setOnClickListener(v -> detailViewModel.toggleFavorite());
        view.findViewById(R.id.buttonBackProduct).setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        int id=requireArguments().getInt(ARG_PRODUCT_ID,-1);
        if (id < 0) id = mainViewModel.getSelectedProductId().getValue() == null ? -1 : mainViewModel.getSelectedProductId().getValue();
        detailViewModel.loadProduct(id);
    }
    private String formatNutrient(double value) {
        if (value < 0) return "—";
        return String.format(java.util.Locale.getDefault(), "%.1f", value);
    }

    private void renderProduct(Product product) {
        if (product == null) { name.setText("Продукт не найден"); favorite.setEnabled(false); return; }
        name.setText(product.getName()); brand.setText("Бренд: " + product.getBrand());
        calories.setText(product.getCalories() > 0 ? String.valueOf(product.getCalories()) : "—");
        protein.setText(formatNutrient(product.getProteins()));
        fat.setText(formatNutrient(product.getFat()));
        ingredients.setText(product.getIngredients()); info.setText("Источник: Open Food Facts · ID: " + product.getId());
        String url=product.getImageUrl();
        if (url == null || url.trim().isEmpty()) image.setImageResource(R.drawable.ic_package);
        else Picasso.get().load(url).placeholder(R.drawable.ic_package).error(R.drawable.ic_package).fit().centerInside().into(image);
    }
}
