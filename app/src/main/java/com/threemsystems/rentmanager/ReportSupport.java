package com.threemsystems.rentmanager;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ReportSupport {
    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");

    private ReportSupport() {}

    public static String withQuery(String url, Map<String, String> params) {
        if (url == null) {
            return "";
        }
        if (params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue() == null ? "" : entry.getValue());
        }
        return builder.build().toString();
    }

    public static int namedPropertyCount(JSONArray rows) {
        if (rows == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < rows.length(); i++) {
            JSONObject row = rows.optJSONObject(i);
            if (row == null) {
                continue;
            }
            String name = row.optString("property_name", "").trim();
            String code = row.optString("property_code", "").trim();
            if (!name.isEmpty() || !code.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public interface RowsCallback {
        void onRows(JSONArray rows);
    }

    public static void enqueue(StringRequest request) {
        enqueue(null, request);
    }

    public static void enqueue(Activity activity, StringRequest request) {
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(20000, 1, 1f));
        if (activity != null) {
            request.setTag(activity.getClass().getName());
        }
        PManagerApp.get().getRequestQueue().add(request);
    }

    public static void loadRows(Activity activity, String url, String field, String value, RowsCallback callback) {
        Map<String, String> params = new HashMap<>();
        if (filled(field)) {
            params.put(field, value == null ? "" : value);
        }
        loadRows(activity, url, params, callback);
    }

    public static void loadRows(Activity activity, String url, Map<String, String> params, RowsCallback callback) {
        Map<String, String> map = params == null ? new HashMap<>() : new HashMap<>(params);
        StringRequest request = new FormRequest(url,
                response -> {
                    if (callback != null) {
                        callback.onRows(resultArray(response));
                    }
                },
                error -> {
                    if (activity != null && !activity.isFinishing()) {
                        VolleyErrors.show(activity, error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return map;
            }
        };
        enqueue(activity, request);
    }

    public static JSONArray resultArray(String response) {
        if (response == null) {
            return new JSONArray();
        }
        String trimmed = response.trim();
        if (!trimmed.startsWith("{")) {
            return new JSONArray();
        }
        try {
            JSONArray array = new JSONObject(trimmed).optJSONArray("result");
            return array == null ? new JSONArray() : array;
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static boolean isJsonObject(String response) {
        return response != null && response.trim().startsWith("{");
    }

    public static void warnIfNotJson(Activity activity, String response) {
        if (!isJsonObject(response)) {
            UiNotifier.snack(activity, "Couldn't read that report. Try again.");
        }
    }

    public static ArrayAdapter<spinnerItems> spinnerAdapter(Context context, List<spinnerItems> items) {
        ArrayAdapter<spinnerItems> adapter = new ArrayAdapter<>(context, R.layout.item_spinner, items);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        return adapter;
    }

    public static ArrayAdapter<String> stringAdapter(Context context, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.item_spinner, items);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        return adapter;
    }

    public static ArrayAdapter<String> stringAdapter(Context context, String[] items) {
        return stringAdapter(context, Arrays.asList(items));
    }

    public static spinnerItems selected(AdapterView<?> parent) {
        Object item = parent.getSelectedItem();
        return item instanceof spinnerItems ? (spinnerItems) item : null;
    }

    public static String money(String raw) {
        return MONEY.format(moneyValue(raw));
    }

    public static float moneyValue(String raw) {
        if (raw == null) {
            return 0f;
        }
        try {
            return Float.parseFloat(raw.replace(",", "").trim());
        } catch (Exception e) {
            return 0f;
        }
    }

    public static boolean filled(String... values) {
        for (String value : values) {
            if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
                return false;
            }
        }
        return true;
    }

    public static boolean same(String left, String right) {
        return left != null && left.equals(right);
    }

    public static void fillSpinner(Spinner spinner, List<spinnerItems> items, JSONArray rows, String idKey, String nameKey) {
        fillLeading(spinner, items, rows, idKey, nameKey, null);
    }

    public static void fillSpinner(Spinner spinner, List<spinnerItems> items, JSONArray rows,
                                   String idKey, String nameKey, spinnerItems leading) {
        fillLeading(spinner, items, rows, idKey, nameKey, leading);
    }

    public static void fillNamed(Spinner spinner, List<spinnerItems> items, JSONArray rows,
                                 String idKey, String nameKey, String allLabel) {
        fillLeading(spinner, items, rows, idKey, nameKey,
                filled(allLabel) ? new spinnerItems("", allLabel) : null);
    }

    private static void fillLeading(Spinner spinner, List<spinnerItems> items, JSONArray rows,
                                    String idKey, String nameKey, spinnerItems leading) {
        items.clear();
        if (leading != null) {
            items.add(leading);
        }
        if (rows != null) {
            for (int i = 0; i < rows.length(); i++) {
                JSONObject json = rows.optJSONObject(i);
                if (json == null) {
                    continue;
                }
                String id = json.optString(idKey, "").trim();
                String name = json.optString(nameKey, "").trim();
                if (id.isEmpty() && name.isEmpty()) {
                    continue;
                }
                items.add(new spinnerItems(id, name));
            }
        }
        spinner.setAdapter(spinnerAdapter(spinner.getContext(), items));
    }

    public static void bindActionSpinner(Spinner spinner, List<String> actions, AdapterView.OnItemSelectedListener listener) {
        ArrayList<String> items = new ArrayList<>();
        items.add("Select action");
        if (actions != null) {
            for (String action : actions) {
                if (action != null && !action.trim().isEmpty() && !same(action, "Select action") && !same(action, "Select Action")) {
                    items.add(action);
                }
            }
        }
        spinner.setAdapter(stringAdapter(spinner.getContext(), items));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String action = String.valueOf(parent.getItemAtPosition(position));
                if (position == 0 || !filled(action) || action.toLowerCase(java.util.Locale.US).startsWith("select")) {
                    return;
                }
                listener.onItemSelected(parent, view, position, id);
                spinner.setSelection(0, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                listener.onNothingSelected(parent);
            }
        });
    }

    public static String selectedAction(AdapterView<?> parent) {
        Object item = parent.getSelectedItem();
        return item == null ? "" : String.valueOf(item);
    }

    public static String spinnerId(Spinner spinner) {
        spinnerItems selected = selected(spinner);
        return selected == null || selected.getId() == null ? "" : selected.getId().trim();
    }

    public static String spinnerName(Spinner spinner) {
        spinnerItems selected = selected(spinner);
        return selected == null || selected.getName() == null ? "" : selected.getName().trim();
    }

    public static String opt(JSONObject json, String... keys) {
        if (json == null || keys == null) {
            return "";
        }
        for (String key : keys) {
            String value = json.optString(key, "").trim();
            if (filled(value)) {
                return value;
            }
        }
        return "";
    }

    public static String firstWord(String text) {
        if (text == null) {
            return "";
        }
        String[] parts = text.trim().split("\\s+");
        return parts.length == 0 ? "" : parts[0];
    }

    public static boolean isDebitDescription(String description) {
        String word = firstWord(description);
        return "Invoice".equals(word) || "Bouncing".equals(word) || "Service".equals(word)
                || "Electricity".equals(word) || "Water".equals(word) || "Old".equals(word)
                || "Under".equals(word);
    }

    public static boolean isCreditDescription(String description) {
        if (description == null) {
            return false;
        }
        String word = firstWord(description);
        return "Payment".equals(word) || "Credit".equals(word) || description.startsWith("Credit Note")
                || "Over".equals(word) || "RemovedInvoice".equals(word);
    }

    public static boolean isFooterRow(HashMap<String, String> row) {
        return row != null && "1".equals(row.get("_footer"));
    }

    public static void markFooter(HashMap<String, String> row) {
        if (row != null) {
            row.put("_footer", "1");
        }
    }

    public static void bindPdfActions(Spinner spinner, AdapterView.OnItemSelectedListener listener) {
        bindActionSpinner(spinner, Arrays.asList("Download PDF", "Share"), listener);
    }

    public static boolean isDownloadAction(String action) {
        return action != null && action.toLowerCase(java.util.Locale.US).contains("download");
    }

    public static boolean isSharePdfAction(String action) {
        return action != null && action.toLowerCase(java.util.Locale.US).contains("share");
    }

    public static void handlePdfAction(Activity activity, String action, String url, String filename) {
        if (isDownloadAction(action)) {
            ReportPdf.run(activity, url, filename, false);
        } else if (isSharePdfAction(action)) {
            ReportPdf.run(activity, url, filename, true);
        }
    }

    public static String pdfName(String prefix, String from, String to) {
        String start = filled(from) ? from : "start";
        String end = filled(to) ? to : "end";
        return prefix + "_" + start + "_" + end + ".pdf";
    }

    public static String displayOrDash(String value) {
        return filled(value) ? value : "-";
    }

    public interface ItemPick {
        void onItem(spinnerItems item);
    }

    public static void bindChoices(Spinner spinner, String... choices) {
        spinner.setAdapter(stringAdapter(spinner.getContext(), choices));
    }

    public static void onItem(Spinner spinner, ItemPick pick) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerItems item = selected(parent);
                if (item != null) {
                    pick.onItem(item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public static void loadOwnerProperties(Activity activity, Spinner spinner, List<spinnerItems> items) {
        loadRows(activity, Config.getInstance().getSERVERURL() + "list_properties.php",
                "owner_id", SessionManager.get(activity).getOwnerId(),
                rows -> fillSpinner(spinner, items, rows, "property_code", "property_name"));
    }

    public static void loadUnits(Activity activity, String propertyCode, Spinner spinner, List<spinnerItems> items) {
        loadRows(activity, Config.getInstance().getSERVERURL() + "list_propertyunits.php",
                "property_code", propertyCode,
                rows -> fillSpinner(spinner, items, rows, "unit_code", "unit_name"));
    }

    public static void loadTenantsForUnit(Activity activity, String unitCode, Spinner spinner, List<spinnerItems> items) {
        loadRows(activity, Config.getInstance().getSERVERURL() + "list_tenantidname.php",
                "unit_code", unitCode,
                rows -> fillSpinner(spinner, items, rows, "tenant_identifier", "tenant_name"));
    }

    public static String text(EditText field) {
        return field == null || field.getText() == null ? "" : String.valueOf(field.getText()).trim();
    }

    public static String selectedText(Spinner spinner) {
        Object item = spinner == null ? null : spinner.getSelectedItem();
        return item == null ? "" : String.valueOf(item);
    }

    public static void submit(Activity activity, ProgressBar progress, View save, String endpoint,
                              String[] field, String[] data, String ok, String fail) {
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
        if (save != null) {
            save.setEnabled(false);
        }
        ApiClient.get().post(activity, Config.getInstance().getSERVERURL() + endpoint, field, data, new ApiClient.Callback() {
            @Override
            public void onSuccess(String result) {
                if (progress != null) {
                    progress.setVisibility(View.GONE);
                }
                if (save != null) {
                    save.setEnabled(true);
                }
                if (UiNotifier.jsonSuccess(result)) {
                    UiNotifier.snack(activity, UiNotifier.userMessage(result, ok));
                    activity.finish();
                } else {
                    UiNotifier.snack(activity, UiNotifier.userMessage(result, fail));
                }
            }

            @Override
            public void onError(String message) {
                if (progress != null) {
                    progress.setVisibility(View.GONE);
                }
                if (save != null) {
                    save.setEnabled(true);
                }
                UiNotifier.snack(activity, message);
            }
        });
    }
}
