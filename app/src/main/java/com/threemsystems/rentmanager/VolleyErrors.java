package com.threemsystems.rentmanager;

import android.app.Activity;
import android.content.Context;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public final class VolleyErrors {
    private VolleyErrors() {}

    public static String message(VolleyError error) {
        if (error instanceof TimeoutError) {
            return "The server took too long to respond. Try again.";
        }
        if (error == null || error.networkResponse == null) {
            return "Couldn't reach the server. Check your connection and try again.";
        }
        return "The server returned an error. Try again.";
    }

    public static void show(Context context, VolleyError error) {
        if (context instanceof Activity) {
            UiNotifier.snack((Activity) context, message(error));
        }
    }
}
