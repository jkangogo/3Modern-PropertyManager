package com.threemsystems.rentmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.threemsystems.rentmanager.Holder.Expenditure;
import com.threemsystems.rentmanager.Holder.Invoices;
import com.threemsystems.rentmanager.Holder.Payments;
import com.threemsystems.rentmanager.Holder.Property;
import com.threemsystems.rentmanager.Holder.Statements;
import com.threemsystems.rentmanager.Holder.Tenancy;
import com.threemsystems.rentmanager.Holder.Units;

public class Reports extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        ScreenNav.bind(this);

        findViewById(R.id.btnProperty).setOnClickListener(v ->
                startActivity(new Intent(this, Property.class)));
        findViewById(R.id.btnUnits).setOnClickListener(v ->
                startActivity(new Intent(this, Units.class)));
        findViewById(R.id.btnTenancy).setOnClickListener(v ->
                startActivity(new Intent(this, Tenancy.class)));
        findViewById(R.id.btnPayments).setOnClickListener(v ->
                startActivity(new Intent(this, Payments.class)));
        findViewById(R.id.btnInvoices).setOnClickListener(v ->
                startActivity(new Intent(this, Invoices.class)));
        findViewById(R.id.btnStatements).setOnClickListener(v ->
                startActivity(new Intent(this, Statements.class)));
        findViewById(R.id.btnexpend).setOnClickListener(v ->
                startActivity(new Intent(this, Expenditure.class)));
    }
}
