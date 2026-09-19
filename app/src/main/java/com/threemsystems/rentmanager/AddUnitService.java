package com.threemsystems.rentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;

public class AddUnitService extends AppCompatActivity {
    private Spinner property_spinner, servicetype_spinner, propertyunit_spinner, Status;
    private EditText previousreadEdit, currentRead, etConsumption, etRate, etServiceCost, service_monthText;
    private Button Save, Close, Reset;
    private ProgressBar progressBar;
    private final ArrayList<spinnerItems> spinnerlist = new ArrayList<>();
    private final ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    private final ArrayList<spinnerItems> servicespinnerlist = new ArrayList<>();
    private String propertycode, unitcode, service_month, servicetypecode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unitservice);
        ScreenNav.bind(this);
        property_spinner = findViewById(R.id.spnUnitservicePAdd);
        propertyunit_spinner = findViewById(R.id.spnUnitservicePUAdd);
        servicetype_spinner = findViewById(R.id.spServiveTypeAdd);
        service_monthText = findViewById(R.id.ServiceMonthAdd);
        previousreadEdit = findViewById(R.id.previousread);
        currentRead = findViewById(R.id.currentRead);
        etConsumption = findViewById(R.id.etConsumption);
        etRate = findViewById(R.id.etRate);
        etServiceCost = findViewById(R.id.etServiceCost);
        Status = findViewById(R.id.spnServiceStatusAdd);
        Save = findViewById(R.id.btServicesubmit);
        Close = findViewById(R.id.btnServiceclose);
        Reset = findViewById(R.id.btnServicereset);
        progressBar = findViewById(R.id.unitserviceaddprogress);

        DateUi.bindPicker(this, service_monthText, iso -> service_month = iso);
        ReportSupport.bindChoices(Status, "Active", "Inactive");
        ReportSupport.onItem(property_spinner, item -> {
            propertycode = item.getId();
            ReportSupport.loadUnits(AddUnitService.this, propertycode, propertyunit_spinner, unitspinnerlist);
        });
        ReportSupport.onItem(propertyunit_spinner, item -> unitcode = item.getId());
        ReportSupport.onItem(servicetype_spinner, item -> servicetypecode = item.getId());
        ReportSupport.loadOwnerProperties(this, property_spinner, spinnerlist);
        ReportSupport.loadRows(this, Config.getInstance().getSERVERURL() + "list_rentalservices.php",
                "property_code", "",
                rows -> ReportSupport.fillSpinner(servicetype_spinner, servicespinnerlist, rows, "rental_service_code", "rental_service_name"));

        Reset.setOnClickListener(v -> {
            previousreadEdit.setText("");
            currentRead.setText("");
            etConsumption.setText("");
            etRate.setText("");
            etServiceCost.setText("");
            service_monthText.setText("");
        });
        Close.setOnClickListener(v -> finish());
        Save.setOnClickListener(v -> save());
    }

    private void save() {
        String billedDate = ReportSupport.filled(service_month) ? service_month : ReportSupport.text(service_monthText);
        String type = ReportSupport.filled(servicetypecode) ? servicetypecode : ReportSupport.selectedText(servicetype_spinner);
        String status = ReportSupport.selectedText(Status);
        String cost = ReportSupport.text(etServiceCost);
        if (!ReportSupport.filled(status, propertycode, unitcode, cost, billedDate)) {
            UiNotifier.snack(this, "All fields are required.");
            return;
        }
        ReportSupport.submit(this, progressBar, Save, "save_new_unitservice.php",
                new String[]{"Property_code", "Unit_code", "previousread", "currentread", "consumption",
                        "service_date", "rate", "service_cost", "status", "servicetype"},
                new String[]{propertycode, unitcode, ReportSupport.text(previousreadEdit), ReportSupport.text(currentRead),
                        ReportSupport.text(etConsumption), billedDate, ReportSupport.text(etRate), cost, status, type},
                "Unit service saved.", "Could not save the unit service.");
    }
}
