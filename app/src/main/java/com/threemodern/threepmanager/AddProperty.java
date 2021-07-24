package com.threemodern.threepmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.vishnusivadas.advanced_httpurlconnection.PutData;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import android.content.SharedPreferences;

public class AddProperty extends AppCompatActivity {
    EditText pCode, ownerID, pName, pDesc, Tell, Address;
    Spinner ownerType;
    Button Save, Close, Reset;
    ProgressBar progressBar;
	SharedPreferences propertypref ;
	

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_property);
        pCode = findViewById(R.id.etPropertyAdd);
        ownerID = findViewById(R.id.etPropertyOIDAdd);
        pName = findViewById(R.id.etPropertyNameAdd);
        pDesc = findViewById(R.id.etPropertyDescAdd);
        Tell = findViewById(R.id.etPropertyTelAdd);
        Address = findViewById(R.id.etPropertyAddressAdd);
        ownerType = findViewById(R.id.spnPropertyOTypeAdd);
        Save = findViewById(R.id.btnPropertysubmit);
        Close = findViewById(R.id.btnPropertyclose);
        Reset = findViewById(R.id.btnPropertyreset);
		progressBar = findViewById(R.id.progress);
	//String unique_id= (String) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
		
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Individual");
        arrayList.add("Cooperate");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ownerType.setAdapter(arrayAdapter);
        ownerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String owner = arrayAdapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
		
			long  propertycode=System.currentTimeMillis();
			propertypref = getSharedPreferences("user_details", MODE_PRIVATE);				
			String idNo=propertypref.getString("idNo","MisingID");
			String ul=propertypref.getString("userlevel","MissingLevel");
			ownerID.setText(idNo);
			pCode.setText(String.valueOf(propertycode));
			//String message=idNo+" "+ul;
			//Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pName.setText("");
                pDesc.setText("");
                Tell.setText("");
                Address.setText("");
            }
        });

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DataEntry.class);
                startActivity(intent);
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
	public void onClick(View v) {
		//String msg0="Reached here0";
	//Toast.makeText(getApplicationContext(), msg0, Toast.LENGTH_LONG).show();
	
	String Property_code, Owner_identifier, Property_name, Property_desc, Property_type, Property_address, Property_tel;
		
	
	Property_code = String.valueOf(pCode.getText());
	Owner_identifier = String.valueOf(ownerID.getText());
	Property_name = String.valueOf(pName.getText());
	Property_desc = String.valueOf(pDesc.getText());
	Property_type = String.valueOf(ownerType.getSelectedItem());
	Property_address = String.valueOf(Address.getText());
	Property_tel = String.valueOf(Tell.getText());
	String msg1="Reached here1";
	Toast.makeText(getApplicationContext(), msg1, Toast.LENGTH_LONG).show();	
	String property_id = "";
	//String msg2="Reached here2";
	//Toast.makeText(getApplicationContext(), msg2, Toast.LENGTH_LONG).show();
	
	if (!Property_code.equals("") && !Owner_identifier.equals("") && !Property_name.equals("") && !Property_desc.equals("") && !Property_type.equals("") && !Property_address.equals("") && !Property_tel.equals("")) {
             progressBar.setVisibility(View.VISIBLE);
             Handler handler = new Handler(Looper.getMainLooper());					
                handler.post(new Runnable() {
                    @Override
        public void run() {		
					
                        String[] field = new String[8];
                        field[0] = "property_code";
                        field[1] = "owner_identifier";
                        field[2] = "property_name";
                        field[3] = "property_desc";
                        field[4] = "property_type";
                        field[5] = "property_tel";
                        field[6] = "property_address";
                        field[7] = "property_id";

                        //Creating array for data
                        String[] data = new String[8];
                        data[0] = Property_code;
                        data[1] = Owner_identifier;
                        data[2] = Property_name;
                        data[3] = Property_desc;
                        data[4] = Property_type;
                        data[5] = Property_tel;
                        data[6] = Property_address;
                        data[7] = property_id;
Config conf = com.threemodern.threepmanager.Config.getInstance();
	String url = conf.getSERVERURL()+"save_new_property.php"; 
	
    //PutData putData = new PutData("http://3modernsystems.com/threepmobileserver/save_new_property.php", "POST", field, data);
	 PutData putData = new PutData(url, "POST", field, data);
	if (putData.startPut()) {
		if (putData.onComplete()) {
			progressBar.setVisibility(View.GONE);
			String result = putData.getResult();
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
			JSONObject jObject;
			String flag = "", message = "";
			try {
				jObject = new JSONObject(result);
				flag = jObject.getString("success");
				message = jObject.getString("message");
			} catch (JSONException e) {
				e.printStackTrace();
				System.out.println("JSON Exception");
			}
			if (flag.equals("true")) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
		Intent intent = new Intent(getApplicationContext(),MainActivity.class);
		startActivity(intent);
		finish();
			} else {
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