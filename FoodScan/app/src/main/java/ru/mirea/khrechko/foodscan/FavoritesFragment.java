package ru.mirea.khrechko.foodscan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ru.mirea.khrechko.foodscan.domain.models.Product;

public class FavoritesFragment extends Fragment {
    private MainViewModel viewModel; private ProductRecyclerViewAdapter adapter;
    private View emptyState; private TextView status;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        viewModel = new ViewModelProvider(requireActivity(), new FoodScanViewModelFactory(requireContext())).get(MainViewModel.class);
        status = view.findViewById(R.id.textFavoritesStatus); emptyState = view.findViewById(R.id.emptyFavorites);
        RecyclerView recycler = view.findViewById(R.id.recyclerFavorites);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ProductRecyclerViewAdapter(this::openProduct); recycler.setAdapter(adapter);
        viewModel.getFavorites().observe(getViewLifecycleOwner(), products -> renderFavorites(products, recycler));
        view.findViewById(R.id.buttonBrowseProducts).setOnClickListener(v -> ((MainActivity)requireActivity()).selectTab(R.id.nav_home));
        view.findViewById(R.id.headerProfileIcon).setOnClickListener(v -> ((MainActivity)requireActivity()).navigateTo(new ProfileFragment(), "profile"));
        viewModel.loadFavorites();
    }
    private void renderFavorites(List<Product> products, RecyclerView recycler) {
        adapter.setItems(products);
        boolean empty = products == null || products.isEmpty();
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        status.setText(empty ? "Сохранённых продуктов пока нет" : "Сохранено продуктов: " + products.size());
    }
    private void openProduct(Product product) { ((MainActivity)requireActivity()).openProduct(product); }
}
