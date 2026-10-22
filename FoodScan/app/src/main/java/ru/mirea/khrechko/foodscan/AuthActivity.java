package ru.mirea.khrechko.foodscan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import ru.mirea.khrechko.foodscan.R;

public class AuthActivity extends AppCompatActivity {
    private AuthViewModel viewModel;
    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private Button buttonGuest;
    private TextView textError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        editName = findViewById(R.id.editDisplayName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonGuest = findViewById(R.id.buttonGuest);
        textError = findViewById(R.id.textAuthError);

        viewModel = new ViewModelProvider(this, new FoodScanViewModelFactory(this))
                .get(AuthViewModel.class);
        viewModel.getLoading().observe(this, this::showLoading);
        viewModel.getError().observe(this, textError::setText);
        viewModel.getOpenMain().observe(this, open -> {
            if (Boolean.TRUE.equals(open)) openMain();
        });

        buttonLogin.setOnClickListener(v -> authenticate(false));
        buttonRegister.setOnClickListener(v -> authenticate(true));
        buttonGuest.setOnClickListener(v -> viewModel.continueAsGuest());
        viewModel.checkSession();
    }

    private void authenticate(boolean register) {
        viewModel.authenticate(editName.getText().toString(), editEmail.getText().toString(),
                editPassword.getText().toString(), register);
    }

    private void showLoading(boolean loading) {
        buttonLogin.setEnabled(!loading);
        buttonRegister.setEnabled(!loading);
        buttonGuest.setEnabled(!loading);
        if (loading) textError.setText("Выполняется запрос…");
    }

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
