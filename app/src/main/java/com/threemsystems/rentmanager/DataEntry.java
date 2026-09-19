package com.threemsystems.rentmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.threemsystems.rentmanager.Holder.MessageActivity;

public class DataEntry extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry);
        ScreenNav.bind(this);

        findViewById(R.id.btnMessage).setOnClickListener(v ->
                openIfAllowed(MessageActivity.class, "You do not have permission to send messages."));
        findViewById(R.id.btnAddProperty).setOnClickListener(v ->
                openIfAllowed(AddProperty.class, "You do not have permission to create a property."));
        findViewById(R.id.btnNewUnits).setOnClickListener(v ->
                openIfAllowed(AddUnit.class, "You do not have permission to create units."));
        findViewById(R.id.btnNewTenancy).setOnClickListener(v ->
                openIfAllowed(AddTenant.class, "You do not have permission to create a tenant."));
        findViewById(R.id.btnPayment).setOnClickListener(v ->
                openIfAllowed(AddPayment.class, "You do not have permission to record a payment."));
        findViewById(R.id.btnNewInv).setOnClickListener(v ->
                openIfAllowed(NewInvoice.class, "You do not have permission to create an invoice."));
        findViewById(R.id.btnJournalEntry).setOnClickListener(v ->
                openIfAllowed(JounalEntry.class, "You do not have permission to post a journal entry."));
        findViewById(R.id.btnExpenditure).setOnClickListener(v ->
                openIfAllowed(NewExpenditure.class, "You do not have permission to record expenditure."));
        findViewById(R.id.UnitServices).setOnClickListener(v ->
                openIfAllowed(AddUnitService.class, "You do not have permission to assign unit services."));
    }

    private void openIfAllowed(Class<?> screen, String deniedMessage) {
        if (SessionManager.get(this).canManagePortfolio()) {
            startActivity(new Intent(this, screen));
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage(deniedMessage)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss())
                .show();
    }
}
