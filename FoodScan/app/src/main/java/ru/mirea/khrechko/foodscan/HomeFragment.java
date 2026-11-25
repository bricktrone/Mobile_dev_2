package ru.mirea.khrechko.foodscan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.mirea.khrechko.foodscan.domain.models.Product;

public class HomeFragment extends Fragment {
    private MainViewModel viewModel;
    private ProductRecyclerViewAdapter adapter;
    private EditText barcodeInput;
    private TextView catalogStatus;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);
        viewModel = new ViewModelProvider(requireActivity(), new FoodScanViewModelFactory(requireContext())).get(MainViewModel.class);
        barcodeInput = view.findViewById(R.id.editHomeBarcode);
        catalogStatus = view.findViewById(R.id.textCatalogStatus);
        RecyclerView recycler = view.findViewById(R.id.recyclerProducts);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ProductRecyclerViewAdapter(this::openProduct);
        recycler.setAdapter(adapter);
        viewModel.getCatalog().observe(getViewLifecycleOwner(), adapter::setItems);
        viewModel.getCatalogStorageSummary().observe(getViewLifecycleOwner(), summary -> {
            catalogStatus.setText(summary == null ? "Загрузка каталога…" : summary);
            view.findViewById(R.id.buttonRetryCatalog).setVisibility(summary != null && summary.contains("Ошибка загрузки каталога:") ? View.VISIBLE : View.GONE);
        });
        view.findViewById(R.id.buttonHomeFind).setOnClickListener(v -> searchBarcode());
        view.findViewById(R.id.buttonOpenScan).setOnClickListener(v -> ((MainActivity)requireActivity()).selectTab(R.id.nav_scan));
        view.findViewById(R.id.tileBarcode).setOnClickListener(v -> ((MainActivity)requireActivity()).selectTab(R.id.nav_scan));
        view.findViewById(R.id.tileRecognition).setOnClickListener(v -> ((MainActivity)requireActivity()).openRecognition());
        view.findViewById(R.id.tileFavorites).setOnClickListener(v -> ((MainActivity)requireActivity()).selectTab(R.id.nav_favorites));
        view.findViewById(R.id.tileProfile).setOnClickListener(v -> ((MainActivity)requireActivity()).selectTab(R.id.nav_profile));
        view.findViewById(R.id.buttonRetryCatalog).setOnClickListener(v -> viewModel.reloadCatalog());
        View headerProfile = view.findViewById(R.id.headerProfileIcon);
        headerProfile.setOnClickListener(v -> ((MainActivity)requireActivity()).navigateTo(new ProfileFragment(), "profile"));
    }
    private void searchBarcode() {
        String barcode = barcodeInput.getText() == null ? "" : barcodeInput.getText().toString().trim();
        viewModel.findProductByBarcode(barcode);
    }
    private void openProduct(Product product) { ((MainActivity)requireActivity()).openProduct(product); }
}
