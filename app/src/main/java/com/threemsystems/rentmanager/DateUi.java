package com.threemsystems.rentmanager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Locale;

public final class DateUi {
    public interface OnPicked {
        void onDate(String isoDate);
    }

    private DateUi() {}

    public static String format(int year, int monthZeroBased, int day) {
        return String.format(Locale.US, "%04d-%02d-%02d", year, monthZeroBased + 1, day);
    }

    public static String format(Calendar calendar) {
        return format(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
    }

    public static Calendar parse(String text) {
        Calendar calendar = Calendar.getInstance();
        if (text == null) {
            return calendar;
        }
        String trimmed = text.trim();
        try {
            if (trimmed.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                String[] parts = trimmed.split("-");
                calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
            } else if (trimmed.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                String[] parts = trimmed.split("/");
                calendar.set(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[0]));
            }
        } catch (Exception ignored) {
        }
        return calendar;
    }

    public static void bindPicker(Context context, EditText field, OnPicked listener) {
        field.setFocusable(false);
        field.setFocusableInTouchMode(false);
        field.setCursorVisible(false);
        field.setKeyListener(null);
        field.setOnClickListener(v -> {
            Calendar calendar = parse(String.valueOf(field.getText()));
            new DatePickerDialog(
                    context,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        String iso = format(year, monthOfYear, dayOfMonth);
                        field.setText(iso);
                        if (listener != null) {
                            listener.onDate(iso);
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }
}
