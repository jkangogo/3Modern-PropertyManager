package com.threemsystems.rentmanager;

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
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.AuthFailureError;

import com.android.volley.RequestQueue ;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.vishnusivadas.advanced_httpurlconnection.PutData;
import java.util.ArrayList;
import android.content.SharedPreferences;
import java.util.Map;import java.util.HashMap;


public class AddUnit extends AppCompatActivity {
    Spinner Property,Electricity,Water;
    EditText unitCode,unitName,Description,unitRent,electMeter,waterMeter,serviceFee;
    Button Save,Close,Reset;
    ProgressBar progressBar;
	SharedPreferences propertypref ;
	private Spinner property_spinner;
	private ArrayList<String> properties;
	ArrayList<spinnerItems> spinnerlist = new ArrayList<>();
	private  JSONArray result;
	public static final String TAG_propertycode = "property_code";
	public static final String TAG_propertyname = "property_name";
	public static final String TAG_jsonarray = "result";
	String propertycode;	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unit);
        //Property=findViewById(R.id.spnUnitPropertyAdd);
        Electricity=findViewById(R.id.spnUnitElectAdd);
        Water=findViewById(R.id.spnUnitWaterSharedAdd);
        unitCode=findViewById(R.id.etUnitCodeAdd);
        unitName=findViewById(R.id.etUnitNameAdd);
        Description=findViewById(R.id.etUnitDescAdd);
        unitRent=findViewById(R.id.etUnitRentAdd);
        electMeter=findViewById(R.id.etUnitElectAdd);
        waterMeter=findViewById(R.id.etUnitWaterAdd);
        serviceFee=findViewById(R.id.etUnitServiceAdd);
        Save=findViewById(R.id.btnUnitsubmit);
        Close=findViewById(R.id.btnUnitclose);
        Reset=findViewById(R.id.btnUnitreset);
		progressBar = findViewById(R.id.unitaddprogress);
	
	properties = new ArrayList<String>();
    property_spinner = (Spinner) findViewById(R.id.spnUnitPropertyAdd);
  //  property_spinner.setOnItemSelectedListener(this);
	
        ArrayList<String> electricityList = new ArrayList<>();
        electricityList.add("Shared");
        electricityList.add("Individual");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, electricityList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Electricity.setAdapter(arrayAdapter);
        Electricity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String electricityItem = arrayAdapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayList<String> waterList = new ArrayList<>();
        waterList.add("Shared");
        waterList.add("Individual");
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, waterList);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Water.setAdapter(arrayAdapter);
        Water.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String waterItem = arrayAdapter1.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
	property_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

             spinnerItems spinerproperty = (spinnerItems) parent.getSelectedItem();
             propertycode=spinerproperty.getId();
			// Toast.makeText(getApplicationContext(), "Property Code: "+spinerproperty.getId()+",  Property Name : "+spinerproperty.getName(), Toast.LENGTH_SHORT).show();    
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {    
        }
    });

	    Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unitName.setText("");
                Description.setText("");
                unitRent.setText("");
                electMeter.setText("");
                waterMeter.setText("");
                serviceFee.setText("");
            }
        });

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DataEntry.class);
                startActivity(intent);
                finish();
            }
        });

	  Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Unit_code, Unit_name, Unit_desc, Property_code, 
				Unit_rent_amount, Electricity_meter, Electricity_metershare, 
				Water_meter, Water_metershare, Service_fee, Status;
                Unit_code = String.valueOf(unitCode.getText());
                Unit_name = String.valueOf(unitName.getText());
                Unit_desc = String.valueOf(Description.getText());
               // Property_code = String.valueOf(Property.getSelectedItem());
				Property_code = propertycode;
                Unit_rent_amount = String.valueOf(unitRent.getText());
                Electricity_meter = String.valueOf(electMeter.getText());
                Electricity_metershare = String.valueOf(Electricity.getSelectedItem());
                Water_meter = String.valueOf(waterMeter.getText());
                Water_metershare = String.valueOf(Water.getSelectedItem());
                Service_fee = String.valueOf(serviceFee.getText());
                String status="";
                String owner_id="";
				
 if(!Unit_code.equals("") && !Unit_name.equals("") && !Unit_desc.equals("") && !Property_code.equals("") && !Unit_rent_amount.equals("") && !Electricity_meter.equals("") && !Electricity_metershare.equals("") && !Water_meter.equals("") && !Water_metershare.equals("") && !Service_fee.equals("")) {

		progressBar.setVisibility(View.VISIBLE);
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
		@Override
		public void run() {
		String[] field = new String[10];
		field[0] = "unit_code";
		field[1] = "unit_name";
		field[2] = "unit_desc";
		field[3] = "property_code";
		field[4] = "unit_rent_amount";
		field[5] = "electricity_meter";
		field[6] = "electricity_metershare";
		field[7] = "water_meter";
		field[8] = "water_metershare";
		field[9] = "service_fee";
		//field[10] = "status";
		//field[11] = "owner_id";

		//Creating array for data
		String[] data = new String[10];
		data[0] = Unit_code;
		data[1] = Unit_name;
		data[2] = Unit_desc;
		data[3] = Property_code;
		data[4] = Unit_rent_amount;
		data[5] = Electricity_meter;
		data[6] = Electricity_metershare;
		data[7] = Water_meter;
		data[8] = Water_metershare;
		data[9] = Service_fee;
		//data[10] = status;
		//data[11] = owner_id;
	Config conf = com.threemsystems.rentmanager.Config.getInstance();
   // String url=conf.getSERVERURL()+"login.php";
	PutData putData = new PutData(conf.getSERVERURL()+"save_new_propertyunit.php", "POST", field, data);
	//PutData putData = new PutData("http://3modernsystems.com/threepmobileserver/save_new_propertyunit.php", "POST", field, data);
	
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
			//if (flag) {
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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

	propertypref = getSharedPreferences("user_details", MODE_PRIVATE);				
	String idNo=propertypref.getString("idNo","MisingID");	        
 //String url = "http://3modernsystems.com/threepmobileserver/list_properties.php?owner_id="+idNo;
	Config conf = com.threemsystems.rentmanager.Config.getInstance();
	String url = conf.getSERVERURL()+"list_properties.php";
	
	getspinnerData(url,idNo);
	//set the unitcode unique id
	long  unitcode=System.currentTimeMillis();	
	unitCode.setText(String.valueOf(unitcode));
}

//////////
private void getspinnerData(String url,String owner_id){	
    StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject j = null;
                    try {
                        j = new JSONObject(response);
                        result = j.getJSONArray(TAG_jsonarray);
                        getResult(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            })
		{
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String,String> map = new HashMap<String,String>();
            map.put("owner_id",owner_id);          
            return map;
        }
    };	
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
}
private void getResult(JSONArray j){	
    for(int i=0;i<j.length();i++){
        try {
            JSONObject json = j.getJSONObject(i);
           // properties.add(json.getString(json.getString(TAG_propertyname));			
			spinnerlist.add(new spinnerItems(json.getString(TAG_propertycode), json.getString(TAG_propertyname)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
   // property_spinner.setAdapter(new ArrayAdapter<String>(AddUnit.this, android.R.layout.simple_spinner_dropdown_item, properties));
	 property_spinner.setAdapter(new ArrayAdapter<spinnerItems>(AddUnit.this, android.R.layout.simple_spinner_dropdown_item, spinnerlist));
	 
}
}