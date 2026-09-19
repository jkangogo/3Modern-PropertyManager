package com.threemsystems.rentmanager;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportTableAdapter extends RecyclerView.Adapter<ReportTableAdapter.RowHolder> {
    public interface Listener {
        void onRowClick(int position, HashMap<String, String> row);
    }

    private final List<ReportColumn> columns = new ArrayList<>();
    private final List<HashMap<String, String>> rows = new ArrayList<>();
    private Listener listener;
    private int selected = RecyclerView.NO_POSITION;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setColumns(List<ReportColumn> next) {
        columns.clear();
        if (next != null) {
            columns.addAll(next);
        }
        notifyDataSetChanged();
    }

    public void setRows(List<HashMap<String, String>> next) {
        rows.clear();
        selected = RecyclerView.NO_POSITION;
        if (next != null) {
            rows.addAll(next);
        }
        notifyDataSetChanged();
    }

    public HashMap<String, String> selectedRow() {
        if (selected < 0 || selected >= rows.size()) {
            return null;
        }
        HashMap<String, String> row = rows.get(selected);
        return ReportSupport.isFooterRow(row) ? null : row;
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout root = new LinearLayout(parent.getContext());
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setGravity(android.view.Gravity.CENTER_VERTICAL);
        root.setBackgroundResource(R.drawable.bg_list_row);
        int pad = dp(parent, 8);
        root.setPadding(pad, pad, pad, pad);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(parent, 6);
        root.setLayoutParams(params);
        return new RowHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int position) {
        holder.bind(columns, rows.get(position), position == selected);
        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }
            HashMap<String, String> row = rows.get(adapterPosition);
            if (ReportSupport.isFooterRow(row)) {
                return;
            }
            int previous = selected;
            selected = adapterPosition;
            if (previous != RecyclerView.NO_POSITION) {
                notifyItemChanged(previous);
            }
            notifyItemChanged(selected);
            if (listener != null) {
                listener.onRowClick(adapterPosition, row);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static int dp(View view, int value) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, value, view.getResources().getDisplayMetrics()));
    }

    static final class RowHolder extends RecyclerView.ViewHolder {
        private final LinearLayout root;

        RowHolder(@NonNull LinearLayout itemView) {
            super(itemView);
            root = itemView;
        }

        void bind(List<ReportColumn> columns, HashMap<String, String> row, boolean selected) {
            root.setActivated(selected);
            boolean footer = ReportSupport.isFooterRow(row);
            ensureCells(columns);
            for (int i = 0; i < columns.size(); i++) {
                ReportColumn column = columns.get(i);
                TextView cell = (TextView) root.getChildAt(i);
                String value = row.get(column.key);
                cell.setText(value == null ? "" : value);
                cell.setGravity(column.gravity);
                cell.setTypeface(null, footer || column.emphasize ? Typeface.BOLD : Typeface.NORMAL);
                cell.setTextColor(androidx.core.content.ContextCompat.getColor(
                        cell.getContext(),
                        column.emphasize ? R.color.teal_700 : R.color.text_primary));
                cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, footer ? 12 : 13);
            }
        }

        private void ensureCells(List<ReportColumn> columns) {
            if (root.getTag() == columns && root.getChildCount() == columns.size()) {
                return;
            }
            root.setTag(columns);
            root.removeAllViews();
            for (ReportColumn column : columns) {
                TextView cell = new TextView(root.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        dp(root, column.widthDp), ViewGroup.LayoutParams.WRAP_CONTENT);
                cell.setLayoutParams(params);
                cell.setPadding(dp(root, 6), 0, dp(root, 6), 0);
                cell.setMaxLines(2);
                cell.setEllipsize(android.text.TextUtils.TruncateAt.END);
                root.addView(cell);
            }
        }
    }
}
