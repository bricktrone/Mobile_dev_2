package ru.mirea.khrechko.foodscan;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.lifecycle.ViewModelProvider;

public class RecognitionFragment extends Fragment {
    private MainViewModel viewModel;
    private ImageView preview; private View placeholder; private TextView result;
    private Uri selectedImage;
    private final ActivityResultLauncher<String> imagePicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null && isAdded()) {
            selectedImage = uri;
            preview.setImageURI(uri); preview.setVisibility(View.VISIBLE); placeholder.setVisibility(View.GONE);
            result.setText("Фотография выбрана. Нажмите «Распознать продукт».");
        }
    });
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_recognition, container, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        viewModel = new ViewModelProvider(requireActivity(), new FoodScanViewModelFactory(requireContext())).get(MainViewModel.class);
        preview=view.findViewById(R.id.imageRecognitionPreview); placeholder=view.findViewById(R.id.recognitionPlaceholder); result=view.findViewById(R.id.textRecognitionResult);
        view.findViewById(R.id.buttonBackRecognition).setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        view.findViewById(R.id.buttonChooseImage).setOnClickListener(v -> imagePicker.launch("image/*"));
        view.findViewById(R.id.buttonRunRecognition).setOnClickListener(v -> {
            if (selectedImage == null) { result.setText("Сначала выберите фотографию продукта."); return; }
            viewModel.runRecognition(selectedImage.toString());
        });
        viewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && message.startsWith("Тестовый результат:")) result.setText(message);
        });
    }
}
