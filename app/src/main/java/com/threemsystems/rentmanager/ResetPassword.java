package com.threemsystems.rentmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class ResetPassword extends AppCompatActivity {
    private EditText username, currPassword, NewPassword, ConfrmNewPassword;
    private Button cancel, ChangePassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ScreenNav.bind(this);

        username = findViewById(R.id.etUser);
        currPassword = findViewById(R.id.etcurrPass);
        NewPassword = findViewById(R.id.etnewPass);
        ConfrmNewPassword = findViewById(R.id.etconfrmnp);
        cancel = findViewById(R.id.btcancel);
        ChangePassword = findViewById(R.id.btchangePassword);
        progressBar = findViewById(R.id.progressBar);

        String sessionUser = SessionManager.get(this).getUsername();
        if (ReportSupport.filled(sessionUser)) {
            username.setText(sessionUser);
            username.setEnabled(false);
            username.setFocusable(false);
        }

        cancel.setOnClickListener(v -> {
            wipeSecrets();
            finish();
        });
        ChangePassword.setOnClickListener(v -> attemptChange());
    }

    private void attemptChange() {
        String userName = textOf(username).trim();
        String current = textOf(currPassword);
        String next = textOf(NewPassword);
        String confirm = textOf(ConfrmNewPassword);

        if (!ReportSupport.filled(userName, current, next, confirm)) {
            UiNotifier.snack(this, "All fields are required.");
            return;
        }
        if (!next.equals(confirm)) {
            UiNotifier.snack(this, "The new passwords do not match.");
            ConfrmNewPassword.requestFocus();
            return;
        }
        if (next.equals(current)) {
            UiNotifier.snack(this, "Choose a password that is different from the current one.");
            NewPassword.requestFocus();
            return;
        }

        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to change your password?")
                .setPositiveButton("Yes", (dialog, id) -> submitChange(userName, current, next, confirm))
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .show();
    }

    private void submitChange(String userName, String current, String next, String confirm) {
        setBusy(true);
        String[] field = {"usn", "cpswd", "npswd", "confirm_password"};
        String[] data = {userName, current, next, confirm};
        ApiClient.get().post(ResetPassword.this, Config.getInstance().getSERVERURL() + "save_changePassword.php", field, data,
                new ApiClient.Callback() {
                    @Override
                    public void onSuccess(String result) {
                        setBusy(false);
                        if (UiNotifier.jsonSuccess(result)) {
                            goToLogin(userName, UiNotifier.userMessage(result, "Password changed. Please log in."));
                            return;
                        }
                        wipeSecrets();
                        UiNotifier.snack(ResetPassword.this, UiNotifier.userMessage(result, "Could not change password."));
                    }

                    @Override
                    public void onError(String message) {
                        setBusy(false);
                        UiNotifier.snack(ResetPassword.this, message);
                    }
                });
    }

    private void goToLogin(String userName, String message) {
        wipeSecrets();
        SessionManager.get(this).clear();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(LoginActivity.EXTRA_USERNAME, userName);
        intent.putExtra(LoginActivity.EXTRA_NOTICE, message);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        wipeSecrets();
        super.onSaveInstanceState(outState);
    }

    private void setBusy(boolean busy) {
        progressBar.setVisibility(busy ? android.view.View.VISIBLE : android.view.View.GONE);
        ChangePassword.setEnabled(!busy);
        cancel.setEnabled(!busy);
    }

    private void wipeSecrets() {
        if (currPassword != null) {
            currPassword.setText("");
        }
        if (NewPassword != null) {
            NewPassword.setText("");
        }
        if (ConfrmNewPassword != null) {
            ConfrmNewPassword.setText("");
        }
    }

    private static String textOf(EditText field) {
        return field.getText() == null ? "" : String.valueOf(field.getText());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            wipeSecrets();
        }
    }

    @Override
    protected void onDestroy() {
        wipeSecrets();
        super.onDestroy();
    }
}
