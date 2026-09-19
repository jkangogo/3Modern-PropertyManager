package com.threemsystems.rentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;

public class AddUnit extends AppCompatActivity {
    private Spinner property_spinner, Electricity, Water;
    private EditText unitCode, unitName, Description, unitRent, electMeter, waterMeter, serviceFee;
    private Button Save, Close, Reset;
    private ProgressBar progressBar;
    private final ArrayList<spinnerItems> spinnerlist = new ArrayList<>();
    private String propertycode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unit);
        ScreenNav.bind(this);
        Electricity = findViewById(R.id.spnUnitElectAdd);
        Water = findViewById(R.id.spnUnitWaterSharedAdd);
        unitCode = findViewById(R.id.etUnitCodeAdd);
        unitName = findViewById(R.id.etUnitNameAdd);
        Description = findViewById(R.id.etUnitDescAdd);
        unitRent = findViewById(R.id.etUnitRentAdd);
        electMeter = findViewById(R.id.etUnitElectAdd);
        waterMeter = findViewById(R.id.etUnitWaterAdd);
        serviceFee = findViewById(R.id.etUnitServiceAdd);
        Save = findViewById(R.id.btnUnitsubmit);
        Close = findViewById(R.id.btnUnitclose);
        Reset = findViewById(R.id.btnUnitreset);
        progressBar = findViewById(R.id.unitaddprogress);
        property_spinner = findViewById(R.id.spnUnitPropertyAdd);

        ReportSupport.bindChoices(Electricity, "Shared", "Individual");
        ReportSupport.bindChoices(Water, "Shared", "Individual");
        ReportSupport.onItem(property_spinner, item -> propertycode = item.getId());
        ReportSupport.loadOwnerProperties(this, property_spinner, spinnerlist);
        unitCode.setText(String.valueOf(System.currentTimeMillis()));

        Reset.setOnClickListener(v -> {
            unitName.setText("");
            Description.setText("");
            unitRent.setText("");
            electMeter.setText("");
            waterMeter.setText("");
            serviceFee.setText("");
        });
        Close.setOnClickListener(v -> finish());
        Save.setOnClickListener(v -> save());
    }

    private void save() {
        String code = ReportSupport.text(unitCode);
        String name = ReportSupport.text(unitName);
        String desc = ReportSupport.text(Description);
        String rent = ReportSupport.text(unitRent);
        String elect = ReportSupport.text(electMeter);
        String electShare = ReportSupport.selectedText(Electricity);
        String water = ReportSupport.text(waterMeter);
        String waterShare = ReportSupport.selectedText(Water);
        String fee = ReportSupport.text(serviceFee);
        if (!ReportSupport.filled(code, name, desc, propertycode, rent, elect, electShare, water, waterShare, fee)) {
            UiNotifier.snack(this, "All fields are required.");
            return;
        }
        ReportSupport.submit(this, progressBar, Save, "save_new_propertyunit.php",
                new String[]{"unit_code", "unit_name", "unit_desc", "property_code", "unit_rent_amount",
                        "electricity_meter", "electricity_metershare", "water_meter", "water_metershare", "service_fee"},
                new String[]{code, name, desc, propertycode, rent, elect, electShare, water, waterShare, fee},
                "Unit saved.", "Could not save the unit.");
    }
}
