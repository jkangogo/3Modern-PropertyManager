package com.threemsystems.rentmanager.Holder;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
import com.threemsystems.rentmanager.Config;
import com.threemsystems.rentmanager.DateUi;
import com.threemsystems.rentmanager.FormRequest;
import com.threemsystems.rentmanager.R;
import com.threemsystems.rentmanager.ReportColumn;
import com.threemsystems.rentmanager.ReportSupport;
import com.threemsystems.rentmanager.ReportTableHost;
import com.threemsystems.rentmanager.ScreenNav;
import com.threemsystems.rentmanager.SessionManager;
import com.threemsystems.rentmanager.UiNotifier;
import com.threemsystems.rentmanager.VolleyErrors;
import com.threemsystems.rentmanager.spinnerItems;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tenancy extends AppCompatActivity {
    Spinner spnProperty, spnUnit, spnTenant, spnAction;
    ArrayList<spinnerItems> spinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> tenantspinnerlist = new ArrayList<>();
    ArrayList<HashMap<String, String>> tenantList = new ArrayList<>();
    String pcode = "";
    String unitcode = "";
    String ownerId;
    String startdate = "";
    String enddate = "";
    Config conf = Config.getInstance();
    ReportTableHost table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenancy);
        ScreenNav.bind(this);

        EditText dateStart = findViewById(R.id.datestart);
        EditText dateEnd = findViewById(R.id.dateend);
        spnProperty = findViewById(R.id.spnpr);
        spnUnit = findViewById(R.id.spnunitt);
        spnTenant = findViewById(R.id.spntenant);
        spnAction = findViewById(R.id.spnaction);
        ownerId = SessionManager.get(this).getOwnerId();
        table = ReportTableHost.attach(this).setEmpty("No tenants match these filters.");
        table.setColumns(
                ReportColumn.center("#", "#", 48),
                ReportColumn.of("id_no", "ID No.", 110),
                ReportColumn.of("names", "Names", 150),
                ReportColumn.of("tel", "Tel.", 110),
                ReportColumn.of("property", "Property", 140),
                ReportColumn.of("unit", "Unit", 90),
                ReportColumn.of("vehicle", "Vehicle", 100),
                ReportColumn.of("from", "From", 100),
                ReportColumn.of("to", "To", 100),
                ReportColumn.money("rent", "Rent", 90),
                ReportColumn.money("deposit", "Deposit", 90),
                ReportColumn.of("status", "Status", 90)
        );

        dateStart.setHint("Start date");
        dateEnd.setHint("End date");
        DateUi.bindPicker(this, dateStart, iso -> {
            startdate = iso;
            loadTenantsList();
        });
        DateUi.bindPicker(this, dateEnd, iso -> {
            enddate = iso;
            loadTenantsList();
        });

        spnProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pcode = ReportSupport.spinnerId(spnProperty);
                unitcode = "";
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
                loadTenantChoices();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spnTenant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadTenantsList();
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

        loadNamed(conf.getSERVERURL() + "list_properties.php", ownerId, "owner_id", "properties");
    }

    private String tenantId() {
        return ReportSupport.spinnerId(spnTenant);
    }

    private String dateFlag() {
        return ReportSupport.filled(startdate, enddate) ? "YES" : "";
    }

    private void loadUnits() {
        loadNamed(conf.getSERVERURL() + "list_propertyunits.php", pcode, "property_code", "units");
    }

    private void loadTenantChoices() {
        if (ReportSupport.filled(unitcode)) {
            loadNamed(conf.getSERVERURL() + "list_tenantidname.php", unitcode, "unit_code", "tenants");
        } else {
            loadNamed(conf.getSERVERURL() + "list_tenantidnamebyowner.php", ownerId, "owner_id", "tenants");
        }
    }

    private void loadNamed(String url, String value, String key, String kind) {
        ReportSupport.loadRows(this, url, key, value, rows -> {
            if ("properties".equals(kind)) {
                ReportSupport.fillNamed(spnProperty, spinnerlist, rows,
                        "property_code", "property_name", "All properties");
            } else if ("units".equals(kind)) {
                ReportSupport.fillNamed(spnUnit, unitspinnerlist, rows,
                        "unit_code", "unit_name", "All units");
            } else {
                ReportSupport.fillNamed(spnTenant, tenantspinnerlist, rows,
                        "tenant_identifier", "tenant_name", "All tenants");
            }
        });
    }

    private void loadTenantsList() {
        StringRequest request = new FormRequest(conf.getSERVERURL() + "select_tenants.php",
                response -> {
                    ReportSupport.warnIfNotJson(Tenancy.this, response);
                    bindRows(ReportSupport.resultArray(response));
                },
                error -> VolleyErrors.show(Tenancy.this, error)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("tenant_identifier", tenantId());
                map.put("tenant_name", "");
                map.put("owner_id", ownerId);
                map.put("property", pcode == null ? "" : pcode);
                map.put("unit", unitcode == null ? "" : unitcode);
                map.put("startdate", startdate == null ? "" : startdate);
                map.put("enddate", enddate == null ? "" : enddate);
                map.put("dateflag", dateFlag());
                return map;
            }
        };
        ReportSupport.enqueue(this, request);
    }

    private void bindRows(JSONArray rows) {
        tenantList.clear();
        for (int i = 0; i < rows.length(); i++) {
            JSONObject json = rows.optJSONObject(i);
            if (json == null) {
                continue;
            }
            HashMap<String, String> item = new HashMap<>();
            item.put("#", String.valueOf(i + 1));
            item.put("id_no", ReportSupport.displayOrDash(json.optString("tenant_identifier")));
            item.put("names", json.optString("tenant_name"));
            item.put("tel", ReportSupport.displayOrDash(json.optString("tenant_tel")));
            item.put("property", ReportSupport.displayOrDash(json.optString("property_name")));
            item.put("unit", ReportSupport.displayOrDash(ReportSupport.opt(json, "unit_name", "unit_code")));
            item.put("vehicle", ReportSupport.displayOrDash(json.optString("vehicle_regno")));
            item.put("from", ReportSupport.displayOrDash(json.optString("date_from")));
            item.put("to", ReportSupport.displayOrDash(json.optString("date_to")));
            item.put("rent", ReportSupport.money(json.optString("rent_payable")));
            item.put("deposit", ReportSupport.money(json.optString("rent_deposit_amount")));
            item.put("status", ReportSupport.displayOrDash(json.optString("active")));
            tenantList.add(item);
        }
        table.setRows(tenantList);
    }

    private void handleAction(String action) {
        if (!ReportSupport.filled(pcode) && !ReportSupport.filled(unitcode) && !ReportSupport.filled(tenantId())) {
            UiNotifier.snack(this, "Select a property, unit, or tenant first.");
            return;
        }
        String reportype = "bypropertycode";
        if (ReportSupport.filled(tenantId())) {
            reportype = "bytenantid";
        } else if (ReportSupport.filled(unitcode)) {
            reportype = "bypropertyunits";
        }
        Map<String, String> params = new HashMap<>();
        params.put("pcode", pcode == null ? "" : pcode);
        params.put("unitcode", unitcode == null ? "" : unitcode);
        params.put("startdate", startdate == null ? "" : startdate);
        params.put("enddate", enddate == null ? "" : enddate);
        params.put("tenantid", tenantId());
        params.put("reportype", reportype);
        ReportSupport.handlePdfAction(
                this,
                action,
                ReportSupport.withQuery(conf.getSERVERURL() + "printTenancyHistory.php", params),
                "tenancy.pdf"
        );
    }
}
