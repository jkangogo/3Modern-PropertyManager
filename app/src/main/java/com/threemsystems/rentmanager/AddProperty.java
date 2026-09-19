package com.threemsystems.rentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;

public class AddProperty extends AppCompatActivity {
    private EditText pCode, ownerID, pName, pDesc, Tell, Address;
    private Spinner ownerType;
    private Button Save, Close, Reset;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_property);
        ScreenNav.bind(this);
        pCode = findViewById(R.id.etPropertyAdd);
        ownerID = findViewById(R.id.etPropertyOIDAdd);
        pName = findViewById(R.id.etPropertyNameAdd);
        pDesc = findViewById(R.id.etPropertyDescAdd);
        Tell = findViewById(R.id.etPropertyTelAdd);
        Address = findViewById(R.id.etPropertyAddressAdd);
        ownerType = findViewById(R.id.spnPropertyOTypeAdd);
        Save = findViewById(R.id.btnPropertysubmit);
        Close = findViewById(R.id.btnPropertyclose);
        Reset = findViewById(R.id.btnPropertyreset);
        progressBar = findViewById(R.id.progress);

        ReportSupport.bindChoices(ownerType, "Individual", "Cooperate");
        ownerID.setText(SessionManager.get(this).getOwnerId());
        pCode.setText(String.valueOf(System.currentTimeMillis()));

        Reset.setOnClickListener(v -> {
            pName.setText("");
            pDesc.setText("");
            Tell.setText("");
            Address.setText("");
        });
        Close.setOnClickListener(v -> finish());
        Save.setOnClickListener(v -> save());
    }

    private void save() {
        String code = ReportSupport.text(pCode);
        String owner = ReportSupport.text(ownerID);
        String name = ReportSupport.text(pName);
        String desc = ReportSupport.text(pDesc);
        String type = ReportSupport.selectedText(ownerType);
        String address = ReportSupport.text(Address);
        String tel = ReportSupport.text(Tell);
        if (!ReportSupport.filled(code, owner, name, desc, type, address, tel)) {
            UiNotifier.snack(this, "All fields are required.");
            return;
        }
        ReportSupport.submit(this, progressBar, Save, "save_new_property.php",
                new String[]{"property_code", "owner_identifier", "property_name", "property_desc", "property_type", "property_tel", "property_address", "property_id"},
                new String[]{code, owner, name, desc, type, tel, address, ""},
                "Property saved.", "Could not save the property.");
    }
}
