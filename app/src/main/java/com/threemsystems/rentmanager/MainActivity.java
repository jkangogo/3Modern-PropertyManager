package com.threemsystems.rentmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Button dataEntry, reports, resetPass, exit;
    private TextView copyrightTV, greeting, greetingSub, statProperties, statStatus, dashboardHint;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = SessionManager.get(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        ScreenNav.bindHome(this);
        dataEntry = findViewById(R.id.btnDataentry);
        reports = findViewById(R.id.btnReports);
        resetPass = findViewById(R.id.btnresetPassword);
        exit = findViewById(R.id.btnExit);
        copyrightTV = findViewById(R.id.copyrightTV2);
        greeting = findViewById(R.id.tvGreeting);
        greetingSub = findViewById(R.id.tvGreetingSub);
        statProperties = findViewById(R.id.tvStatProperties);
        statStatus = findViewById(R.id.tvStatStatus);
        dashboardHint = findViewById(R.id.tvDashboardHint);

        ScreenNav.copyright(copyrightTV);
        bindGreeting();

        dataEntry.setOnClickListener(v ->
                startActivity(new Intent(this, DataEntry.class)));
        reports.setOnClickListener(v ->
                startActivity(new Intent(this, Reports.class)));
        resetPass.setOnClickListener(v ->
                startActivity(new Intent(this, ResetPassword.class)));
        exit.setOnClickListener(v -> confirmLogout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (session.isLoggedIn()) {
            bindGreeting();
            loadPropertyCount();
        }
    }

    private void bindGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String period;
        if (hour < 12) {
            period = "Good morning";
        } else if (hour < 17) {
            period = "Good afternoon";
        } else {
            period = "Good evening";
        }
        greeting.setText(period + ", " + session.getDisplayName());
        greetingSub.setText(new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(new Date()));
    }

    private void loadPropertyCount() {
        statProperties.setText("—");
        statStatus.setText("Updating…");
        dashboardHint.setText("Refreshing your portfolio");

        String ownerId = session.getOwnerId();
        if (ownerId.isEmpty()) {
            bindStats(0, false);
            return;
        }

        String url = Config.getInstance().getSERVERURL() + "list_properties.php";
        StringRequest request = new FormRequest(url,
                response -> {
                    JSONArray rows = ReportSupport.resultArray(response);
                    bindStats(ReportSupport.namedPropertyCount(rows), ReportSupport.isJsonObject(response));
                },
                error -> {
                    statProperties.setText("—");
                    statStatus.setText("Offline");
                    dashboardHint.setText(VolleyErrors.message(error));
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("owner_id", ownerId);
                return map;
            }
        };
        request.setTag(MainActivity.class.getName());
        ReportSupport.enqueue(this, request);
    }

    private void bindStats(int count, boolean parsedJson) {
        statProperties.setText(String.valueOf(count));
        if (!parsedJson) {
            statStatus.setText("Couldn't refresh");
            dashboardHint.setText("Open Reports to view your properties");
            return;
        }
        if (count == 0) {
            statStatus.setText("No properties yet");
            dashboardHint.setText("Add a property from Data entry to get started");
        } else {
            statStatus.setText("Up to date");
            dashboardHint.setText("Record activity or open reports");
        }
    }

    @Override
    protected void onDestroy() {
        PManagerApp.get().getRequestQueue().cancelAll(MainActivity.class.getName());
        super.onDestroy();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    session.clear();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel())
                .show();
    }
}
