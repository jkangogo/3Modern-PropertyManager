package com.threemsystems.rentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;

public class JounalEntry extends AppCompatActivity {
    private Spinner property, propertyUnit, Tenant, Reason_spn;
    private EditText amount, journaDate;
    private Button save, close, reset;
    private String propertycode, unitcode, tenantid, tenantname, statementid, Action_date;
    private ProgressBar progressBar;
    private final ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();
    private final ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    private final ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jounal_entry);
        ScreenNav.bind(this);
        property = findViewById(R.id.spnProperty);
        propertyUnit = findViewById(R.id.spnPropertyUnit);
        Tenant = findViewById(R.id.spnTenant);
        Reason_spn = findViewById(R.id.spnReason);
        amount = findViewById(R.id.etAmount);
        journaDate = findViewById(R.id.dteJournalDate);
        save = findViewById(R.id.btnPaymentsubmit);
        close = findViewById(R.id.btnPaymentclose);
        reset = findViewById(R.id.btnPaymentreset);
        progressBar = findViewById(R.id.paymentaddprogress);

        DateUi.bindPicker(this, journaDate, iso -> Action_date = iso);
        ReportSupport.bindChoices(Reason_spn, "Old Balance", "Over Payment", "Under Payment", "Bounced Pay");
        ReportSupport.onItem(property, item -> {
            propertycode = item.getId();
            unitspinnerlist.clear();
            ReportSupport.loadUnits(JounalEntry.this, propertycode, propertyUnit, unitspinnerlist);
        });
        ReportSupport.onItem(propertyUnit, item -> {
            unitcode = item.getId();
            tenantpinnerlist.clear();
            ReportSupport.loadTenantsForUnit(JounalEntry.this, unitcode, Tenant, tenantpinnerlist);
        });
        ReportSupport.onItem(Tenant, item -> {
            tenantid = item.getId();
            tenantname = item.getName();
        });
        ReportSupport.loadOwnerProperties(this, property, propertyspinnerlist);

        reset.setOnClickListener(v -> {
            amount.setText("");
            journaDate.setText("");
        });
        close.setOnClickListener(v -> finish());
        save.setOnClickListener(v -> save());
    }

    private void save() {
        String payAmount = ReportSupport.text(amount);
        String reason = ReportSupport.selectedText(Reason_spn);
        if (!ReportSupport.filled(Action_date, tenantid, tenantname, propertycode, unitcode, payAmount, reason)) {
            UiNotifier.snack(this, "All fields are required.");
            return;
        }
        String statement = statementid == null ? "" : statementid;
        ReportSupport.submit(this, progressBar, save, "saveJournalEntry.php",
                new String[]{"statement_id", "action_date", "statement_identifier", "tenant_identifier",
                        "description", "amount", "balance", "transaction_id", "payment_id"},
                new String[]{statement, Action_date, tenantid, tenantname, propertycode, unitcode, payAmount, reason, ""},
                "Journal entry saved.", "Could not save the journal entry.");
    }
}
