package com.threemsystems.rentmanager;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

public final class UiNotifier {
    private UiNotifier() {}

    public static void snack(Activity activity, String message) {
        if (activity == null) {
            return;
        }
        View root = activity.findViewById(android.R.id.content);
        if (root != null) {
            Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
        } else {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
        }
    }

    public static String userMessage(String rawResult, String fallback) {
        if (rawResult == null || rawResult.trim().isEmpty()) {
            return fallback;
        }
        String trimmed = rawResult.trim();
        if (trimmed.startsWith("{")) {
            try {
                JSONObject json = new JSONObject(trimmed);
                if (json.has("message")) {
                    return json.getString("message");
                }
            } catch (Exception ignored) {
            }
        }
        if (trimmed.length() > 180 || trimmed.contains("<html") || trimmed.contains("Warning") || trimmed.contains("Notice:")) {
            return fallback;
        }
        return trimmed.replace("\"", "");
    }

    public static boolean jsonSuccess(String rawResult) {
        if (rawResult == null) {
            return false;
        }
        try {
            JSONObject json = new JSONObject(rawResult.trim());
            Object success = json.opt("success");
            if (success instanceof Boolean) {
                return (Boolean) success;
            }
            return "true".equalsIgnoreCase(json.optString("success"));
        } catch (Exception e) {
            return false;
        }
    }
}
