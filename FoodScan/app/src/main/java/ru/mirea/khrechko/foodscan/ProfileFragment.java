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
import ru.mirea.khrechko.foodscan.domain.models.User;

public class ProfileFragment extends Fragment {
    private MainViewModel viewModel; private TextView name,email;
    private View login,register,logout;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        viewModel = new ViewModelProvider(requireActivity(), new FoodScanViewModelFactory(requireContext())).get(MainViewModel.class);
        name=view.findViewById(R.id.textProfileName); email=view.findViewById(R.id.textProfileEmail);
        login=view.findViewById(R.id.buttonProfileLogin); register=view.findViewById(R.id.buttonProfileRegister); logout=view.findViewById(R.id.buttonProfileLogout);
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), this::renderUser);
        login.setOnClickListener(v -> ((MainActivity)requireActivity()).openAuth());
        register.setOnClickListener(v -> ((MainActivity)requireActivity()).openAuth());
        logout.setOnClickListener(v -> viewModel.logout());
        view.findViewById(R.id.menuMyFavorites).setOnClickListener(v -> ((MainActivity)requireActivity()).selectTab(R.id.nav_favorites));
        view.findViewById(R.id.menuRecognition).setOnClickListener(v -> ((MainActivity)requireActivity()).openRecognition());
        view.findViewById(R.id.menuAbout).setOnClickListener(v -> android.widget.Toast.makeText(requireContext(), "FoodScan · каталог Open Food Facts и локальное избранное Room", android.widget.Toast.LENGTH_LONG).show());
        view.findViewById(R.id.headerProfileIcon).setVisibility(View.INVISIBLE);
        viewModel.refreshProfile();
    }
    private void renderUser(User user) {
        boolean guest = user == null || user.isGuest();
        name.setText(guest ? "Гостевой режим" : user.getLogin());
        email.setText(guest ? "Войдите, чтобы использовать аккаунт" : "Аккаунт FoodScan");
        login.setVisibility(guest ? View.VISIBLE : View.GONE);
        register.setVisibility(guest ? View.VISIBLE : View.GONE);
        logout.setVisibility(guest ? View.GONE : View.VISIBLE);
    }
}
