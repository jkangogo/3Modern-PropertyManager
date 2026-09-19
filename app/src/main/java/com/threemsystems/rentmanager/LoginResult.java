package com.threemsystems.rentmanager;

import org.json.JSONObject;

public final class LoginResult {
    public final boolean success;
    public final String ownerId;
    public final String name;
    public final String userLevel;
    public final String username;
    public final String token;
    public final String message;

    private LoginResult(boolean success, String ownerId, String name, String userLevel,
                        String username, String token, String message) {
        this.success = success;
        this.ownerId = ownerId == null ? "" : ownerId;
        this.name = name == null ? "" : name;
        this.userLevel = filled(userLevel) ? userLevel : "1";
        this.username = username == null ? "" : username;
        this.token = token == null ? "" : token;
        this.message = message == null ? "" : message;
    }

    public static LoginResult parse(String raw, String fallbackUsername) {
        String user = fallbackUsername == null ? "" : fallbackUsername;
        if (raw == null || raw.trim().isEmpty()) {
            return fail("Couldn't sign in. Try again.");
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("{")) {
            try {
                JSONObject json = new JSONObject(trimmed);
                if ("true".equalsIgnoreCase(json.optString("success"))) {
                    String name = json.optString("name", user);
                    return new LoginResult(
                            true,
                            json.optString("owner_id"),
                            filled(name) ? name : user,
                            json.optString("user_level", "1"),
                            json.optString("username", user),
                            json.optString("token"),
                            json.optString("message", "Signed in.")
                    );
                }
                return fail(json.optString("message", "Incorrect username or password."));
            } catch (Exception e) {
                return fail("Couldn't sign in. Try again.");
            }
        }
        String stripped = trimmed.replace("\"", "");
        String[] parts = stripped.split("_", -1);
        if (parts.length >= 3 && "success".equalsIgnoreCase(parts[2])) {
            String ownerId = parts[0];
            String name = parts.length > 1 ? parts[1] : user;
            String userLevel = parts.length > 4 ? parts[4] : (parts.length > 3 ? parts[3] : "1");
            return new LoginResult(true, ownerId, name, userLevel, user, "", "Signed in.");
        }
        return fail("Incorrect username or password.");
    }

    private static LoginResult fail(String message) {
        return new LoginResult(false, "", "", "1", "", "", message);
    }

    private static boolean filled(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
