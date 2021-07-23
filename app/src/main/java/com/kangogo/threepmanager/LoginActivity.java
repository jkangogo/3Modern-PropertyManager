package com.kangogo.threepmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;


import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;
    private TextView copyrightTV;
    private Button login, register, cancel;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
   SharedPreferences pref ;	
   Intent intent;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
		
	//pref = getSharedPreferences("user_details",MODE_PRIVATE);
        username=findViewById(R.id.etusername);
        password = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
        register = findViewById(R.id.btnRegister);
        cancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progress);

        //username.setText("0722977672");
       // password.setText("0722977672");

        copyrightTV = findViewById(R.id.copyrightTV);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        copyrightTV.setText("3PManager © " + year + ", All rights reserved.");

	 pref = getSharedPreferences("user_details", MODE_PRIVATE);
	intent = new Intent(LoginActivity.this, MainActivity.class);
	
       if (pref.contains("username") && pref.contains("password")) {
           startActivity(intent);
      }
	 
	   
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                String Username, Password,timedout;
                Username = String.valueOf(username.getText());
                Password = String.valueOf(password.getText());
                timedout="no";
				
                if(!Username.equals("") && !Password.equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[3];
                            field[0] = "usn";
                            field[1] = "pswd";
                            field[2] = "timedout";
                            //Creating array for data
                            String[] data = new String[3];
                            data[0] = Username;
                            data[1] = Password;
                            data[2] = timedout;
		
	Config conf = com.kangogo.threepmanager.Config.getInstance();
    String url=conf.getSERVERURL()+"login.php";
    PutData putData = new PutData(url, "POST", field, data);
	
	//PutData putData = new PutData("http://41.89.65.22/threepmobileserver/login.php", "POST", field, data);

	if (putData.startPut()) {
		if (putData.onComplete()) {
			progressBar.setVisibility(View.GONE);
			String result = putData.getResult();
			result=result.replace("\"","");
			String idno="";
			String  name="";String outcome="";
			String  userlevel="";
			String[] arrayresult=result.split("_");
			if(arrayresult.length>3){
				idno=arrayresult[0];
				name=arrayresult[1];
				outcome=arrayresult[2];
				userlevel=arrayresult[4];
			}

	if (outcome.equals("success")) {		
	//result=result +" "+	unique_id;		
		//Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("idNo",idno);			
			editor.putString("userlevel",userlevel);
			editor.commit();		
		Intent intent = new Intent(getApplicationContext(),MainActivity.class);
		startActivity(intent);
		finish();
	}
			else {
				Toast.makeText(getApplicationContext(), "Incorrect login details", Toast.LENGTH_LONG).show();
			}
		}
	}
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "All Fields are Required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}