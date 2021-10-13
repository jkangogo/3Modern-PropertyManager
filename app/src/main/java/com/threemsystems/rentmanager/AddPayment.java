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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Calendar;
import android.content.SharedPreferences;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue ;
import java.util.Map;import java.util.HashMap;

public class AddPayment extends AppCompatActivity {
    DatePickerDialog picker;
    //Spinner Property,propertyUnit,Tenant,PaymentMode;
	private Spinner property_spinner,unit_spinner,tenant_spinner,PaymentMode;
	String propertycode,propertyname,unitcode,unitname,tenantid,tenantname;	
    EditText Amount,payDate,mpesacode,bankref;
    Button Save,Close,Reset;
    ProgressBar progressBar;
	ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();
	ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
	ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>();
	SharedPreferences propertypref ;
	private JSONArray result;
	public static final String TAG_jsonarray = "result";
	public static final String TAG_propertycode = "property_code";
	public static final String TAG_propertyname = "property_name";
String Payment_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment);
		
        property_spinner=findViewById(R.id.spnPaymentPropertyAdd);
        unit_spinner=findViewById(R.id.spnPaymentPropertyUnitAdd);
        tenant_spinner=findViewById(R.id.spnPaymentTenantUAdd);
        PaymentMode=findViewById(R.id.spnPaymentModeAdd);
        payDate=findViewById(R.id.dtePaymentDateAdd);
        Amount=findViewById(R.id.etPaymentAmountAdd);
		
		mpesacode=findViewById(R.id.etPaympesacodeAdd);
        bankref=findViewById(R.id.etPayBankrefAdd);
		
        Save=findViewById(R.id.btnPaymentsubmit);
        Close=findViewById(R.id.btnPaymentclose);
        Reset=findViewById(R.id.btnPaymentreset);
		progressBar=findViewById(R.id.paymentaddprogress);		

        payDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(AddPayment.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        payDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
						//payDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
						Payment_date=year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }
                }, year, month, day);
                picker.show();
            }
        });

        ArrayList<String> paymentMode = new ArrayList<>();
        paymentMode.add("M-Pesa");
        paymentMode.add("Cash");
        paymentMode.add("Bank Transfer");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paymentMode);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        PaymentMode.setAdapter(arrayAdapter);
        
		PaymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String payMode = arrayAdapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
		
		propertypref = getSharedPreferences("user_details", MODE_PRIVATE);				
		String idNo=propertypref.getString("idNo","MisingID");
		
		
		
		property_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		
		unitspinnerlist.clear();	
		//notifyDataSetChanged();
             spinnerItems spinerproperty = (spinnerItems) parent.getSelectedItem();
             propertycode=spinerproperty.getId();
			 propertyname=spinerproperty.getName();
			 //call the method to populate unitcode spinner
	Config conf = com.threemsystems.rentmanager.Config.getInstance();
	String url = conf.getSERVERURL()+"list_propertyunits.php"; 
	//String url = "http://3modernsystems.com/threepmobileserver/list_propertyunits.php";
	 //getUnitsspinnerData(url,propertycode,);
	 getspinnerData(url,propertycode,"property_code","getSelectunitsResult");
			// Toast.makeText(getApplicationContext(), "Property Code: "+spinerproperty.getId()+",  Property Name : "+spinerproperty.getName(), Toast.LENGTH_SHORT).show();    
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {    
        }
    });
	unit_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			
			 tenantpinnerlist.clear();
			 //tenantpinnerlist.notifyDataSetChanged();
             spinnerItems spinerunitproperty = (spinnerItems) parent.getSelectedItem();
             unitcode=spinerunitproperty.getId();
			 unitname=spinerunitproperty.getName();		
	 Config conf = com.threemsystems.rentmanager.Config.getInstance();
	 String url = conf.getSERVERURL()+"list_tenantidname.php"; 	
	 getspinnerData(url,unitcode,"unit_code","getSelectedTenantResult");			 
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {    
        }
    });
	tenant_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             spinnerItems spinertenant= (spinnerItems) parent.getSelectedItem();
             tenantid=spinertenant.getId();
			 tenantname=spinertenant.getName();			    
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {    
        }
    });

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payDate.setText("");
                Amount.setText("");
                mpesacode.setText("");
                bankref.setText("");
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
                String  Tenant_identifier, Tenant_name, Property_code, Unit_code, Payment_amount, Pay_mode;
				String mpesa_code,bank_ref;
                //Payment_date = String.valueOf(payDate.getText());
				//Payment_date = payDate.getText();
                Tenant_identifier = tenantid;
               Tenant_name = tenantname;
               Property_code = propertycode;
                Unit_code = unitcode;
                Payment_amount = String.valueOf(Amount.getText());
				mpesa_code= String.valueOf(mpesacode.getText());
				bank_ref= String.valueOf(bankref.getText());
                Pay_mode = String.valueOf(PaymentMode.getSelectedItem());
                String transaction_id="";
                String payment_id="";

		if(!Payment_date.equals("") && !Tenant_identifier.equals("") && !Tenant_name.equals("") && 
		!Property_code.equals("") && !Unit_code.equals("") && !Payment_amount.equals("") &&
		!Pay_mode.equals("") ) {

                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[11];
                            field[0] = "payment_date";
                            field[1] = "tenant_identifier";
                            field[2] = "tenant_name";
                            field[3] = "property_code";
                            field[4] = "unit_code";
                            field[5] = "payment_amount";
                            field[6] = "pay_mode";
                            field[7] = "transaction_id";
                            field[8] = "payment_id";
							field[9] = "mpesa_code";
                            field[10] = "bank_ref";

                            //Creating array for data
                            String[] data = new String[11];
                            data[0] = Payment_date;
                            data[1] = Tenant_identifier;
                            data[2] = Tenant_name;
                            data[3] = Property_code;
                            data[4] = Unit_code;
                            data[5] = Payment_amount;
                            data[6] = Pay_mode;
                            data[7] = transaction_id;
                            data[8] = payment_id;
							data[9] = mpesa_code;
                            data[10] = bank_ref;

Config conf = com.threemsystems.rentmanager.Config.getInstance();
PutData putData = new PutData(conf.getSERVERURL()+"save_tenantpayment.php", "POST", field, data);
     
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
					Toast.makeText(getApplicationContext(), message+ " date= "+Payment_date, Toast.LENGTH_LONG).show();
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
	idNo=propertypref.getString("idNo","MisingID");	        
	Config conf = com.threemsystems.rentmanager.Config.getInstance();
	String url = conf.getSERVERURL()+"list_properties.php";	
	getspinnerData(url,idNo,"owner_id","getPropertySpinnerResult");

    }
	private void getspinnerData(String url,String fieldcode,String fieldname,String functionname){	
    StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject j = null;
                    try {
                        j = new JSONObject(response);
                        result = j.getJSONArray(TAG_jsonarray);
						if(functionname.equals("getPropertySpinnerResult")){
                        getPropertySpinnerResult(result);
						}else if(functionname.equals("getSelectunitsResult")){
							 getSelectunitsResult(result);
						}
						else if(functionname.equals("getSelectedTenantResult")){
							 getSelectedTenantResult(result);
						}
						
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
            map.put(fieldname,fieldcode);          
            return map;
        }
    };	
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
}
private void getPropertySpinnerResult(JSONArray j){	
    for(int i=0;i<j.length();i++){
        try {
            JSONObject json = j.getJSONObject(i);
           // properties.add(json.getString(json.getString(TAG_propertyname));			
			propertyspinnerlist.add(new spinnerItems(json.getString(TAG_propertycode), json.getString(TAG_propertyname)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
   // property_spinner.setAdapter(new ArrayAdapter<String>(AddUnit.this, android.R.layout.simple_spinner_dropdown_item, properties));
	 property_spinner.setAdapter(new ArrayAdapter<spinnerItems>(AddPayment.this, android.R.layout.simple_spinner_dropdown_item, propertyspinnerlist));
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
	 unit_spinner.setAdapter(new ArrayAdapter<spinnerItems>(AddPayment.this, android.R.layout.simple_spinner_dropdown_item, unitspinnerlist));
}
private void getSelectedTenantResult(JSONArray j){	
    for(int i=0;i<j.length();i++){
        try {
            JSONObject json = j.getJSONObject(i);
           // properties.add(json.getString(json.getString(TAG_propertyname));			
		tenantpinnerlist.add(new spinnerItems(json.getString("tenant_identifier"), json.getString("tenant_name")));
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
   // property_spinner.setAdapter(new ArrayAdapter<String>(AddUnit.this, android.R.layout.simple_spinner_dropdown_item, properties));
	 tenant_spinner.setAdapter(new ArrayAdapter<spinnerItems>(AddPayment.this, android.R.layout.simple_spinner_dropdown_item, tenantpinnerlist));
}
}