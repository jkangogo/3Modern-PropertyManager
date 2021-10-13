package com.threemsystems.rentmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

<<<<<<< HEAD
import com.threemsystems.rentmanager.Holder.MessageActivity;

public class DataEntry extends AppCompatActivity {
    Button addProperty, newUnit, newTenant, Payment,newInvoice;
	Button 	journalEntry, expenditure, back, message,BunitServices;
=======
public class DataEntry extends AppCompatActivity {
<<<<<<< HEAD
    Button addProperty, newUnit, newTenant, Payment,newInvoice;
	Button 	journalEntry, expenditure, back, message,BunitServices;
=======
    Button addProperty, newUnit, newTenant, Payment,newInvoice, journalEntry, expenditure, back, message;
>>>>>>> 9f40e6ad2715ec2eeb495b86d592bbfb11e408a8
>>>>>>> 0ccf562acf0afdfaf3db505a7994472c700c3619
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
        message=(Button)findViewById(R.id.btnMessage);
<<<<<<< HEAD
		BunitServices=(Button)findViewById(R.id.UnitServices);
		
=======
<<<<<<< HEAD
		BunitServices=(Button)findViewById(R.id.UnitServices);
		
=======
>>>>>>> 9f40e6ad2715ec2eeb495b86d592bbfb11e408a8
>>>>>>> 0ccf562acf0afdfaf3db505a7994472c700c3619
        propertypref = getSharedPreferences("user_details", MODE_PRIVATE);
        String idNo=propertypref.getString("idNo","MisingID");

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                startActivity(intent);
            }
        });

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
<<<<<<< HEAD
                    finish();
=======
<<<<<<< HEAD
                    finish();
=======
>>>>>>> 9f40e6ad2715ec2eeb495b86d592bbfb11e408a8
>>>>>>> 0ccf562acf0afdfaf3db505a7994472c700c3619
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
<<<<<<< HEAD
                    finish();
=======
<<<<<<< HEAD
                    finish();
=======
>>>>>>> 9f40e6ad2715ec2eeb495b86d592bbfb11e408a8
>>>>>>> 0ccf562acf0afdfaf3db505a7994472c700c3619
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
                    Intent intent = new Intent(getApplicationContext(), com.threemsystems.rentmanager.AddTenant.class);
                    startActivity(intent);
<<<<<<< HEAD
                    finish();
=======
<<<<<<< HEAD
                    finish();
=======
>>>>>>> 9f40e6ad2715ec2eeb495b86d592bbfb11e408a8
>>>>>>> 0ccf562acf0afdfaf3db505a7994472c700c3619
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
<<<<<<< HEAD
                    finish();
=======
<<<<<<< HEAD
                    finish();
=======
>>>>>>> 9f40e6ad2715ec2eeb495b86d592bbfb11e408a8
>>>>>>> 0ccf562acf0afdfaf3db505a7994472c700c3619

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
<<<<<<< HEAD
                finish();
=======
<<<<<<< HEAD
                finish();
=======
>>>>>>> 9f40e6ad2715ec2eeb495b86d592bbfb11e408a8
>>>>>>> 0ccf562acf0afdfaf3db505a7994472c700c3619
            }
        });

        journalEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), JounalEntry.class);
                startActivity(i);
<<<<<<< HEAD
                finish();
=======
<<<<<<< HEAD
                finish();
=======
>>>>>>> 9f40e6ad2715ec2eeb495b86d592bbfb11e408a8
>>>>>>> 0ccf562acf0afdfaf3db505a7994472c700c3619
            }
        });

        expenditure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewExpenditure.class);
                startActivity(i);
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> 0ccf562acf0afdfaf3db505a7994472c700c3619
                finish();
            }
        });
		 BunitServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddUnitService.class);
                startActivity(i);
                finish();
<<<<<<< HEAD
=======
=======
>>>>>>> 9f40e6ad2715ec2eeb495b86d592bbfb11e408a8
>>>>>>> 0ccf562acf0afdfaf3db505a7994472c700c3619
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}