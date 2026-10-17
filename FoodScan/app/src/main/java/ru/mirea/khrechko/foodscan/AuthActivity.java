package ru.mirea.khrechko.foodscan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.khrechko.foodscan.di.ServiceLocator;
import ru.mirea.khrechko.foodscan.domain.LoginUseCase;
import ru.mirea.khrechko.foodscan.domain.RegisterUseCase;
import ru.mirea.khrechko.foodscan.domain.models.User;
import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;

public class AuthActivity extends AppCompatActivity {
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private AuthRepository authRepository;
    private LoginUseCase loginUseCase;
    private RegisterUseCase registerUseCase;
    private EditText editName, editEmail, editPassword;
    private Button buttonLogin, buttonRegister, buttonGuest;
    private TextView textError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        authRepository = ServiceLocator.get(getApplicationContext()).authRepository();
        loginUseCase = new LoginUseCase(authRepository);
        registerUseCase = new RegisterUseCase(authRepository);

        editName = findViewById(R.id.editDisplayName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonGuest = findViewById(R.id.buttonGuest);
        textError = findViewById(R.id.textAuthError);

        User user = authRepository.getProfile();
        if (user != null && !user.isGuest()) {
            openMain();
            return;
        }
        buttonLogin.setOnClickListener(v -> authenticate(false));
        buttonRegister.setOnClickListener(v -> authenticate(true));
        buttonGuest.setOnClickListener(v -> openMain());
    }

    private void authenticate(boolean register) {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            textError.setText("Введите почту и пароль.");
            return;
        }
        setLoading(true);
        io.execute(() -> {
            boolean success = register
                    ? registerUseCase.execute(email, password, name)
                    : loginUseCase.execute(email, password);
            String error = success ? "" : authRepository.getLastError();
            runOnUiThread(() -> {
                setLoading(false);
                if (success) openMain();
                else textError.setText(error.isEmpty()
                        ? "Проверьте почту и пароль. Для регистрации пароль должен быть не короче 6 символов."
                        : error);
            });
        });
    }

    private void setLoading(boolean loading) {
        buttonLogin.setEnabled(!loading);
        buttonRegister.setEnabled(!loading);
        buttonGuest.setEnabled(!loading);
        textError.setText(loading ? "Выполняется запрос…" : "");
    }

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() { io.shutdown(); super.onDestroy(); }
}
