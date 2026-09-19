package com.threemsystems.rentmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_NOTICE = "notice";

    private EditText username, password;
    private TextView copyrightTV;
    private Button login, register, cancel;
    private ProgressBar progressBar;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = SessionManager.get(this);
        if (session.isLoggedIn()) {
            openHome();
            return;
        }

        setContentView(R.layout.activity_login);
        AdaptiveUi.attach(this);
        username = findViewById(R.id.etusername);
        password = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
        register = findViewById(R.id.btnRegister);
        cancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progress);

        copyrightTV = findViewById(R.id.copyrightTV);
        ScreenNav.copyright(copyrightTV);
        applyIntent(getIntent());

        register.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        cancel.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, id) -> finish())
                .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                .show());

        login.setOnClickListener(this::attemptLogin);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        applyIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (password != null) {
            password.setText("");
        }
    }

    private void applyIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        String user = intent.getStringExtra(EXTRA_USERNAME);
        if (ReportSupport.filled(user) && username != null) {
            username.setText(user);
        }
        String notice = intent.getStringExtra(EXTRA_NOTICE);
        if (ReportSupport.filled(notice)) {
            username.post(() -> UiNotifier.snack(this, notice));
            intent.removeExtra(EXTRA_NOTICE);
        }
        if (password != null) {
            password.setText("");
        }
    }

    private void attemptLogin(View v) {
        String user = String.valueOf(username.getText()).trim();
        String pass = String.valueOf(password.getText());
        if (user.isEmpty() || pass.isEmpty()) {
            UiNotifier.snack(this, "Enter your phone number (or username) and password.");
            return;
        }

        setLoading(true);
        Config conf = Config.getInstance();
        String[] field = {"usn", "pswd", "timedout"};
        String[] data = {user, pass, "no"};
        ApiClient.get().post(LoginActivity.this, conf.getSERVERURL() + "login.php", field, data, new ApiClient.Callback() {
            @Override
            public void onSuccess(String result) {
                setLoading(false);
                handleLoginResult(user, result);
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                password.setText("");
                UiNotifier.snack(LoginActivity.this, message);
            }
        });
    }

    private void handleLoginResult(String usernameValue, String rawResult) {
        LoginResult parsed = LoginResult.parse(rawResult, usernameValue);
        if (parsed.success && ReportSupport.filled(parsed.ownerId, parsed.token)) {
            password.setText("");
            session.saveSession(parsed.ownerId, parsed.name, parsed.userLevel,
                    ReportSupport.filled(parsed.username) ? parsed.username : usernameValue, parsed.token);
            openHome();
            return;
        }
        password.setText("");
        UiNotifier.snack(this, ReportSupport.filled(parsed.message) ? parsed.message : "Incorrect username or password.");
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        login.setEnabled(!loading);
        register.setEnabled(!loading);
    }

    private void openHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
