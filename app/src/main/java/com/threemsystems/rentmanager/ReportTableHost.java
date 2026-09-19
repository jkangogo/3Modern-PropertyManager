package com.threemsystems.rentmanager;

import android.app.Activity;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class ReportTableHost {
    private final LinearLayout header;
    private final TextView empty;
    private final ReportTableAdapter adapter = new ReportTableAdapter();
    private List<ReportColumn> columns = new ArrayList<>();

    private ReportTableHost(Activity activity) {
        header = activity.findViewById(R.id.reportHeader);
        RecyclerView list = activity.findViewById(R.id.reportList);
        empty = activity.findViewById(R.id.reportEmpty);
        list.setLayoutManager(new LinearLayoutManager(activity));
        list.setAdapter(adapter);
        list.setHasFixedSize(false);
    }

    public static ReportTableHost attach(Activity activity) {
        return new ReportTableHost(activity);
    }

    public ReportTableHost setEmpty(String message) {
        empty.setText(message);
        return this;
    }

    public ReportTableHost setListener(ReportTableAdapter.Listener listener) {
        adapter.setListener(listener);
        return this;
    }

    public void setColumns(ReportColumn... next) {
        setColumns(Arrays.asList(next));
    }

    public void setColumns(List<ReportColumn> next) {
        columns = next == null ? new ArrayList<>() : new ArrayList<>(next);
        adapter.setColumns(columns);
        header.removeAllViews();
        for (ReportColumn column : columns) {
            TextView title = new TextView(header.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dp(column.widthDp), ViewGroup.LayoutParams.WRAP_CONTENT);
            title.setLayoutParams(params);
            title.setText(column.title);
            title.setAllCaps(true);
            title.setGravity(column.gravity);
            title.setTextColor(androidx.core.content.ContextCompat.getColor(header.getContext(), R.color.table_header_text));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            title.setLetterSpacing(0.04f);
            title.setTypeface(title.getTypeface(), android.graphics.Typeface.BOLD);
            title.setPadding(dp(8), dp(12), dp(8), dp(12));
            title.setMaxLines(2);
            header.addView(title);
        }
    }

    public void setRows(List<HashMap<String, String>> rows) {
        adapter.setRows(rows);
        boolean emptyList = rows == null || rows.isEmpty();
        empty.setVisibility(emptyList ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    public HashMap<String, String> selectedRow() {
        return adapter.selectedRow();
    }

    private int dp(int value) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, header.getResources().getDisplayMetrics()));
    }
}
