package com.threemodern.threepmanager;

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
                Intent i = new Intent(getApplicationContext(), com.threemodern.threepmanager.Holder.Property.class);
                startActivity(i);
            }
        });

        Units.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.threemodern.threepmanager.Holder.Units.class);
                startActivity(i);
            }
        });

        Tenancy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.threemodern.threepmanager.Holder.Tenancy.class);
                startActivity(i);
            }
        });

        Payments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.threemodern.threepmanager.Holder.Payments.class);
                startActivity(i);
            }
        });

        Invoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.threemodern.threepmanager.Holder.Invoices.class);
                startActivity(i);
            }
        });

        Statements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.threemodern.threepmanager.Holder.Statements.class);
                startActivity(i);
            }
        });

        Expenditure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.threemodern.threepmanager.Holder.Expenditure.class);
                startActivity(i);
            }
        });

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }
}