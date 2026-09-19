package com.threemsystems.rentmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Session is owner id + display name + role. Passwords are never persisted.
 */
public class SessionManager {
    public static final String PREFS_LEGACY = "user_details";
    private static final String PREFS_SECURE = "pmanager_session";
    private static final String KEY_OWNER_ID = "idNo";
    private static final String KEY_NAME = "displayName";
    private static final String KEY_LEVEL = "userlevel";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "sessionToken";
    private static final String KEY_PASSWORD = "password";
    private static final String MISSING = "MisingID";

    private static SessionManager instance;
    private final SharedPreferences secure;
    private final SharedPreferences legacy;

    private SessionManager(Context context) {
        Context app = context.getApplicationContext();
        legacy = app.getSharedPreferences(PREFS_LEGACY, Context.MODE_PRIVATE);
        secure = openSecurePrefs(app);
    }

    public static synchronized SessionManager get(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    private SharedPreferences openSecurePrefs(Context app) {
        try {
            String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            return EncryptedSharedPreferences.create(
                    PREFS_SECURE,
                    masterKey,
                    app,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            return app.getSharedPreferences(PREFS_SECURE, Context.MODE_PRIVATE);
        }
    }

    public void migrateLegacySession() {
        if (isLoggedIn()) {
            return;
        }
        String id = legacy.getString(KEY_OWNER_ID, "");
        if (isUsable(id)) {
            saveSession(
                    id,
                    firstNonEmpty(legacy.getString(KEY_NAME, ""), legacy.getString(KEY_USERNAME, "")),
                    legacy.getString(KEY_LEVEL, "1"),
                    legacy.getString(KEY_USERNAME, ""),
                    ""
            );
        }
        legacy.edit().remove(KEY_PASSWORD).commit();
    }

    public void saveSession(String ownerId, String displayName, String userLevel, String username) {
        saveSession(ownerId, displayName, userLevel, username, getToken());
    }

    public void saveSession(String ownerId, String displayName, String userLevel, String username, String token) {
        write(secure, ownerId, displayName, userLevel, username, token);
        legacy.edit().clear().commit();
        stripPasswords(secure);
    }

    public void clear() {
        secure.edit().clear().commit();
        legacy.edit().clear().commit();
    }

    public boolean isLoggedIn() {
        return isUsable(getOwnerId()) && isUsable(getToken());
    }

    public String getOwnerId() {
        String id = firstNonEmpty(secure.getString(KEY_OWNER_ID, ""), legacy.getString(KEY_OWNER_ID, ""));
        return isUsable(id) ? id : "";
    }

    public String getUsername() {
        return firstNonEmpty(secure.getString(KEY_USERNAME, ""), legacy.getString(KEY_USERNAME, ""));
    }

    public String getToken() {
        return firstNonEmpty(secure.getString(KEY_TOKEN, ""), legacy.getString(KEY_TOKEN, ""));
    }

    public String getDisplayName() {
        String name = firstNonEmpty(
                secure.getString(KEY_NAME, ""),
                legacy.getString(KEY_NAME, ""),
                legacy.getString(KEY_USERNAME, "")
        );
        return TextUtils.isEmpty(name) ? "there" : name;
    }

    public String getUserLevel() {
        String level = firstNonEmpty(secure.getString(KEY_LEVEL, ""), legacy.getString(KEY_LEVEL, "1"));
        return TextUtils.isEmpty(level) ? "1" : level;
    }

    public int getUserLevelInt() {
        try {
            return Integer.parseInt(getUserLevel());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public boolean canManagePortfolio() {
        return getUserLevelInt() == 9;
    }

    private void write(SharedPreferences prefs, String ownerId, String displayName, String userLevel,
                       String username, String token) {
        prefs.edit()
                .putString(KEY_OWNER_ID, ownerId)
                .putString(KEY_NAME, displayName)
                .putString(KEY_LEVEL, userLevel)
                .putString(KEY_USERNAME, username == null ? "" : username)
                .putString(KEY_TOKEN, token == null ? "" : token)
                .remove(KEY_PASSWORD)
                .commit();
    }

    private void stripPasswords(SharedPreferences prefs) {
        if (prefs.contains(KEY_PASSWORD)) {
            prefs.edit().remove(KEY_PASSWORD).apply();
        }
    }

    private static boolean isUsable(String value) {
        return !TextUtils.isEmpty(value) && !MISSING.equalsIgnoreCase(value);
    }

    private static String firstNonEmpty(String... values) {
        for (String value : values) {
            if (!TextUtils.isEmpty(value) && !MISSING.equalsIgnoreCase(value)) {
                return value;
            }
        }
        return "";
    }
}
