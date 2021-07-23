package com.kangogo.threepmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kangogo.threepmanager.Holder.Payments;
import com.kangogo.threepmanager.Holder.Property;
import com.kangogo.threepmanager.Holder.Tenancy;
import com.kangogo.threepmanager.Holder.Units;

public class DataEntry extends AppCompatActivity {
    Button addProperty, newUnit, newTenant, Payment,newInvoice, journalEntry, expenditure, back;
    SharedPreferences propertypref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry);

        addProperty = findViewById(R.id.btnAddProperty);
        newUnit = findViewById(R.id.btnNewUnits);
        newTenant = findViewById(R.id.btnNewTenancy);
        Payment = findViewById(R.id.btnPayment);
        newInvoice = findViewById(R.id.btnNewInv);
        journalEntry = findViewById(R.id.btnJournalEntry);
        expenditure = findViewById(R.id.btnExpenditure);
        back = findViewById(R.id.back);
        propertypref = getSharedPreferences("user_details", MODE_PRIVATE);
        String idNo=propertypref.getString("idNo","MisingID");

        addProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if user level<9
                propertypref = getSharedPreferences("user_details", MODE_PRIVATE);
                String userL=propertypref.getString("userlevel","MisingID");
                int ul=Integer.parseInt(userL);
                if(ul==9){
                    Intent intent= new Intent(getApplicationContext(), AddProperty.class);
                    startActivity(intent);
                    finish();
                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(DataEntry.this);
                    builder.setMessage("You do not have required permission to create new property")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        newUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ////
                propertypref = getSharedPreferences("user_details", MODE_PRIVATE);
                String userL=propertypref.getString("userlevel","MisingID");
                int ul=Integer.parseInt(userL);
                if(ul==9){
                    Intent intent = new Intent( getApplicationContext(), AddUnit.class);
                    startActivity(intent);
                    finish();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(DataEntry.this);
                    builder.setMessage("You do not have required permission to create new units")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        newTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                propertypref = getSharedPreferences("user_details", MODE_PRIVATE);
                String userL=propertypref.getString("userlevel","MisingID");
                int ul=Integer.parseInt(userL);
                if(ul==9){
                    Intent intent = new Intent(getApplicationContext(), com.kangogo.threepmanager.AddTenant.class);
                    startActivity(intent);
                    finish();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(DataEntry.this);
                    builder.setMessage("You do not have required permission to create new tenant")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }



            }
        });

        Payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                propertypref = getSharedPreferences("user_details", MODE_PRIVATE);
                String userL=propertypref.getString("userlevel","MisingID");
                int ul=Integer.parseInt(userL);
                if(ul==9){
                    Intent intent = new Intent(getApplicationContext(), AddPayment.class);
                    startActivity(intent);
                    finish();

                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(DataEntry.this);
                    builder.setMessage("You do not have required permission to create a payment entry")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        newInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewInvoice.class);
                startActivity(i);
            }
        });

        journalEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), JounalEntry.class);
                startActivity(i);
            }
        });

        expenditure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewExpenditure.class);
                startActivity(i);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

    }
}