package com.threemsystems.rentmanager;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class ApiClient {
    public interface Callback {
        void onSuccess(String result);

        void onError(String message);
    }

    private static final ApiClient INSTANCE = new ApiClient();
    private final Handler main = new Handler(Looper.getMainLooper());

    public static ApiClient get() {
        return INSTANCE;
    }

    public void post(String url, String[] fields, String[] data, Callback callback) {
        post(null, url, fields, data, callback);
    }

    public void post(Activity activity, String url, String[] fields, String[] data, Callback callback) {
        Map<String, String> params = new HashMap<>();
        if (fields != null && data != null) {
            int count = Math.min(fields.length, data.length);
            for (int i = 0; i < count; i++) {
                if (fields[i] != null) {
                    params.put(fields[i], data[i] == null ? "" : data[i]);
                }
            }
        }
        WeakReference<Activity> ref = activity == null ? null : new WeakReference<>(activity);
        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, url,
                result -> deliverSuccess(ref, callback, result),
                error -> deliverError(ref, callback, VolleyErrors.message(error))) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return FormRequest.sessionHeaders();
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1f));
        if (activity != null) {
            request.setTag(activity.getClass().getName());
        }
        PManagerApp.get().getRequestQueue().add(request);
    }

    private void deliverSuccess(WeakReference<Activity> ref, Callback callback, String result) {
        main.post(() -> {
            if (!alive(ref)) {
                return;
            }
            if (result == null || result.trim().isEmpty()) {
                callback.onError("The server returned an empty response. Try again.");
            } else {
                callback.onSuccess(result);
            }
        });
    }

    private void deliverError(WeakReference<Activity> ref, Callback callback, String message) {
        main.post(() -> {
            if (!alive(ref)) {
                return;
            }
            callback.onError(message);
        });
    }

    private static boolean alive(WeakReference<Activity> ref) {
        if (ref == null) {
            return true;
        }
        Activity activity = ref.get();
        return activity != null && !activity.isFinishing() && !activity.isDestroyed();
    }
}
