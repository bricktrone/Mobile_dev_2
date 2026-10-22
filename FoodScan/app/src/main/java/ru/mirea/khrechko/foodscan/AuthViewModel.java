package ru.mirea.khrechko.foodscan;

import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.khrechko.foodscan.domain.GetProfileUseCase;
import ru.mirea.khrechko.foodscan.domain.LoginUseCase;
import ru.mirea.khrechko.foodscan.domain.RegisterUseCase;
import ru.mirea.khrechko.foodscan.domain.models.User;
import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;

/** Authentication state and use-case calls; this class never holds an Activity or View. */
public class AuthViewModel extends ViewModel {
    private static final String TAG = AuthViewModel.class.getSimpleName();

    private final AuthRepository authRepository;
    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> openMain = new MutableLiveData<>(false);

    public AuthViewModel(AuthRepository authRepository) {
        Log.d(TAG, "AuthViewModel created");
        this.authRepository = authRepository;
        loginUseCase = new LoginUseCase(authRepository);
        registerUseCase = new RegisterUseCase(authRepository);
        getProfileUseCase = new GetProfileUseCase(authRepository);
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getOpenMain() { return openMain; }

    public void checkSession() {
        io.execute(() -> {
            User user = getProfileUseCase.execute();
            if (user != null && !user.isGuest()) openMain.postValue(true);
        });
    }

    private static final int MIN_PASSWORD_LENGTH = 6;

    public void authenticate(String name, String email, String password, boolean register) {
        String normalizedEmail = email == null ? "" : email.trim();
        String normalizedPassword = password == null ? "" : password;

        // Validate input before calling Firebase so local validation errors are
        // not replaced by a generic authentication error or a previous error.
        if (normalizedEmail.isEmpty()) {
            error.setValue("Введите адрес электронной почты.");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
            error.setValue("Введите корректный адрес электронной почты.");
            return;
        }
        if (normalizedPassword.isEmpty()) {
            error.setValue("Введите пароль.");
            return;
        }
        if (normalizedPassword.length() < MIN_PASSWORD_LENGTH) {
            error.setValue("Пароль должен содержать не менее 6 символов.");
            return;
        }

        error.setValue("");
        loading.setValue(true);
        io.execute(() -> {
            boolean success = register
                    ? registerUseCase.execute(normalizedEmail, normalizedPassword,
                            name == null ? "" : name.trim())
                    : loginUseCase.execute(normalizedEmail, normalizedPassword);
            String message = "";
            if (!success) {
                message = authRepository.getLastError();
                if (message == null || message.trim().isEmpty()) {
                    message = "Не удалось выполнить запрос. Проверьте введённые данные и повторите попытку.";
                }
            }
            loading.postValue(false);
            if (success) {
                error.postValue("");
                openMain.postValue(true);
            } else {
                error.postValue(message);
            }
        });
    }

    public void continueAsGuest() {
        error.setValue("");
        openMain.setValue(true);
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "AuthViewModel cleared");
        io.shutdownNow();
        super.onCleared();
    }
}
