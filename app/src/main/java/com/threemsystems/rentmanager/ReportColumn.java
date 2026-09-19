package com.threemsystems.rentmanager;

import android.view.Gravity;

public final class ReportColumn {
    public final String key;
    public final String title;
    public final int widthDp;
    public final int gravity;
    public final boolean emphasize;

    private ReportColumn(String key, String title, int widthDp, int gravity, boolean emphasize) {
        this.key = key;
        this.title = title;
        this.widthDp = widthDp;
        this.gravity = gravity;
        this.emphasize = emphasize;
    }

    public static ReportColumn of(String key, String title, int widthDp) {
        return new ReportColumn(key, title, widthDp, Gravity.START | Gravity.CENTER_VERTICAL, false);
    }

    public static ReportColumn center(String key, String title, int widthDp) {
        return new ReportColumn(key, title, widthDp, Gravity.CENTER, false);
    }

    public static ReportColumn end(String key, String title, int widthDp) {
        return new ReportColumn(key, title, widthDp, Gravity.END | Gravity.CENTER_VERTICAL, false);
    }

    public static ReportColumn money(String key, String title, int widthDp) {
        return new ReportColumn(key, title, widthDp, Gravity.END | Gravity.CENTER_VERTICAL, true);
    }
}
