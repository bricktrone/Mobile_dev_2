package ru.mirea.khrechko.foodscan.data.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUserStorage implements UserStorage {
    private static final String PREFS_NAME = "foodscan_user";
    private static final String KEY_UID = "uid";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "display_name";
    private static final String KEY_LAST_LOGIN = "last_login_millis";

    private final SharedPreferences preferences;

    public SharedPrefUserStorage(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public UserStorageModel get() {
        String uid = preferences.getString(KEY_UID, null);
        if (uid == null) return null;
        return new UserStorageModel(uid,
                preferences.getString(KEY_EMAIL, ""),
                preferences.getString(KEY_NAME, ""),
                preferences.getLong(KEY_LAST_LOGIN, 0L));
    }

    @Override
    public boolean save(UserStorageModel user) {
        if (user == null) return false;
        return preferences.edit()
                .putString(KEY_UID, user.getUid())
                .putString(KEY_EMAIL, user.getEmail())
                .putString(KEY_NAME, user.getDisplayName())
                .putLong(KEY_LAST_LOGIN, user.getLastLoginMillis())
                .commit();
    }

    @Override
    public void clear() { preferences.edit().clear().apply(); }
}
