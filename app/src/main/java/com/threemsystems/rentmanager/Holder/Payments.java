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

public class Payments extends AppCompatActivity {
    Spinner spnProperty, spnUnit, spnTenant, spnAction;
    ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>();
    ArrayList<HashMap<String, String>> paymentList = new ArrayList<>();
    String startdate, enddate, ownerId;
    String propertycode = "";
    String unitcode = "";
    Config conf = Config.getInstance();
    ReportTableHost table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        ScreenNav.bind(this);

        EditText dateFrom = findViewById(R.id.datefrm);
        EditText dateTo = findViewById(R.id.datetu);
        spnProperty = findViewById(R.id.spnprty);
        spnUnit = findViewById(R.id.spnunt);
        spnTenant = findViewById(R.id.spntnt);
        spnAction = findViewById(R.id.spnact);
        ownerId = SessionManager.get(this).getOwnerId();
        table = ReportTableHost.attach(this).setEmpty("No payments in this range.");
        table.setColumns(
                ReportColumn.center("#", "#", 48),
                ReportColumn.of("date", "Date", 100),
                ReportColumn.of("mpesa", "M-Pesa Code", 120),
                ReportColumn.of("ref", "Ref.No", 100),
                ReportColumn.of("id_no", "ID No.", 110),
                ReportColumn.of("property", "Property", 130),
                ReportColumn.of("unit", "Unit", 90),
                ReportColumn.of("tenant", "Tenant", 150),
                ReportColumn.of("mode", "Pay Mode", 100),
                ReportColumn.money("amount", "Amount", 100),
                ReportColumn.of("validity", "Validity", 90)
        );
        table.setListener((position, row) -> {
        });

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
            loadPayments();
        });
        DateUi.bindPicker(this, dateTo, iso -> {
            enddate = iso;
            loadPayments();
        });

        spnProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerItems selected = ReportSupport.selected(parent);
                if (selected == null) {
                    return;
                }
                propertycode = selected.getId();
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
                spinnerItems selected = ReportSupport.selected(parent);
                if (selected == null) {
                    return;
                }
                unitcode = selected.getId();
                loadTenants();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spnTenant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadPayments();
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

        getspinnerData(conf.getSERVERURL() + "list_properties.php", ownerId, "owner_id", "properties");
    }

    private String tenantId() {
        return ReportSupport.spinnerId(spnTenant);
    }

    private void loadUnits() {
        getspinnerData(conf.getSERVERURL() + "list_propertyunits.php", propertycode, "property_code", "units");
    }

    private void loadTenants() {
        if (ReportSupport.filled(unitcode)) {
            getspinnerData(conf.getSERVERURL() + "list_tenantidname.php", unitcode, "unit_code", "tenants");
        } else {
            getspinnerData(conf.getSERVERURL() + "list_tenantidnamebyowner.php", ownerId, "owner_id", "tenants");
        }
    }

    private void loadPayments() {
        if (!ReportSupport.filled(startdate, enddate)) {
            return;
        }
        StringRequest request = new FormRequest(conf.getSERVERURL() + "select_tenantpayments.php",
                response -> {
                    ReportSupport.warnIfNotJson(Payments.this, response);
                    bindPayments(ReportSupport.resultArray(response));
                },
                error -> VolleyErrors.show(Payments.this, error)) {
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

    private void getspinnerData(String url, String fieldcode, String fieldname, String kind) {
        ReportSupport.loadRows(this, url, fieldname, fieldcode, rows -> {
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

    private void bindPayments(JSONArray rows) {
        float total = 0f;
        paymentList.clear();
        for (int i = 0; i < rows.length(); i++) {
            JSONObject json = rows.optJSONObject(i);
            if (json == null) {
                continue;
            }
            boolean validPay = !"Invalid".equalsIgnoreCase(json.optString("validity"));
            if (validPay) {
                total += ReportSupport.moneyValue(json.optString("payment_amount"));
            }
            HashMap<String, String> item = new HashMap<>();
            item.put("#", String.valueOf(i + 1));
            item.put("date", json.optString("payment_date"));
            item.put("mpesa", ReportSupport.displayOrDash(json.optString("mpesacode")));
            item.put("ref", ReportSupport.displayOrDash(json.optString("pay_refno")));
            item.put("id_no", ReportSupport.displayOrDash(json.optString("tenant_identifier")));
            item.put("property", ReportSupport.displayOrDash(json.optString("property_name")));
            item.put("unit", ReportSupport.displayOrDash(ReportSupport.opt(json, "unit_name", "unit_code")));
            item.put("tenant", json.optString("tenant_name"));
            item.put("mode", ReportSupport.displayOrDash(json.optString("pay_mode")));
            item.put("amount", ReportSupport.money(json.optString("payment_amount")));
            item.put("validity", ReportSupport.displayOrDash(json.optString("validity")));
            item.put("payment_id", json.optString("payment_id"));
            paymentList.add(item);
        }
        if (!paymentList.isEmpty()) {
            HashMap<String, String> totalRow = new HashMap<>();
            totalRow.put("#", "");
            totalRow.put("date", "");
            totalRow.put("mpesa", "");
            totalRow.put("ref", "");
            totalRow.put("id_no", "");
            totalRow.put("property", "");
            totalRow.put("unit", "");
            totalRow.put("tenant", "TOTAL");
            totalRow.put("mode", "");
            totalRow.put("amount", ReportSupport.money(String.valueOf(total)));
            totalRow.put("validity", "");
            ReportSupport.markFooter(totalRow);
            paymentList.add(totalRow);
        }
        table.setRows(paymentList);
    }

    private void handleAction(String action) {
        if (paymentList.isEmpty()) {
            UiNotifier.snack(this, "There are no records to print.");
            return;
        }
        HashMap<String, String> selectedRow = table.selectedRow();
        String url;
        String filename;
        if (selectedRow != null && ReportSupport.filled(selectedRow.get("payment_id"))) {
            if ("Invalid".equalsIgnoreCase(selectedRow.get("validity"))) {
                UiNotifier.snack(this, "The selected record is invalid.");
                return;
            }
            Map<String, String> params = new HashMap<>();
            params.put("paymentid", selectedRow.get("payment_id"));
            params.put("ownerid", ownerId);
            url = ReportSupport.withQuery(conf.getSERVERURL() + "printPaymentReceipt.php", params);
            filename = "receipt_" + selectedRow.get("payment_id") + ".pdf";
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("tenantid", tenantId());
            params.put("startDate", startdate);
            params.put("endDate", enddate);
            params.put("pcode", propertycode == null ? "" : propertycode);
            params.put("ucode", unitcode == null ? "" : unitcode);
            params.put("ownerid", ownerId);
            url = ReportSupport.withQuery(conf.getSERVERURL() + "printTenantPayments.php", params);
            filename = ReportSupport.pdfName("payments", startdate, enddate);
        }
        ReportSupport.handlePdfAction(this, action, url, filename);
    }
}
