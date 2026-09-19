package com.threemsystems.rentmanager;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class FormRequest extends StringRequest {
    public FormRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
    }

    static Map<String, String> sessionHeaders() {
        Map<String, String> headers = new HashMap<>();
        PManagerApp app = PManagerApp.get();
        if (app != null) {
            String token = SessionManager.get(app).getToken();
            if (ReportSupport.filled(token)) {
                headers.put("X-PManager-Token", token);
            }
        }
        return headers;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return sessionHeaders();
    }
}
