package com.threemsystems.rentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
    private EditText Id, telNo, address, names, password, confirmPassword;
    private Spinner ownertype;
    private Button Submit, Close, Reset;
    private TextView tvlogin;
    private ProgressBar progressBar;
    private final String[] ownertypes = {"Individual", "Cooperate"};
    private final Config conf = Config.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ScreenNav.bind(this);

        Id = findViewById(R.id.etID);
        telNo = findViewById(R.id.etTel);
        address = findViewById(R.id.etAddress);
        names = findViewById(R.id.etnames);
        password = findViewById(R.id.etpwd);
        confirmPassword = findViewById(R.id.etpwdConfirm);
        ownertype = findViewById(R.id.ownertype);
        tvlogin = findViewById(R.id.textlogin);
        Submit = findViewById(R.id.btsubmit);
        progressBar = findViewById(R.id.progressBar);
        Close = findViewById(R.id.btclose);
        Reset = findViewById(R.id.btnreset);

        ownertype.setAdapter(ReportSupport.stringAdapter(this, ownertypes));
        tvlogin.setOnClickListener(v -> finish());
        Reset.setOnClickListener(v -> clearForm());
        Close.setOnClickListener(v -> finish());
        Submit.setOnClickListener(v -> submit());
    }

    private void clearForm() {
        Id.setText("");
        telNo.setText("");
        address.setText("");
        names.setText("");
        password.setText("");
        confirmPassword.setText("");
    }

    private void submit() {
        String identifier = textOf(Id).trim();
        String phone = textOf(telNo).trim();
        String ownerNames = textOf(names).trim();
        String ownerAddress = textOf(address).trim();
        String type = String.valueOf(ownertype.getSelectedItem());
        String pass = textOf(password);
        String confirm = textOf(confirmPassword);

        if (!ReportSupport.filled(identifier, phone, ownerNames, ownerAddress, type, pass, confirm)) {
            UiNotifier.snack(this, "All fields are required.");
            return;
        }
        if (pass.length() < 6) {
            UiNotifier.snack(this, "Password must be at least 6 characters.");
            return;
        }
        if (!pass.equals(confirm)) {
            UiNotifier.snack(this, "The passwords do not match.");
            return;
        }

        progressBar.setVisibility(android.view.View.VISIBLE);
        Submit.setEnabled(false);
        String[] field = {"owner_identifier", "owner_tel", "owner_names", "owner_address", "owner_type", "owner_id", "npswd", "confirm_password"};
        String[] data = {identifier, phone, ownerNames, ownerAddress, type, "", pass, confirm};
        ApiClient.get().post(RegisterActivity.this, conf.getSERVERURL() + "save_new_owner.php", field, data, new ApiClient.Callback() {
            @Override
            public void onSuccess(String result) {
                progressBar.setVisibility(android.view.View.GONE);
                Submit.setEnabled(true);
                if (UiNotifier.jsonSuccess(result)) {
                    Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    login.putExtra(LoginActivity.EXTRA_USERNAME, phone);
                    login.putExtra(LoginActivity.EXTRA_NOTICE, UiNotifier.userMessage(result, "Account created. Sign in with your phone number."));
                    startActivity(login);
                    finish();
                } else {
                    UiNotifier.snack(RegisterActivity.this, UiNotifier.userMessage(result, "Registration failed. Try again."));
                }
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(android.view.View.GONE);
                Submit.setEnabled(true);
                UiNotifier.snack(RegisterActivity.this, message);
            }
        });
    }

    private static String textOf(EditText field) {
        return field.getText() == null ? "" : String.valueOf(field.getText());
    }
}
