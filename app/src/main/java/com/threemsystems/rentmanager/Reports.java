package com.threemsystems.rentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Reports extends AppCompatActivity {
    Button Property, Units, Tenancy, Payments, Invoices, Statements, Expenditure, Back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        Property = findViewById(R.id.btnProperty);
        Units = findViewById(R.id.btnUnits);
        Tenancy = findViewById(R.id.btnTenancy);
        Payments = findViewById(R.id.btnPayments);
        Invoices = findViewById(R.id.btnInvoices);
        Statements = findViewById(R.id.btnStatements);
        Expenditure = findViewById(R.id.btnexpend);
        Back= findViewById(R.id.btnback);

        Property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.threemsystems.rentmanager.Holder.Property.class);
                startActivity(i);
            }
        });

        Units.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.threemsystems.rentmanager.Holder.Units.class);
                startActivity(i);
            }
        });

        Tenancy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.threemsystems.rentmanager.Holder.Tenancy.class);
                startActivity(i);
            }
        });

        Payments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.threemsystems.rentmanager.Holder.Payments.class);
                startActivity(i);
            }
        });

        Invoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.threemsystems.rentmanager.Holder.Invoices.class);
                startActivity(i);
            }
        });

        Statements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.threemsystems.rentmanager.Holder.Statements.class);
                startActivity(i);
            }
        });

        Expenditure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.threemsystems.rentmanager.Holder.Expenditure.class);
                startActivity(i);
            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}