package com.threemsystems.rentmanager.Holder;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
import com.threemsystems.rentmanager.Config;
import com.threemsystems.rentmanager.FormRequest;
import com.threemsystems.rentmanager.R;
import com.threemsystems.rentmanager.ReportColumn;
import com.threemsystems.rentmanager.ReportSupport;
import com.threemsystems.rentmanager.ReportTableHost;
import com.threemsystems.rentmanager.ScreenNav;
import com.threemsystems.rentmanager.SessionManager;
import com.threemsystems.rentmanager.VolleyErrors;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Property extends AppCompatActivity {
    private final ArrayList<HashMap<String, String>> propertyList = new ArrayList<>();
    private Spinner spnProperty;
    private ReportTableHost table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);
        ScreenNav.bind(this);
        spnProperty = findViewById(R.id.spinnerProperty);
        table = ReportTableHost.attach(this)
                .setEmpty("No properties yet. Choose a type or add one from Data entry.");
        table.setColumns(
                ReportColumn.center("#", "#", 48),
                ReportColumn.of("owner_id", "Owner ID", 110),
                ReportColumn.of("property_name", "Property Name", 160),
                ReportColumn.of("short_name", "Short Name", 110),
                ReportColumn.of("description", "Description", 150),
                ReportColumn.of("payment", "Payment Details", 180),
                ReportColumn.of("contact", "Contact", 110),
                ReportColumn.of("status", "Status", 90)
        );

        spnProperty.setAdapter(ReportSupport.stringAdapter(this, new String[]{"All", "Cooperate", "Individual"}));
        spnProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadProperties();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ReportSupport.bindPdfActions(findViewById(R.id.spinneraction), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleAction(ReportSupport.selectedAction(parent));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        loadProperties();
    }

    private String selectedType() {
        return spnProperty.getSelectedItem() == null ? "All" : String.valueOf(spnProperty.getSelectedItem());
    }

    private void loadProperties() {
        fetchProperties("All".equals(selectedType()) ? "" : selectedType(), this::bindRows);
    }

    private interface RowsCallback {
        void onRows(JSONArray rows);
    }

    private void fetchProperties(String type, RowsCallback callback) {
        String idNo = SessionManager.get(this).getOwnerId();
        ScreenNav.setBusy(this, true);
        StringRequest request = new FormRequest(Config.getInstance().getSERVERURL() + "select_properties.php",
                response -> {
                    ScreenNav.setBusy(Property.this, false);
                    ReportSupport.warnIfNotJson(Property.this, response);
                    callback.onRows(ReportSupport.resultArray(response));
                },
                error -> {
                    ScreenNav.setBusy(Property.this, false);
                    VolleyErrors.show(Property.this, error);
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("owner_id", idNo);
                map.put("p_type", type == null ? "" : type);
                return map;
            }
        };
        ReportSupport.enqueue(this, request);
    }

    private void bindRows(JSONArray rows) {
        propertyList.clear();
        int n = 0;
        for (int i = 0; i < rows.length(); i++) {
            JSONObject json = rows.optJSONObject(i);
            if (json == null) {
                continue;
            }
            String name = json.optString("property_name", "").trim();
            String code = json.optString("property_code", "").trim();
            if (name.isEmpty() && code.isEmpty()) {
                continue;
            }
            HashMap<String, String> item = new HashMap<>();
            item.put("#", String.valueOf(++n));
            item.put("owner_id", ReportSupport.displayOrDash(json.optString("owner_identifier")));
            item.put("property_name", ReportSupport.displayOrDash(name));
            item.put("short_name", ReportSupport.displayOrDash(json.optString("short_name")));
            item.put("description", ReportSupport.displayOrDash(json.optString("property_desc")));
            item.put("payment", ReportSupport.displayOrDash(json.optString("paymentChannel")));
            item.put("contact", ReportSupport.displayOrDash(json.optString("property_tel")));
            item.put("status", ReportSupport.displayOrDash(json.optString("property_status")));
            propertyList.add(item);
        }
        table.setRows(propertyList);
    }

    private void handleAction(String action) {
        String idNo = SessionManager.get(this).getOwnerId();
        Map<String, String> params = new HashMap<>();
        params.put("ownerid", idNo);
        params.put("property_type", "All".equals(selectedType()) ? "" : selectedType());
        ReportSupport.handlePdfAction(
                this,
                action,
                ReportSupport.withQuery(Config.getInstance().getSERVERURL() + "printProperties.php", params),
                "properties.pdf"
        );
    }
}
