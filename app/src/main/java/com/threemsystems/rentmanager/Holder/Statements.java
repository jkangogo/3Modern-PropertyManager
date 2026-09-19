package com.threemsystems.rentmanager.Holder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
import com.threemsystems.rentmanager.Config;
import com.threemsystems.rentmanager.DateUi;
import com.threemsystems.rentmanager.FormRequest;
import com.threemsystems.rentmanager.JounalEntry;
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

public class Statements extends AppCompatActivity {
    private static final String[] STATEMENT_TYPES = {"All", "Payments", "Unpaid", "Invoices", "Credit Note", "Bounced"};
    private static final String[] DEBT_TYPES = {"Active Debts", "Inactive Debts", "All Debts"};

    Spinner spnProperty, spnTenant, spnAction, spnType;
    View tenantFilter;
    TextView btnChoiceStatements, btnChoiceDebts, txtScope;
    ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>();
    ArrayList<HashMap<String, String>> statementList = new ArrayList<>();
    String startdate = "";
    String enddate = "";
    String propertycode = "";
    String ownerId;
    String currentChoice = "Statements";
    Config conf = Config.getInstance();
    ReportTableHost table;
    boolean applyingChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statements);
        ScreenNav.bind(this);

        EditText dateStart = findViewById(R.id.datestt);
        EditText dateEnd = findViewById(R.id.datend);
        spnProperty = findViewById(R.id.spnprty);
        spnTenant = findViewById(R.id.spntt);
        spnType = findViewById(R.id.spntype);
        spnAction = findViewById(R.id.spnacton);
        tenantFilter = findViewById(R.id.tenantFilter);
        btnChoiceStatements = findViewById(R.id.btnChoiceStatements);
        btnChoiceDebts = findViewById(R.id.btnChoiceDebts);
        txtScope = findViewById(R.id.txtScope);
        table = ReportTableHost.attach(this).setEmpty("Select a tenant to load a statement.");
        findViewById(R.id.btnJournal).setOnClickListener(v ->
                startActivity(new Intent(Statements.this, JounalEntry.class)));
        ownerId = SessionManager.get(this).getOwnerId();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        enddate = DateUi.format(cal);
        dateEnd.setText(enddate);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        startdate = DateUi.format(c);
        dateStart.setText(startdate);

        DateUi.bindPicker(this, dateStart, iso -> {
            startdate = iso;
            loadGrid();
        });
        DateUi.bindPicker(this, dateEnd, iso -> {
            enddate = iso;
            loadGrid();
        });

        btnChoiceStatements.setOnClickListener(v -> setChoice("Statements"));
        btnChoiceDebts.setOnClickListener(v -> setChoice("Debt Summary"));

        AdapterView.OnItemSelectedListener reload = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent == spnProperty) {
                    propertycode = ReportSupport.spinnerId(spnProperty);
                    loadTenants();
                }
                if (!applyingChoice) {
                    loadGrid();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spnProperty.setOnItemSelectedListener(reload);
        spnTenant.setOnItemSelectedListener(reload);
        spnType.setOnItemSelectedListener(reload);

        ReportSupport.bindPdfActions(spnAction, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleAction(ReportSupport.selectedAction(parent));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        applyingChoice = true;
        applyChoiceUi();
        applyingChoice = false;
        getspinnerData(conf.getSERVERURL() + "list_properties.php", ownerId, "owner_id", "properties");
    }

    private void setChoice(String choice) {
        if (ReportSupport.same(currentChoice, choice)) {
            return;
        }
        currentChoice = choice;
        applyingChoice = true;
        applyChoiceUi();
        applyingChoice = false;
        loadGrid();
    }

    private String tenantId() {
        return ReportSupport.spinnerId(spnTenant);
    }

    private boolean isStatementsChoice() {
        return "Statements".equals(currentChoice);
    }

    private boolean isDebtSummary() {
        return "Debt Summary".equals(currentChoice);
    }

    private String selectValue() {
        return spnType.getSelectedItem() == null ? "" : String.valueOf(spnType.getSelectedItem());
    }

    private boolean isUnpaid() {
        return isStatementsChoice() && "Unpaid".equals(selectValue());
    }

    private void applyChoiceUi() {
        boolean statements = isStatementsChoice();
        boolean debts = isDebtSummary();
        btnChoiceStatements.setSelected(statements);
        btnChoiceDebts.setSelected(debts);
        tenantFilter.setVisibility(statements ? View.VISIBLE : View.GONE);
        if (statements) {
            spnType.setAdapter(ReportSupport.stringAdapter(this, STATEMENT_TYPES));
        } else if (debts) {
            spnType.setAdapter(ReportSupport.stringAdapter(this, DEBT_TYPES));
        }
        applyColumns();
        updateScope();
    }

    private void applyColumns() {
        if (isDebtSummary()) {
            table.setColumns(
                    ReportColumn.of("tenant_tel", "Tel. No.", 130),
                    ReportColumn.of("tenant_name", "Tenant", 160),
                    ReportColumn.of("unit_name", "Unit", 110),
                    ReportColumn.money("balance", "Balance", 110)
            );
        } else if (isUnpaid()) {
            table.setColumns(
                    ReportColumn.of("tenant_id", "Tenant ID", 120),
                    ReportColumn.of("tenant_name", "Names", 160),
                    ReportColumn.of("tenant_tel", "Tel No.", 120),
                    ReportColumn.of("status", "Status", 100),
                    ReportColumn.money("balance", "Balance", 110)
            );
        } else {
            table.setColumns(
                    ReportColumn.of("date", "Date", 110),
                    ReportColumn.of("tenant", "Tenant", 150),
                    ReportColumn.of("desc", "Description", 180),
                    ReportColumn.money("debit", "Debit/Invoices", 120),
                    ReportColumn.money("credit", "Credit/Payments", 130),
                    ReportColumn.money("balance", "Balance", 110)
            );
        }
    }

    private void updateScope() {
        if (txtScope == null) {
            return;
        }
        if (isDebtSummary()) {
            String property = ReportSupport.spinnerName(spnProperty);
            txtScope.setText(ReportSupport.filled(propertycode)
                    ? "Debt summary for " + property + " · " + selectValue()
                    : "Select a property, then Active, Inactive, or All Debts.");
            return;
        }
        if (isUnpaid()) {
            txtScope.setText(ReportSupport.filled(tenantId())
                    ? "Unpaid balance for " + ReportSupport.spinnerName(spnTenant)
                    : "Unpaid balances for all tenants");
            return;
        }
        if (ReportSupport.filled(tenantId())) {
            txtScope.setText("Statement for " + ReportSupport.spinnerName(spnTenant) + " · " + selectValue());
        } else {
            txtScope.setText("Select a tenant to load a statement. Unpaid with All tenants shows every outstanding balance.");
        }
    }

    private void loadTenants() {
        if (ReportSupport.filled(propertycode)) {
            getspinnerData(conf.getSERVERURL() + "select_tenants.php", propertycode, "property", "tenants");
        } else {
            getspinnerData(conf.getSERVERURL() + "list_tenantidnamebyowner.php", ownerId, "owner_id", "tenants");
        }
    }

    private void loadGrid() {
        applyColumns();
        updateScope();
        if (!isStatementsChoice() && !isDebtSummary()) {
            statementList.clear();
            table.setRows(statementList);
            return;
        }
        if (!ReportSupport.filled(startdate, enddate)) {
            UiNotifier.snack(this, "Choose a start and end date first.");
            return;
        }
        if (isDebtSummary()) {
            if (!ReportSupport.filled(propertycode)) {
                statementList.clear();
                table.setEmpty("Select a property first.");
                table.setRows(statementList);
                return;
            }
            loadDebtSummary();
            return;
        }
        if (!isUnpaid() && !ReportSupport.filled(tenantId())) {
            statementList.clear();
            table.setEmpty("Select a tenant first. Choose All tenants only for Unpaid.");
            table.setRows(statementList);
            return;
        }
        loadStatements();
    }

    private void loadStatements() {
        StringRequest request = new FormRequest(conf.getSERVERURL() + "select_tenantstatement.php",
                response -> {
                    ReportSupport.warnIfNotJson(Statements.this, response);
                    bindStatements(ReportSupport.resultArray(response));
                },
                error -> VolleyErrors.show(Statements.this, error)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("owner_id", ownerId);
                map.put("start_date", startdate);
                map.put("end_date", enddate);
                map.put("pcode", propertycode == null ? "" : propertycode);
                map.put("ucode", "");
                map.put("id", tenantId());
                map.put("stype", selectValue());
                return map;
            }
        };
        ReportSupport.enqueue(this, request);
    }

    private void loadDebtSummary() {
        StringRequest request = new FormRequest(conf.getSERVERURL() + "select_debtsummary.php",
                response -> {
                    ReportSupport.warnIfNotJson(Statements.this, response);
                    bindDebts(ReportSupport.resultArray(response));
                },
                error -> VolleyErrors.show(Statements.this, error)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("owner_id", ownerId);
                map.put("pcode", propertycode == null ? "" : propertycode);
                map.put("start_date", startdate);
                map.put("end_date", enddate);
                map.put("stype", selectValue());
                return map;
            }
        };
        ReportSupport.enqueue(this, request);
    }

    private void getspinnerData(String url, String fieldcode, String fieldname, String kind) {
        Map<String, String> map = new HashMap<>();
        map.put(fieldname, fieldcode == null ? "" : fieldcode);
        if ("tenants".equals(kind) && "property".equals(fieldname)) {
            map.put("owner_id", ownerId);
            map.put("tenant_identifier", "");
            map.put("tenant_name", "");
            map.put("unit", "");
            map.put("startdate", "");
            map.put("enddate", "");
            map.put("dateflag", "");
        }
        ReportSupport.loadRows(this, url, map, rows -> {
            if ("properties".equals(kind)) {
                ReportSupport.fillNamed(spnProperty, propertyspinnerlist, rows,
                        "property_code", "property_name", "All properties");
            } else {
                ReportSupport.fillNamed(spnTenant, tenantpinnerlist, rows,
                        "tenant_identifier", "tenant_name", "All tenants");
            }
        });
    }

    private void bindStatements(JSONArray rows) {
        statementList.clear();
        float debitTotal = 0f;
        float creditTotal = 0f;
        String lastBalance = "";
        boolean unpaid = isUnpaid();
        String wantedTenant = tenantId();
        for (int i = 0; i < rows.length(); i++) {
            JSONObject json = rows.optJSONObject(i);
            if (json == null) {
                continue;
            }
            if (unpaid && ReportSupport.filled(wantedTenant)) {
                String rowId = ReportSupport.opt(json, "ID", "tenant_identifier");
                if (!wantedTenant.equals(rowId)) {
                    continue;
                }
            }
            HashMap<String, String> item = new HashMap<>();
            if (unpaid) {
                item.put("tenant_id", ReportSupport.opt(json, "ID", "tenant_identifier"));
                item.put("tenant_name", json.optString("tenant_name"));
                item.put("tenant_tel", json.optString("tenant_tel"));
                item.put("status", json.optString("active"));
                lastBalance = ReportSupport.money(json.optString("balance"));
                item.put("balance", lastBalance);
                debitTotal += ReportSupport.moneyValue(json.optString("balance"));
            } else {
                String desc = json.optString("description");
                String amount = json.optString("amount");
                boolean debit = ReportSupport.isDebitDescription(desc);
                boolean credit = ReportSupport.isCreditDescription(desc);
                if (debit) {
                    debitTotal += ReportSupport.moneyValue(amount);
                }
                if (credit) {
                    creditTotal += ReportSupport.moneyValue(amount);
                }
                lastBalance = ReportSupport.money(json.optString("balance"));
                item.put("date", json.optString("action_date"));
                item.put("tenant", json.optString("tenant_name"));
                item.put("desc", desc);
                item.put("debit", debit ? ReportSupport.money(amount) : "-");
                item.put("credit", credit ? ReportSupport.money(amount) : "-");
                item.put("balance", lastBalance);
            }
            statementList.add(item);
        }
        if (!statementList.isEmpty()) {
            HashMap<String, String> footer = new HashMap<>();
            if (unpaid) {
                footer.put("tenant_id", "");
                footer.put("tenant_name", "TOTAL");
                footer.put("tenant_tel", "");
                footer.put("status", "");
                footer.put("balance", ReportSupport.money(String.valueOf(debitTotal)));
            } else {
                footer.put("date", "");
                footer.put("tenant", "TOTAL");
                footer.put("desc", "");
                footer.put("debit", ReportSupport.money(String.valueOf(debitTotal)));
                footer.put("credit", ReportSupport.money(String.valueOf(creditTotal)));
                footer.put("balance", lastBalance);
            }
            ReportSupport.markFooter(footer);
            statementList.add(footer);
        }
        if (unpaid && ReportSupport.filled(wantedTenant)) {
            table.setEmpty("No unpaid balance for " + ReportSupport.spinnerName(spnTenant) + ".");
        } else if (unpaid) {
            table.setEmpty("No unpaid balances for all tenants.");
        } else {
            table.setEmpty("No statement lines for this tenant.");
        }
        table.setRows(statementList);
        updateScope();
    }

    private void bindDebts(JSONArray rows) {
        statementList.clear();
        float total = 0f;
        for (int i = 0; i < rows.length(); i++) {
            JSONObject json = rows.optJSONObject(i);
            if (json == null) {
                continue;
            }
            HashMap<String, String> item = new HashMap<>();
            item.put("tenant_tel", json.optString("tenant_tel"));
            item.put("tenant_name", json.optString("tenant_name"));
            item.put("unit_name", ReportSupport.opt(json, "unit_name"));
            item.put("balance", ReportSupport.money(json.optString("balance")));
            total += ReportSupport.moneyValue(json.optString("balance"));
            statementList.add(item);
        }
        if (!statementList.isEmpty()) {
            HashMap<String, String> footer = new HashMap<>();
            footer.put("tenant_tel", "");
            footer.put("tenant_name", "TOTAL");
            footer.put("unit_name", "");
            footer.put("balance", ReportSupport.money(String.valueOf(total)));
            ReportSupport.markFooter(footer);
            statementList.add(footer);
        }
        table.setEmpty("No debts for this property.");
        table.setRows(statementList);
        updateScope();
    }

    private void handleAction(String action) {
        Map<String, String> params = new HashMap<>();
        String script;
        String filename;
        if (isUnpaid() && ReportSupport.filled(tenantId())) {
            params.put("tenantid", tenantId());
            params.put("startdate", startdate);
            params.put("enddate", enddate);
            params.put("reportype", "All");
            params.put("ownerid", ownerId);
            script = "printTenancyStatement.php";
            filename = ReportSupport.pdfName("unpaid", startdate, enddate);
        } else if (isDebtSummary() || isUnpaid()) {
            if (isDebtSummary() && !ReportSupport.filled(propertycode)) {
                UiNotifier.snack(this, "Select a property first.");
                return;
            }
            params.put("ownerid", ownerId);
            params.put("pcode", propertycode == null ? "" : propertycode);
            params.put("enddate", enddate);
            params.put("tenantid", tenantId());
            params.put("debttype", isDebtSummary() ? selectValue() : "All");
            script = "printTenancyDebts.php";
            filename = ReportSupport.pdfName(isDebtSummary() ? "debt_summary" : "unpaid_all", startdate, enddate);
        } else if (isStatementsChoice()) {
            if (!ReportSupport.filled(tenantId())) {
                UiNotifier.snack(this, "Select a tenant first.");
                return;
            }
            params.put("tenantid", tenantId());
            params.put("startdate", startdate);
            params.put("enddate", enddate);
            params.put("reportype", selectValue());
            params.put("ownerid", ownerId);
            script = "printTenancyStatement.php";
            filename = ReportSupport.pdfName("statement", startdate, enddate);
        } else {
            UiNotifier.snack(this, "Choose Statements or Debt Summary first.");
            return;
        }
        ReportSupport.handlePdfAction(this, action, ReportSupport.withQuery(conf.getSERVERURL() + script, params), filename);
    }
}
