package com.threemsystems.rentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;

public class AddTenant extends AppCompatActivity {
    private Spinner tenantType, propertyunit_spinner, Status, property_spinner;
    private EditText unitRent, electriBill, serviceFee, ID, Name, Pin, Tell, Address, dateFrom, WaterBill;
    private Button Save, Close, Reset;
    private ProgressBar progressBar;
    private final ArrayList<spinnerItems> spinnerlist = new ArrayList<>();
    private final ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    private String propertycode, unitcode, Date_from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tenant);
        ScreenNav.bind(this);
        tenantType = findViewById(R.id.spnTenantTypeAdd);
        property_spinner = findViewById(R.id.spnTenantPAdd);
        propertyunit_spinner = findViewById(R.id.spnTenantPUAdd);
        Status = findViewById(R.id.spnTenantStatusAdd);
        unitRent = findViewById(R.id.etTenantUNAdd);
        electriBill = findViewById(R.id.ettenantElecAdd);
        WaterBill = findViewById(R.id.ettenantWaterAdd);
        serviceFee = findViewById(R.id.etTenantSAdd);
        ID = findViewById(R.id.etTenantIDAdd);
        Name = findViewById(R.id.etTenantNameAdd);
        Pin = findViewById(R.id.etTenantpinAdd);
        Tell = findViewById(R.id.ettenantTelAdd);
        Address = findViewById(R.id.ettenantAddAdd);
        dateFrom = findViewById(R.id.dteTenantFrmAdd);
        Save = findViewById(R.id.btTenantsubmit);
        Close = findViewById(R.id.btnTenantclose);
        Reset = findViewById(R.id.btnTenantreset);
        progressBar = findViewById(R.id.tenantaddprogress);

        DateUi.bindPicker(this, dateFrom, iso -> Date_from = iso);
        ReportSupport.bindChoices(tenantType, "Individual", "Cooperate");
        ReportSupport.bindChoices(Status, "Active", "Inactive");
        ReportSupport.onItem(property_spinner, item -> {
            propertycode = item.getId();
            ReportSupport.loadUnits(AddTenant.this, propertycode, propertyunit_spinner, unitspinnerlist);
        });
        ReportSupport.onItem(propertyunit_spinner, item -> unitcode = item.getId());
        ReportSupport.loadOwnerProperties(this, property_spinner, spinnerlist);

        Reset.setOnClickListener(v -> {
            unitRent.setText("");
            electriBill.setText("");
            WaterBill.setText("");
            serviceFee.setText("");
            ID.setText("");
            Name.setText("");
            Pin.setText("");
            Tell.setText("");
            Address.setText("");
            dateFrom.setText("");
        });
        Close.setOnClickListener(v -> finish());
        Save.setOnClickListener(v -> save());
    }

    private void save() {
        String identifier = ReportSupport.text(ID);
        String name = ReportSupport.text(Name);
        String pin = ReportSupport.text(Pin);
        String address = ReportSupport.text(Address);
        String type = ReportSupport.selectedText(tenantType);
        String tel = ReportSupport.text(Tell);
        String rent = ReportSupport.text(unitRent);
        String water = ReportSupport.text(WaterBill);
        String electric = ReportSupport.text(electriBill);
        String fee = ReportSupport.text(serviceFee);
        String active = ReportSupport.selectedText(Status);
        if (!ReportSupport.filled(identifier, name, pin, address, type, tel, propertycode, unitcode, rent, water, electric, Date_from, active)) {
            UiNotifier.snack(this, "All fields are required.");
            return;
        }
        ReportSupport.submit(this, progressBar, Save, "save_new_tenants.php",
                new String[]{"tenant_identifier", "tenant_name", "tenant_pin", "tenant_address", "tenant_type",
                        "tenant_tel", "property_code", "unit_code", "rent_payable", "waterbill", "electricbill",
                        "servicefee", "date_from", "active"},
                new String[]{identifier, name, pin, address, type, tel, propertycode, unitcode, rent, water, electric, fee, Date_from, active},
                "Tenant saved.", "Could not save the tenant.");
    }
}
