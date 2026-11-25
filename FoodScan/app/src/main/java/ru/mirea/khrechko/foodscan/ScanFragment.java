package ru.mirea.khrechko.foodscan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ScanFragment extends Fragment {
    private MainViewModel viewModel; private EditText barcode;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        viewModel = new ViewModelProvider(requireActivity(), new FoodScanViewModelFactory(requireContext())).get(MainViewModel.class);
        barcode = view.findViewById(R.id.editBarcode);
        view.findViewById(R.id.buttonFindProduct).setOnClickListener(v -> {
            String value = barcode.getText() == null ? "" : barcode.getText().toString().trim();
            viewModel.findProductByBarcode(value);
        });
        view.findViewById(R.id.headerProfileIcon).setOnClickListener(v -> ((MainActivity)requireActivity()).navigateTo(new ProfileFragment(), "profile"));
    }
}
