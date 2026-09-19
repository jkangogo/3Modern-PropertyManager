package com.threemsystems.rentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;

public class AddPayment extends AppCompatActivity {
    private Spinner property_spinner, unit_spinner, tenant_spinner, PaymentMode;
    private String propertycode, tenantid, tenantname, unitcode, Payment_date;
    private EditText Amount, payDate, mpesacode, bankref;
    private Button Save, Close, Reset;
    private ProgressBar progressBar;
    private final ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();
    private final ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    private final ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);
        ScreenNav.bind(this);
        property_spinner = findViewById(R.id.spnPaymentPropertyAdd);
        unit_spinner = findViewById(R.id.spnPaymentPropertyUnitAdd);
        tenant_spinner = findViewById(R.id.spnPaymentTenantUAdd);
        PaymentMode = findViewById(R.id.spnPaymentModeAdd);
        payDate = findViewById(R.id.dtePaymentDateAdd);
        Amount = findViewById(R.id.etPaymentAmountAdd);
        mpesacode = findViewById(R.id.etPaympesacodeAdd);
        bankref = findViewById(R.id.etPayBankrefAdd);
        Save = findViewById(R.id.btnPaymentsubmit);
        Close = findViewById(R.id.btnPaymentclose);
        Reset = findViewById(R.id.btnPaymentreset);
        progressBar = findViewById(R.id.paymentaddprogress);

        DateUi.bindPicker(this, payDate, iso -> Payment_date = iso);
        ReportSupport.bindChoices(PaymentMode, "M-Pesa", "Cash", "Bank Transfer");
        ReportSupport.onItem(property_spinner, item -> {
            propertycode = item.getId();
            unitspinnerlist.clear();
            ReportSupport.loadUnits(AddPayment.this, propertycode, unit_spinner, unitspinnerlist);
        });
        ReportSupport.onItem(unit_spinner, item -> {
            unitcode = item.getId();
            tenantpinnerlist.clear();
            ReportSupport.loadTenantsForUnit(AddPayment.this, unitcode, tenant_spinner, tenantpinnerlist);
        });
        ReportSupport.onItem(tenant_spinner, item -> {
            tenantid = item.getId();
            tenantname = item.getName();
        });
        ReportSupport.loadOwnerProperties(this, property_spinner, propertyspinnerlist);

        Reset.setOnClickListener(v -> {
            payDate.setText("");
            Amount.setText("");
            mpesacode.setText("");
            bankref.setText("");
        });
        Close.setOnClickListener(v -> finish());
        Save.setOnClickListener(v -> save());
    }

    private void save() {
        String amount = ReportSupport.text(Amount);
        String mode = ReportSupport.selectedText(PaymentMode);
        if (!ReportSupport.filled(Payment_date, tenantid, tenantname, propertycode, unitcode, amount, mode)) {
            UiNotifier.snack(this, "All fields are required.");
            return;
        }
        ReportSupport.submit(this, progressBar, Save, "save_tenantpayment.php",
                new String[]{"payment_date", "tenant_identifier", "tenant_name", "property_code", "unit_code",
                        "payment_amount", "pay_mode", "transaction_id", "payment_id", "mpesa_code", "bank_ref", "owner_id"},
                new String[]{Payment_date, tenantid, tenantname, propertycode, unitcode, amount, mode, "", "",
                        ReportSupport.text(mpesacode), ReportSupport.text(bankref), SessionManager.get(this).getOwnerId()},
                "Payment saved.", "Could not save the payment.");
    }
}
