package com.threemodern.threepmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;




public class MainActivity extends AppCompatActivity {
    Button dataEntry,Reports,resetPass, Exit;
    TextView copyrightTV;
	
	 Intent intent;
	public static final String userdetails = "userdetails" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataEntry = findViewById(R.id.btnDataentry);
        Reports = findViewById(R.id.btnReports);
        resetPass = findViewById(R.id.btnresetPassword);
        Exit = findViewById(R.id.btnExit);
		

        copyrightTV = findViewById(R.id.copyrightTV2);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        copyrightTV.setText("3PManager © " + year + ", All rights reserved.");


        dataEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DataEntry.class);
                startActivity(i);
            }
        });

        Reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Reports.class);
                startActivity(i);
            }
        });

        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				  AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to log out?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                 finish();
				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
				/////////////
                
            }
        });
		 resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ResetPassword.class);
                startActivity(i);
            }
        });

    }
	
}