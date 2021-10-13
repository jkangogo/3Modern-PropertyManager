package com.threemsystems.rentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.vishnusivadas.advanced_httpurlconnection.PutData;
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
import java.util.ArrayList;
import java.util.Calendar;
import android.content.SharedPreferences;
import java.util.Map;import java.util.HashMap;

public class AddUnitService extends AppCompatActivity {
    DatePickerDialog picker, picker1;
    private  Spinner property_spinner,servicetype_spinner,tenantType;
    private  Spinner Property,propertyunit_spinner,Status;
    EditText previousreadEdit,currentRead,etConsumption, etRate,etServiceCost,service_monthText;

    Button Save,Close,Reset;
    ProgressBar progressBar;
    SharedPreferences propertypref ;
   
    private ArrayList<String> properties;
    ArrayList<spinnerItems> spinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
	 ArrayList<spinnerItems> servicespinnerlist = new ArrayList<>();
    private JSONArray result;
    public static final String TAG_propertycode = "property_code";
    public static final String TAG_propertyname = "property_name";
    public static final String TAG_jsonarray = "result";
    String propertycode,propertyname,unitcode,unitname,service_month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unitservice);
        tenantType=findViewById(R.id.spnTenantTypeAdd);
        property_spinner=findViewById(R.id.spnUnitservicePAdd);
        propertyunit_spinner=findViewById(R.id.spnUnitservicePUAdd);
		servicetype_spinner=findViewById(R.id.spServiveTypeAdd);
		service_monthText=findViewById(R.id.ServiceMonthAdd);
		previousreadEdit=findViewById(R.id.previousread);
        currentRead=findViewById(R.id.currentRead);
		etConsumption=findViewById(R.id.etConsumption);
	    etRate=findViewById(R.id.etRate);
		etServiceCost=findViewById(R.id.etServiceCost);		
        Status=findViewById(R.id.spnServiceStatusAdd);        
        Save=findViewById(R.id.btServicesubmit);
        Close=findViewById(R.id.btnServiceclose);
        Reset=findViewById(R.id.btnServicereset);
        progressBar = findViewById(R.id.unitserviceaddprogress);

        service_monthText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(AddUnitService.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        service_monthText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        service_month=year  + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }
                }, year, month, day);
                picker.show();
            }
        });

        ArrayList<String> StatusList = new ArrayList<>();
        StatusList.add("Active");
        StatusList.add("Inactive");
        ArrayAdapter<String> statusListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StatusList);
        statusListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Status.setAdapter(statusListAdapter);
        Status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String StatusItem = statusListAdapter.getItem(position).toString();
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
                propertyname=spinerproperty.getName();
                //call the method to populate unitcode spinner
                Config conf = com.threemsystems.rentmanager.Config.getInstance();
                String url2 = conf.getSERVERURL()+"list_propertyunits.php";
                //String url = "http://3modernsystems.com/threepmobileserver/list_propertyunits.php";
                getUnitsspinnerData(url2,propertycode);
                // Toast.makeText(getApplicationContext(), "Property Code: "+spinerproperty.getId()+",  Property Name : "+spinerproperty.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        propertyunit_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerItems spinerunitproperty = (spinnerItems) parent.getSelectedItem();
                unitcode=spinerunitproperty.getId();
                unitname=spinerunitproperty.getName();
                //call the method to populate unitcode spinner
                //String url = "http://3modernsystems.com/threepmobileserver/list_propertyunits.php";
                //	 getUnitsspinnerData(url,propertycode);
                // Toast.makeText(getApplicationContext(), "Property Code: "+spinerproperty.getId()+",  Property Name : "+spinerproperty.getName(), Toast.LENGTH_SHORT).show();
		Config conf = com.threemsystems.rentmanager.Config.getInstance();
        String url = conf.getSERVERURL()+"list_rentalservices.php";
			getServicespinnerData(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
		servicetype_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerItems spinerproperty = (spinnerItems) parent.getSelectedItem();
                propertycode=spinerproperty.getId();
                propertyname=spinerproperty.getName();
                //call the method to populate unitcode spinner
               // Config conf = com.threemsystems.rentmanager.Config.getInstance();
               // String url2 = conf.getSERVERURL()+"list_propertyunits.php";
                //String url = "http://3modernsystems.com/threepmobileserver/list_propertyunits.php";
               // getUnitsspinnerData(url2,propertycode);
                // Toast.makeText(getApplicationContext(), "Property Code: "+spinerproperty.getId()+",  Property Name : "+spinerproperty.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				previousreadEdit.setText("");
				currentRead.setText("");
				etConsumption.setText("");
				etRate.setText("");
				etServiceCost.setText("");
				service_monthText.setText("");               
			  
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
				 String  		Property_code =propertycode;
				 String  		Unit_code = unitcode;				
                 String         previousread= String.valueOf(previousreadEdit.getText());
				 String 		currentread= String.valueOf(currentRead.getText());
				 String 		consumption= String.valueOf(etConsumption.getText());
				 String 		service_date= String.valueOf(service_monthText.getText());
				 String 		rate= String.valueOf(etRate.getText());
				 String 		service_cost= String.valueOf(etServiceCost.getText());
				 String 		status= String.valueOf(Status.getSelectedItem());
				 String 		servicetype= String.valueOf(servicetype_spinner.getSelectedItem());
				 
                if(!status.equals("") || !Unit_code.equals("") || !service_cost.equals("") 
					&& !service_date.equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[10];
                            field[0] = "Property_code";
                            field[1] = "Unit_code";
                            field[2] = "previousread";
                            field[3] = "currentread";
                            field[4] = "consumption";
                            field[5] = "service_date";
                            field[6] = "rate";
                            field[7] = "service_cost";
                            field[8] = "status"; 
							field[9] = "servicetype"; 						
                          
                            //Creating array for data
                            String[] data = new String[10];
                            data[0] = Property_code;
                            data[1] = Unit_code;
                            data[2] = previousread;
                            data[3] = currentread;
                            data[4] = consumption;
                            data[5] = service_date;                            
                            data[6] = rate;
                            data[7] = service_cost;
                            data[8] = status;
							data[9] = servicetype; 
                            
        Config conf = com.threemsystems.rentmanager.Config.getInstance();
        String url = conf.getSERVERURL()+"save_new_unitservice.php";

//PutData putData = new PutData("http://3modernsystems.com/threepmobileserver/save_new_tenants.php", "POST", field, data);
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
					//if (flag) {
					Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
					Intent intent = new Intent(getApplicationContext(), DataEntry.class);
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
        Config conf = com.threemsystems.rentmanager.Config.getInstance();
        String url = conf.getSERVERURL()+"list_properties.php";
//String url = "http://3modernsystems.com/threepmobileserver/list_properties.php";
        getspinnerData(url,idNo);

    }

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
        property_spinner.setAdapter(new ArrayAdapter<spinnerItems>(AddUnitService.this, android.R.layout.simple_spinner_dropdown_item, spinnerlist));
    }
    private void getUnitsspinnerData(String url,String p_code){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            result = j.getJSONArray(TAG_jsonarray);
                            getSelectunitsResult(result);
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
                map.put("property_code",p_code);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getSelectunitsResult(JSONArray j){
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                // properties.add(json.getString(json.getString(TAG_propertyname));
                unitspinnerlist.add(new spinnerItems(json.getString("unit_code"), json.getString("unit_name")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // property_spinner.setAdapter(new ArrayAdapter<String>(AddUnit.this, android.R.layout.simple_spinner_dropdown_item, properties));
        propertyunit_spinner.setAdapter(new ArrayAdapter<spinnerItems>(AddUnitService.this, android.R.layout.simple_spinner_dropdown_item, unitspinnerlist));
    }
	//Get unit services data
	 private void getServicespinnerData(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            result = j.getJSONArray(TAG_jsonarray);
                            getServiceResult(result);
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
                map.put("property_code","pcode");
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void getServiceResult(JSONArray j){
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                // properties.add(json.getString(json.getString(TAG_propertyname));
                servicespinnerlist.add(new spinnerItems(json.getString("rental_service_code"), json.getString("rental_service_name")));
				
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // property_spinner.setAdapter(new ArrayAdapter<String>(AddUnit.this, android.R.layout.simple_spinner_dropdown_item, properties));
        servicetype_spinner.setAdapter(new ArrayAdapter<spinnerItems>(AddUnitService.this, android.R.layout.simple_spinner_dropdown_item, servicespinnerlist));
    }


}