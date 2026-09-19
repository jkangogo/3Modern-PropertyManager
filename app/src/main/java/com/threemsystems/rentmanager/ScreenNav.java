package com.threemsystems.rentmanager;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public final class ScreenNav {
    private ScreenNav() {}

    public static void bind(AppCompatActivity activity) {
        bind(activity, true);
    }

    public static void bindHome(AppCompatActivity activity) {
        bind(activity, false);
    }

    public static void copyright(TextView view) {
        if (view != null) {
            view.setText("PManager © " + Calendar.getInstance().get(Calendar.YEAR) + ", All rights reserved.");
        }
    }

    public static void setBusy(AppCompatActivity activity, boolean busy) {
        View progress = activity.findViewById(R.id.progress);
        if (progress != null) {
            progress.setVisibility(busy ? View.VISIBLE : View.GONE);
        }
    }

    private static void bind(AppCompatActivity activity, boolean showBack) {
        if (!(activity instanceof LoginActivity) && !(activity instanceof RegisterActivity)) {
            if (!SessionManager.get(activity).isLoggedIn()) {
                Intent login = new Intent(activity, LoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(login);
                activity.finish();
                return;
            }
        }
        Toolbar toolbar = activity.findViewById(R.id.toolbar_top);
        if (toolbar != null) {
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            if (showBack) {
                toolbar.setNavigationIcon(R.drawable.ic_nav_back);
                toolbar.setNavigationContentDescription("Back");
                toolbar.setNavigationOnClickListener(v -> activity.finish());
            } else {
                toolbar.setNavigationIcon(null);
            }
        }
        AdaptiveUi.attach(activity);
    }
}
