package com.threemsystems.rentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;

public class NewInvoice extends AppCompatActivity {
    private Spinner property, propertyUnit, Tenant, invoiceType;
    private EditText unitRent, electBill, serviceFee, invoiceMonth;
    private Button save, close, reset;
    private String propertycode, unitcode, tenantid, tenantname, Invoice_month;
    private ProgressBar progressBar;
    private final ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();
    private final ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    private final ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invoice);
        ScreenNav.bind(this);
        property = findViewById(R.id.spnProperty);
        propertyUnit = findViewById(R.id.spnPropertyUnit);
        Tenant = findViewById(R.id.spnTenant);
        invoiceType = findViewById(R.id.spnInvoiceType);
        unitRent = findViewById(R.id.etUnitRent);
        electBill = findViewById(R.id.etElectBill);
        serviceFee = findViewById(R.id.etServiceFee);
        invoiceMonth = findViewById(R.id.dteInvoicemonth);
        save = findViewById(R.id.btnPaymentsubmit);
        close = findViewById(R.id.btnPaymentclose);
        reset = findViewById(R.id.btnPaymentreset);
        progressBar = findViewById(R.id.paymentaddprogress);

        DateUi.bindPicker(this, invoiceMonth, iso -> Invoice_month = iso);
        ReportSupport.bindChoices(invoiceType, "Temporary", "Permanent");
        ReportSupport.onItem(property, item -> {
            propertycode = item.getId();
            unitspinnerlist.clear();
            ReportSupport.loadUnits(NewInvoice.this, propertycode, propertyUnit, unitspinnerlist);
        });
        ReportSupport.onItem(propertyUnit, item -> {
            unitcode = item.getId();
            tenantpinnerlist.clear();
            ReportSupport.loadTenantsForUnit(NewInvoice.this, unitcode, Tenant, tenantpinnerlist);
        });
        ReportSupport.onItem(Tenant, item -> {
            tenantid = item.getId();
            tenantname = item.getName();
        });
        ReportSupport.loadOwnerProperties(this, property, propertyspinnerlist);

        reset.setOnClickListener(v -> {
            unitRent.setText("");
            electBill.setText("");
            serviceFee.setText("");
            invoiceMonth.setText("");
        });
        close.setOnClickListener(v -> finish());
        save.setOnClickListener(v -> save());
    }

    private void save() {
        String rent = ReportSupport.text(unitRent);
        String fee = ReportSupport.text(serviceFee);
        String type = ReportSupport.selectedText(invoiceType);
        String electric = ReportSupport.filled(ReportSupport.text(electBill)) ? ReportSupport.text(electBill) : "0";
        if (!ReportSupport.filled(Invoice_month, tenantid, tenantname, propertycode, unitcode, fee, rent, type)) {
            UiNotifier.snack(this, "All fields are required.");
            return;
        }
        ReportSupport.submit(this, progressBar, save, "createtenant_invoice.php",
                new String[]{"tenant_identifier", "property_code", "unit_code", "rent_payable", "invoice_month",
                        "waterbill", "electricbill", "servicefee", "invoice_id"},
                new String[]{tenantid, propertycode, unitcode, rent, Invoice_month, "0", electric, fee, ""},
                "Invoice created.", "Could not create the invoice.");
    }
}
