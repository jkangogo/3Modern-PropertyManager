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

public class Invoices extends AppCompatActivity {
    Spinner spnTenant, spnAction;
    ArrayList<spinnerItems> tenantspinnerlist = new ArrayList<>();
    ArrayList<HashMap<String, String>> tenantInvoiceList = new ArrayList<>();
    String startdate, enddate, ownerId;
    ReportTableHost table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);
        ScreenNav.bind(this);
        EditText dateFrom = findViewById(R.id.datefrom);
        EditText dateTo = findViewById(R.id.dateto);
        spnTenant = findViewById(R.id.spntenant);
        spnAction = findViewById(R.id.spnactionn);
        ownerId = SessionManager.get(this).getOwnerId();
        table = ReportTableHost.attach(this).setEmpty("No invoices in this range.");
        table.setColumns(
                ReportColumn.center("#", "#", 48),
                ReportColumn.of("month", "Month", 100),
                ReportColumn.of("id_no", "ID No.", 110),
                ReportColumn.of("names", "Names", 150),
                ReportColumn.of("property", "Property", 120),
                ReportColumn.of("unit", "Unit", 100),
                ReportColumn.money("rent", "Rent", 90),
                ReportColumn.money("services", "Services", 90),
                ReportColumn.money("invoiced", "Invoiced", 100)
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
            loadInvoices();
        });
        DateUi.bindPicker(this, dateTo, iso -> {
            enddate = iso;
            loadInvoices();
        });
        spnTenant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadInvoices();
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

        Config conf = Config.getInstance();
        StringRequest tenants = new FormRequest(conf.getSERVERURL() + "list_tenantidnamebyowner.php",
                response -> ReportSupport.fillNamed(spnTenant, tenantspinnerlist,
                        ReportSupport.resultArray(response),
                        "tenant_identifier", "tenant_name", "All tenants"),
                error -> VolleyErrors.show(Invoices.this, error)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("owner_id", ownerId);
                return map;
            }
        };
        ReportSupport.enqueue(this, tenants);
        loadInvoices();
    }

    private String tenantId() {
        return ReportSupport.spinnerId(spnTenant);
    }

    private void loadInvoices() {
        if (!ReportSupport.filled(startdate, enddate)) {
            return;
        }
        Config conf = Config.getInstance();
        StringRequest request = new FormRequest(conf.getSERVERURL() + "select_tenantinvoices.php",
                response -> {
                    ReportSupport.warnIfNotJson(Invoices.this, response);
                    bindInvoices(ReportSupport.resultArray(response));
                },
                error -> VolleyErrors.show(Invoices.this, error)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("tenant_identifier", tenantId());
                map.put("tenant_name", "");
                map.put("ownerid", ownerId);
                map.put("startdate", startdate);
                map.put("enddate", enddate);
                return map;
            }
        };
        ReportSupport.enqueue(this, request);
    }

    private void bindInvoices(JSONArray rows) {
        tenantInvoiceList.clear();
        float total = 0f;
        for (int i = 0; i < rows.length(); i++) {
            JSONObject json = rows.optJSONObject(i);
            if (json == null) {
                continue;
            }
            String invoiced = ReportSupport.opt(json, "totalamount_invoiced", "rent_payable");
            total += ReportSupport.moneyValue(invoiced);
            HashMap<String, String> item = new HashMap<>();
            item.put("#", String.valueOf(i + 1));
            item.put("month", json.optString("invoice_month"));
            item.put("id_no", ReportSupport.displayOrDash(json.optString("tenant_identifier")));
            item.put("names", json.optString("tenant_name"));
            item.put("property", ReportSupport.displayOrDash(json.optString("property_code")));
            item.put("unit", ReportSupport.displayOrDash(ReportSupport.opt(json, "unit_name", "unit_code")));
            item.put("rent", ReportSupport.money(json.optString("rent_payable")));
            item.put("services", ReportSupport.money(json.optString("servicefee")));
            item.put("invoiced", ReportSupport.money(invoiced));
            tenantInvoiceList.add(item);
        }
        if (!tenantInvoiceList.isEmpty()) {
            HashMap<String, String> footer = new HashMap<>();
            footer.put("#", "");
            footer.put("month", "");
            footer.put("id_no", "");
            footer.put("names", "TOTAL");
            footer.put("property", "");
            footer.put("unit", "");
            footer.put("rent", "");
            footer.put("services", "");
            footer.put("invoiced", ReportSupport.money(String.valueOf(total)));
            ReportSupport.markFooter(footer);
            tenantInvoiceList.add(footer);
        }
        table.setRows(tenantInvoiceList);
    }

    private void handleAction(String action) {
        if (tenantInvoiceList.isEmpty()) {
            UiNotifier.snack(this, "Please load at least one invoice.");
            return;
        }
        if (!ReportSupport.filled(tenantId())) {
            UiNotifier.snack(this, "Select a tenant first.");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("tenantid", tenantId());
        params.put("startdate", startdate);
        params.put("enddate", enddate);
        Config conf = Config.getInstance();
        ReportSupport.handlePdfAction(
                this,
                action,
                ReportSupport.withQuery(conf.getSERVERURL() + "printTenantsInvoiceItems.php", params),
                ReportSupport.pdfName("invoices", startdate, enddate)
        );
    }
}
