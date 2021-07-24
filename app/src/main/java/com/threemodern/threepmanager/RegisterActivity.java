package com.threemodern.threepmanager;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class RegisterActivity extends AppCompatActivity {
    private EditText Id, telNo, address, names;
    private Spinner ownertype ;
    private Button Submit, Close, Reset;
    private TextView tvlogin;
    private ProgressBar progressBar;
    String[] ownertypes = { "Individual", "Cooperate"};
	
	Config conf = com.threemodern.threepmanager.Config.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Id = findViewById(R.id.etID);
        telNo = findViewById(R.id.etTel);
        address = findViewById(R.id.etAddress);
        // password = findViewById(R.id.etpwd);
        names = findViewById(R.id.etnames);
        //ownertype = findViewById(R.id.etown);
        ownertype = (Spinner) findViewById(R.id.ownertype);
        tvlogin = findViewById(R.id.textlogin);
        Submit = findViewById(R.id.btsubmit);
        progressBar = findViewById(R.id.progressBar);
        Close = findViewById(R.id.btclose);
        Reset = findViewById(R.id.btnreset);
        // username = findViewById(R.id.etUser);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ownertypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ownertype.setAdapter(adapter);
        // ownertype.setOnItemSelectedListener(this);

        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }


        });
        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
//AlertDialog.Builder messageBox = new AlertDialog.Builder(youractivityname.this);
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

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Id_passport, TelNo, Names, Address, Ownertype;
                Id_passport = String.valueOf(Id.getText());
                TelNo = String.valueOf(telNo.getText());
                Names = String.valueOf(names.getText());
                Address = String.valueOf(address.getText());
                Ownertype = String.valueOf(ownertype.getSelectedItem());
                String owner_id="";

                if(!Id_passport.equals("") && !TelNo.equals("") && !Names.equals("") && !Address.equals("") && !Ownertype.equals("") ) {

                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[6];
                            field[0] = "owner_identifier";
                            field[1] = "owner_tel";
                            field[2] = "owner_names";
                            field[3] = "owner_address";
                            field[4] = "owner_type";
                            //field[5] = "username";
                            //field[6] = "password";
                            field[5] = "owner_id";

                            //Creating array for data
                            String[] data = new String[6];
                            data[0] = Id_passport;
                            data[1] = TelNo;
                            data[2] = Names;
                            data[3] = Address;
                            data[4] = Ownertype;
                            //data[5] = Username;
                            //data[6] = Password;
                            data[5] = owner_id;
                            // PutData putData = new PutData("http://172.16.1.214/server/save_new_user.php", "POST", field, data);
	String url = conf.getSERVERURL()+"save_new_owner.php";
  //PutData putData = new PutData("http://3modernsystems.com/threepmobileserver/save_new_owner.php", "POST", field, data);
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
	   /*
	   result=result.replace("\"","");
	   String[] arrayresult=result.split("_");
	  String outcome="";
	  if(arrayresult.length>1){
			outcome=arrayresult[0];
		}*/
					if (flag.equals("true")) {
						//if (flag) {
						Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
						Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
						startActivity(intent);
						finish();
					}
					else {
                                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
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
