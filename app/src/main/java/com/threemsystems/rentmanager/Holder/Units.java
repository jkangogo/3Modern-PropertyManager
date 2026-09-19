package com.threemsystems.rentmanager.Holder;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.threemsystems.rentmanager.spinnerItems;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Units extends AppCompatActivity {
    String pcode = "";
    String unitcode = "";
    String ownerId;
    Spinner spnProperty, spnStatus, spnUnit, spnAction;
    ArrayList<spinnerItems> spinnerlist = new ArrayList<>();
    ArrayList<HashMap<String, String>> unitList = new ArrayList<>();
    ArrayList<HashMap<String, String>> allUnits = new ArrayList<>();
    ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    String unitNameQuery = "";
    Config conf = Config.getInstance();
    ReportTableHost table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_units);
        ScreenNav.bind(this);

        EditText unitName = findViewById(R.id.ettunitname);
        unitName.setHint("Unit name");
        unitName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                unitNameQuery = s == null ? "" : s.toString();
                showFilteredUnits();
            }
        });
        spnProperty = findViewById(R.id.spnp);
        spnStatus = findViewById(R.id.spnstatus);
        spnUnit = findViewById(R.id.spnunit);
        spnAction = findViewById(R.id.spnact);
        ownerId = SessionManager.get(this).getOwnerId();
        table = ReportTableHost.attach(this).setEmpty("No units match these filters.");
        table.setColumns(
                ReportColumn.center("#", "#", 48),
                ReportColumn.of("property", "Property", 140),
                ReportColumn.of("unit", "Unit", 140),
                ReportColumn.of("status", "Status", 100),
                ReportColumn.money("deposit", "Deposit", 100),
                ReportColumn.money("rent", "Rent", 100),
                ReportColumn.of("elect", "Elect. Meter", 120),
                ReportColumn.of("water", "Water Meter", 120),
                ReportColumn.money("service", "Service Fee", 100)
        );

        spnStatus.setAdapter(ReportSupport.stringAdapter(this, new String[]{"All", "Occupied", "Vacant"}));
        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadUnits();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spnUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                unitcode = ReportSupport.spinnerId(spnUnit);
                loadUnits();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spnProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pcode = ReportSupport.spinnerId(spnProperty);
                unitcode = "";
                if (ReportSupport.filled(pcode)) {
                    loadUnitChoices(pcode);
                }
                loadUnits();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ReportSupport.bindPdfActions(spnAction, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleAction(ReportSupport.selectedAction(parent));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        loadProperties();
        loadUnits();
    }

    private String statusParam() {
        String status = spnStatus.getSelectedItem() == null ? "All" : String.valueOf(spnStatus.getSelectedItem());
        return "All".equals(status) ? "allstatus" : status;
    }

    private void loadProperties() {
        StringRequest request = new FormRequest(conf.getSERVERURL() + "list_properties.php",
                response -> ReportSupport.fillNamed(spnProperty, spinnerlist,
                        ReportSupport.resultArray(response),
                        "property_code", "property_name", "All properties"),
                error -> VolleyErrors.show(Units.this, error)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("owner_id", ownerId);
                return map;
            }
        };
        ReportSupport.enqueue(this, request);
    }

    private void loadUnitChoices(String propertyCode) {
        StringRequest request = new FormRequest(conf.getSERVERURL() + "list_propertyunits.php",
                response -> ReportSupport.fillNamed(spnUnit, unitspinnerlist,
                        ReportSupport.resultArray(response),
                        "unit_code", "unit_name", "All units"),
                error -> VolleyErrors.show(Units.this, error)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("property_code", propertyCode);
                return map;
            }
        };
        ReportSupport.enqueue(this, request);
    }

    private void loadUnits() {
        ScreenNav.setBusy(this, true);
        StringRequest request = new FormRequest(conf.getSERVERURL() + "select_propertyunits.php",
                response -> {
                    ScreenNav.setBusy(Units.this, false);
                    ReportSupport.warnIfNotJson(Units.this, response);
                    bindRows(ReportSupport.resultArray(response));
                },
                error -> {
                    ScreenNav.setBusy(Units.this, false);
                    VolleyErrors.show(Units.this, error);
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("property_code", pcode == null ? "" : pcode);
                map.put("owner_id", ownerId);
                map.put("u_status", statusParam());
                map.put("u_code", ReportSupport.filled(unitcode) ? unitcode : "allunits");
                return map;
            }
        };
        ReportSupport.enqueue(this, request);
    }

    private void bindRows(JSONArray rows) {
        allUnits.clear();
        for (int i = 0; i < rows.length(); i++) {
            JSONObject json = rows.optJSONObject(i);
            if (json == null) {
                continue;
            }
            HashMap<String, String> item = new HashMap<>();
            item.put("property", ReportSupport.displayOrDash(json.optString("property_name")));
            item.put("unit", json.optString("unit_name"));
            item.put("status", json.optString("status"));
            item.put("deposit", ReportSupport.money(json.optString("unit_rentdeposit_amount")));
            item.put("rent", ReportSupport.money(json.optString("unit_rent_amount")));
            item.put("elect", ReportSupport.displayOrDash(json.optString("electricity_meter")));
            item.put("water", ReportSupport.displayOrDash(json.optString("water_meter")));
            item.put("service", ReportSupport.money(json.optString("service_fee")));
            allUnits.add(item);
        }
        showFilteredUnits();
    }

    private void showFilteredUnits() {
        unitList.clear();
        String query = unitNameQuery == null ? "" : unitNameQuery.trim().toLowerCase();
        int n = 0;
        for (HashMap<String, String> item : allUnits) {
            String name = item.get("unit");
            if (query.isEmpty() || (name != null && name.toLowerCase().contains(query))) {
                HashMap<String, String> row = new HashMap<>(item);
                row.put("#", String.valueOf(++n));
                unitList.add(row);
            }
        }
        table.setRows(unitList);
    }

    private void handleAction(String action) {
        Map<String, String> params = new HashMap<>();
        params.put("ownerid", ownerId);
        params.put("pcode", pcode == null ? "" : pcode);
        params.put("ucode", unitcode == null ? "" : unitcode);
        params.put("u_status", statusParam());
        ReportSupport.handlePdfAction(
                this,
                action,
                ReportSupport.withQuery(conf.getSERVERURL() + "printPropertyUnits.php", params),
                "units.pdf"
        );
    }
}
