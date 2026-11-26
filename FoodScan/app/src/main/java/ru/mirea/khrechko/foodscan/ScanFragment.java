package ru.mirea.khrechko.foodscan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;

public class ScanFragment extends Fragment {
    private MainViewModel viewModel;
    private EditText barcode;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        viewModel = new ViewModelProvider(requireActivity(), new FoodScanViewModelFactory(requireContext())).get(MainViewModel.class);
        barcode = view.findViewById(R.id.editBarcode);

        view.findViewById(R.id.buttonFindProduct).setOnClickListener(v -> {
            String value = barcode.getText() == null ? "" : barcode.getText().toString().trim();
            if (value.isEmpty()) {
                barcode.setError("Введите штрихкод");
                return;
            }
            hideKeyboard();
            viewModel.findProductByBarcode(value);
        });

        view.findViewById(R.id.buttonScanCamera).setOnClickListener(v -> startCameraScanner());
        view.findViewById(R.id.headerProfileIcon).setOnClickListener(v -> ((MainActivity) requireActivity()).selectTab(R.id.nav_profile));
    }

    private void startCameraScanner() {
        if (!isAdded()) return;
        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_EAN_8,
                        Barcode.FORMAT_UPC_A,
                        Barcode.FORMAT_UPC_E,
                        Barcode.FORMAT_CODE_128,
                        Barcode.FORMAT_ITF)
                .enableAutoZoom()
                .build();

        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(requireActivity(), options);
        scanner.startScan()
                .addOnSuccessListener(result -> {
                    if (!isAdded()) return;
                    String value = result.getRawValue();
                    if (value == null || value.trim().isEmpty()) {
                        Toast.makeText(requireContext(), "Не удалось прочитать штрихкод. Попробуйте ещё раз.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    barcode.setText(value);
                    hideKeyboard();
                    viewModel.findProductByBarcode(value);
                })
                .addOnCanceledListener(() -> {
                    if (isAdded()) Toast.makeText(requireContext(), "Сканирование отменено", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(error -> {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Не удалось запустить сканер. Проверьте Google Play services: " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void hideKeyboard() {
        if (barcode == null || !isAdded()) return;
        InputMethodManager manager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) manager.hideSoftInputFromWindow(barcode.getWindowToken(), 0);
    }
}
