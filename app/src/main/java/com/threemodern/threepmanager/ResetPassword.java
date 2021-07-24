package com.threemodern.threepmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;
import android.os.Looper;
import com.vishnusivadas.advanced_httpurlconnection.PutData;




public class ResetPassword extends AppCompatActivity {
    private EditText username, currPassword, NewPassword, ConfrmNewPassword;
    private Button cancel, ChangePassword;
	Config conf = com.threemodern.threepmanager.Config.getInstance();
	 private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        username = findViewById(R.id.etUser);
        currPassword = findViewById(R.id.etcurrPass);
        NewPassword = findViewById(R.id.etnewPass);
        ConfrmNewPassword = findViewById(R.id.etconfrmnp);
        cancel = findViewById(R.id.btcancel);
        ChangePassword = findViewById(R.id.btchangePassword);
		 progressBar = findViewById(R.id.progressBar);
		 cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
                builder.setMessage("Are you sure you want to cancel?")
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
		 ChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {	
				
				  AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
                builder.setMessage("Are you sure you want to change your password?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
               String user_name, cur_password, new_password, confirm_password;				
                user_name = String.valueOf(username.getText());
                cur_password = String.valueOf(currPassword.getText());
                new_password = String.valueOf(NewPassword.getText());
                confirm_password = String.valueOf(ConfrmNewPassword.getText());                
				
    if(!user_name.equals("") && !cur_password.equals("") && !new_password.equals("") && !confirm_password.equals("")) {

	if(new_password.equals(confirm_password)){
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[4];
                            field[0] = "usn";
                            field[1] = "cpswd";
                            field[2] = "npswd";
                            field[3] = "confirm_password";                                                  
                            String[] data = new String[6];
                            data[0] = user_name;
                            data[1] = cur_password;
                            data[2] = new_password;
                            data[3] = confirm_password;                                                
		String url = conf.getSERVERURL()+"save_changePassword.php";
       // PutData putData = new PutData("http://3modernsystems.com/threepmobileserver/save_new_owner.php", "POST", field, data);
		 PutData putData = new PutData(url, "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                    JSONObject jObject; String flag="",message="";
                                    try{
                                        jObject = new JSONObject(result);
                                        flag = jObject.getString("success");
                                        message = jObject.getString("message");

                                    }catch(JSONException e){
                                        e.printStackTrace();
                                        System.out.println("JSON Exception");
                                    }	  
        if (flag.equals("true")) {                                        
					Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
					startActivity(intent);
					finish();
					Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                    });
					}else{
						//the entered new passwords are not matching
				Toast.makeText(getApplicationContext(), "The entered new passwords are not matching", Toast.LENGTH_SHORT).show();
					}
                }
                else{
                    Toast.makeText(getApplicationContext(), "All Fields are Required", Toast.LENGTH_SHORT).show();
                }
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
    }
}

/////////////
			