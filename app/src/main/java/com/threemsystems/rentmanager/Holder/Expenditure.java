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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Expenditure extends AppCompatActivity {
    Spinner spnProperty, spnUnit, spnTenant, spnAction;
    ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>();
    ArrayList<HashMap<String, String>> expenditureList = new ArrayList<>();
    String startdate, enddate, ownerId;
    String propertycode = "";
    String unitcode = "";
    Config conf = Config.getInstance();
    ReportTableHost table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenditure);
        ScreenNav.bind(this);

        EditText dateFrom = findViewById(R.id.datefrm);
        EditText dateTo = findViewById(R.id.datetu);
        spnProperty = findViewById(R.id.spnprty);
        spnUnit = findViewById(R.id.spnunt);
        spnTenant = findViewById(R.id.spntnt);
        spnAction = findViewById(R.id.spnact);
        ownerId = SessionManager.get(this).getOwnerId();
        table = ReportTableHost.attach(this).setEmpty("No expenditure in this range.");
        table.setColumns(
                ReportColumn.center("#", "#", 48),
                ReportColumn.of("date", "Date", 100),
                ReportColumn.of("ref", "Ref.No", 100),
                ReportColumn.of("mm", "MM Code", 110),
                ReportColumn.of("payee", "Payee", 140),
                ReportColumn.of("mode", "Pay Mode", 100),
                ReportColumn.money("amount", "Amount", 100),
                ReportColumn.of("desc", "Desc", 160),
                ReportColumn.of("property", "Property", 130),
                ReportColumn.of("unit", "Unit", 90),
                ReportColumn.of("tenant", "Tenant", 140)
        );

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        enddate = DateUi.format(cal);
        dateTo.setText(enddate);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        startdate = DateUi.format(c);
        dateFrom.setText(startdate);

        DateUi.bindPicker(this, dateFrom, iso -> {
            startdate = iso;
            loadExpenditure();
        });
        DateUi.bindPicker(this, dateTo, iso -> {
            enddate = iso;
            loadExpenditure();
        });

        spnProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                propertycode = ReportSupport.spinnerId(spnProperty);
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
                loadTenants();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spnTenant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadExpenditure();
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

    private void loadUnits() {
        loadNamed(conf.getSERVERURL() + "list_propertyunits.php", propertycode, "property_code", "units");
    }

    private void loadTenants() {
        if (ReportSupport.filled(unitcode)) {
            loadNamed(conf.getSERVERURL() + "list_tenantidname.php", unitcode, "unit_code", "tenants");
        } else {
            loadNamed(conf.getSERVERURL() + "list_tenantidnamebyowner.php", ownerId, "owner_id", "tenants");
        }
    }

    private void loadNamed(String url, String value, String key, String kind) {
        ReportSupport.loadRows(this, url, key, value, rows -> {
            if ("properties".equals(kind)) {
                ReportSupport.fillNamed(spnProperty, propertyspinnerlist, rows,
                        "property_code", "property_name", "All properties");
            } else if ("units".equals(kind)) {
                ReportSupport.fillNamed(spnUnit, unitspinnerlist, rows,
                        "unit_code", "unit_name", "All units");
            } else {
                ReportSupport.fillNamed(spnTenant, tenantpinnerlist, rows,
                        "tenant_identifier", "tenant_name", "All tenants");
            }
        });
    }

    private void loadExpenditure() {
        if (!ReportSupport.filled(startdate, enddate)) {
            return;
        }
        StringRequest request = new FormRequest(conf.getSERVERURL() + "select_Expenditures.php",
                response -> {
                    ReportSupport.warnIfNotJson(Expenditure.this, response);
                    bindRows(ReportSupport.resultArray(response));
                },
                error -> VolleyErrors.show(Expenditure.this, error)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("ownerid", ownerId);
                map.put("startdate", startdate);
                map.put("enddate", enddate);
                map.put("pcode", propertycode == null ? "" : propertycode);
                map.put("ucode", unitcode == null ? "" : unitcode);
                map.put("id", tenantId());
                return map;
            }
        };
        ReportSupport.enqueue(this, request);
    }

    private void bindRows(JSONArray rows) {
        float total = 0f;
        expenditureList.clear();
        for (int i = 0; i < rows.length(); i++) {
            JSONObject json = rows.optJSONObject(i);
            if (json == null) {
                continue;
            }
            total += ReportSupport.moneyValue(json.optString("payment_amount"));
            HashMap<String, String> item = new HashMap<>();
            item.put("#", String.valueOf(i + 1));
            item.put("date", json.optString("payment_date"));
            item.put("ref", ReportSupport.displayOrDash(json.optString("pay_refno")));
            item.put("mm", ReportSupport.displayOrDash(json.optString("mobilemoneycode")));
            item.put("payee", json.optString("payeeExp"));
            item.put("mode", ReportSupport.displayOrDash(json.optString("pay_mode")));
            item.put("amount", ReportSupport.money(json.optString("payment_amount")));
            item.put("desc", ReportSupport.displayOrDash(json.optString("expense_desc")));
            item.put("property", ReportSupport.displayOrDash(json.optString("property_name")));
            item.put("unit", ReportSupport.displayOrDash(ReportSupport.opt(json, "unit_name", "unit_code")));
            item.put("tenant", ReportSupport.displayOrDash(json.optString("tenant_name")));
            expenditureList.add(item);
        }
        if (!expenditureList.isEmpty()) {
            HashMap<String, String> footer = new HashMap<>();
            footer.put("#", "");
            footer.put("date", "");
            footer.put("ref", "");
            footer.put("mm", "");
            footer.put("payee", "TOTAL");
            footer.put("mode", "");
            footer.put("amount", ReportSupport.money(String.valueOf(total)));
            footer.put("desc", "");
            footer.put("property", "");
            footer.put("unit", "");
            footer.put("tenant", "");
            ReportSupport.markFooter(footer);
            expenditureList.add(footer);
        }
        table.setRows(expenditureList);
    }

    private void handleAction(String action) {
        if (expenditureList.isEmpty()) {
            UiNotifier.snack(this, "There are no records to print.");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("tenantid", tenantId());
        params.put("startDate", startdate);
        params.put("endDate", enddate);
        params.put("pcode", propertycode == null ? "" : propertycode);
        params.put("ucode", unitcode == null ? "" : unitcode);
        params.put("oid", ownerId);
        ReportSupport.handlePdfAction(
                this,
                action,
                ReportSupport.withQuery(conf.getSERVERURL() + "printExpenses.php", params),
                ReportSupport.pdfName("expenditure", startdate, enddate)
        );
    }
}
