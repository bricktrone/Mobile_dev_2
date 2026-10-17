package ru.mirea.khrechko.foodscan.data.auth;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FirebaseAuthSource {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public AuthUserDto signIn(String email, String password) throws AuthSourceException {
        try {
            FirebaseUser user = Tasks.await(
                    firebaseAuth.signInWithEmailAndPassword(email, password)).getUser();
            if (user == null) throw new IllegalStateException("Firebase не вернул пользователя");
            return mapUser(user);
        } catch (Exception e) {
            throw new AuthSourceException(mapError(e), e);
        }
    }

    public AuthUserDto register(String email, String password, String displayName)
            throws AuthSourceException {
        try {
            FirebaseUser user = Tasks.await(
                    firebaseAuth.createUserWithEmailAndPassword(email, password)).getUser();
            if (user == null) throw new IllegalStateException("Firebase не вернул пользователя");
            String safeName = displayName == null ? "" : displayName.trim();
            if (safeName.isEmpty()) safeName = email.substring(0, email.indexOf('@'));
            Tasks.await(user.updateProfile(new UserProfileChangeRequest.Builder()
                    .setDisplayName(safeName).build()));
            FirebaseUser updated = firebaseAuth.getCurrentUser();
            return mapUser(updated == null ? user : updated);
        } catch (Exception e) {
            throw new AuthSourceException(mapError(e), e);
        }
    }

    public AuthUserDto getCurrentUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user == null ? null : mapUser(user);
    }

    public void signOut() { firebaseAuth.signOut(); }

    private AuthUserDto mapUser(FirebaseUser user) {
        return new AuthUserDto(user.getUid(), user.getEmail(), user.getDisplayName());
    }

    private String mapError(Throwable error) {
        Throwable current = error;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        if (current instanceof FirebaseAuthWeakPasswordException) {
            return "Пароль должен содержать не менее 6 символов.";
        }
        if (current instanceof FirebaseAuthUserCollisionException) {
            return "Аккаунт с такой почтой уже зарегистрирован.";
        }
        if (current instanceof FirebaseAuthInvalidCredentialsException) {
            return "Неверная почта или пароль.";
        }
        if (current instanceof FirebaseNetworkException) {
            return "Нет подключения к сети. Проверьте интернет и повторите попытку.";
        }
        return "Не удалось выполнить авторизацию. Проверьте данные и настройки Firebase.";
    }
}
