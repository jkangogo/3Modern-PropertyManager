package com.threemsystems.rentmanager.Holder;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Map;import java.util.HashMap;
import android.content.SharedPreferences;
import java.util.ArrayList;

import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.AuthFailureError;
import android.app.ListActivity;

import com.android.volley.RequestQueue ;
import com.threemsystems.rentmanager.spinnerItems;
import com.threemsystems.rentmanager.Config;
import com.threemsystems.rentmanager.R;
import java.util.Calendar;
import java.util.Date;


public class Payments extends ListActivity {
    DatePickerDialog picker, picker2;
	Spinner spnProperty,spnUnit,spnTenant,spnAction;
	
    ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();
	ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
	ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>(); 
ArrayList<HashMap<String, String>> paymentList = new ArrayList<HashMap<String, String>>();	
	
	private JSONArray result;
	public static final String TAG_jsonarray = "result";
	public static final String TAG_propertycode = "property_code";
	public static final String TAG_propertyname = "property_name";
	String TAG_COUNT="count"; String TAG_PAYDATE="payment_date";
	String TAG_TENANTNAME="tenant_name";String TAG_AMOUNT="payment_amount";
	
	String startdate,enddate;
	//spinnerItems spinerproperty;
	String propertycode, propertyname,unitcode,unitname;
	Config conf = com.threemsystems.rentmanager.Config.getInstance();
	SharedPreferences propertypref ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        EditText dateFrom = findViewById(R.id.datefrm);
        EditText dateTo = findViewById(R.id.datetu);
         spnProperty = findViewById(R.id.spnprty);
         spnUnit = findViewById(R.id.spnunt);
         spnTenant = findViewById(R.id.spntnt);
         spnAction = findViewById(R.id.spnact);
		propertypref = getSharedPreferences("user_details", MODE_PRIVATE);
		String idNo=propertypref.getString("idNo","MisingID");
       // spnUnit.setFocusableInTouchMode(true);
		
		Date currentDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.DATE, -1);
		dateTo.setText(cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE));
		
		Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
		c.add(Calendar.MONTH, -1);
		//Date sdate = c.getTime();		
		dateFrom.setText(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE));

        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(Payments.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateFrom.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
				startdate=year  + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }
                }, year, month, day);
                picker.show();
            }
        });
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
		picker2 = new DatePickerDialog(Payments.this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			dateTo.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
		enddate=year  + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
		if(startdate!="" && enddate!=""){
		String url = conf.getSERVERURL()+"select_tenantpayments.php";	
			//paymentList.clear();			
	getPaymentsListData(url,idNo,startdate,enddate, "", "","");
		//Toast.makeText(getApplicationContext(), "dates:="+startdate+" / "+enddate, Toast.LENGTH_LONG).show();		
					 }else{
						 //dates are not well formated
	Toast.makeText(getApplicationContext(), "Ensure that the dates are well formated", Toast.LENGTH_LONG).show();	
					 }
                    }
                }, year, month, day);
                picker2.show();
            }
        });

       // ArrayList<String> propertyList = new ArrayList<>();
       // propertyList.add("");
        //ArrayAdapter<String> propertyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, propertyList);
        //propertyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // spnProperty.setAdapter(propertyAdapter);
        
		spnProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String PropertyItem = propertyAdapter.getItem(position).toString();
			//spnUnit.setFocusableInTouchMode(true);
            //spnUnit.setFocusable(true);			
			spinnerItems spinerproperty = (spinnerItems) parent.getSelectedItem();
            propertycode=spinerproperty.getId();
			propertyname=spinerproperty.getName();		
			
			String url2 = conf.getSERVERURL()+"select_tenantpayments.php";	
			//paymentList.clear();			
			getPaymentsListData(url2,idNo,startdate,enddate, propertycode, "","");
			unitspinnerlist.clear(); 
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
		

       // ArrayList<String> unitList = new ArrayList<>();
        //unitList.add("");
       // ArrayAdapter<String> unitAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, unitList );
        //unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spnUnit.setAdapter(unitAdapter);
        
		spnUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             //Config conf = com.threemodern.threepmanager.Config.getInstance();
			 spinnerItems spinerunitproperty = (spinnerItems) parent.getSelectedItem();
             unitcode=spinerunitproperty.getId();
			 unitname=spinerunitproperty.getName();				
		String url2 = conf.getSERVERURL()+"select_tenantpayments.php";			
		// paymentList.clear();			
		getPaymentsListData(url2,idNo,startdate,enddate, propertycode, unitcode,"");
		//unitspinnerlist.clear();
		tenantpinnerlist.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
		spnUnit.setOnTouchListener(new View.OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			//Toast.makeText ( Payments.this, "Touched", Toast.LENGTH_SHORT ).show();
			String url = conf.getSERVERURL()+"list_propertyunits.php";	
			//unitspinnerlist.clear();				    
			getspinnerData(url,propertycode,"property_code","getSelectunitsResult");
			
			return false;
		}

		});
		
	
       // ArrayList<String> tenantList = new ArrayList<>();
       // tenantList.add("");
       // ArrayAdapter<String> tenantAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tenantList );
        //tenantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // spnTenant.setAdapter(tenantAdapter);
        spnTenant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              //  String TenantItem = tenantAdapter.getItem(position).toString();
			spinnerItems spinerTenants = (spinnerItems) parent.getSelectedItem();
            String tenantid=spinerTenants.getId();
			String url2 = conf.getSERVERURL()+"select_tenantpayments.php";	
			//paymentList.clear();			
			getPaymentsListData(url2,idNo,startdate,enddate, propertycode, unitcode,tenantid);
			//tenantpinnerlist.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
		spnTenant.setOnTouchListener(new View.OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			//Toast.makeText ( Payments.this, "tenant Touched", Toast.LENGTH_SHORT ).show();
		String url = conf.getSERVERURL()+"list_tenantidname.php";
		//tenantpinnerlist.clear();		            
		getspinnerData(url,unitcode,"unit_code","getSelectedTenantResult");
			
			return false;
		}

		});		

        ArrayList<String> ActionList = new ArrayList<>();
        ActionList.add("Print Receipt");
        ActionList.add("Print Payments");
        ActionList.add("Export(excel)");
        ActionList.add("Email receipt");
        ArrayAdapter<String> actionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ActionList);
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAction.setAdapter(actionAdapter);
        spnAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String actionItem = actionAdapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
	        
	 
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
	 spnProperty.setAdapter(new ArrayAdapter<spinnerItems>(Payments.this, android.R.layout.simple_spinner_dropdown_item, propertyspinnerlist));
}

private void getSelectunitsResult(JSONArray j){
	unitspinnerlist.clear();
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
	 spnUnit.setAdapter(new ArrayAdapter<spinnerItems>(Payments.this, android.R.layout.simple_spinner_dropdown_item, unitspinnerlist));
}
private void getSelectedTenantResult(JSONArray j){
tenantpinnerlist.clear();	
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
	 spnTenant.setAdapter(new ArrayAdapter<spinnerItems>(Payments.this, android.R.layout.simple_spinner_dropdown_item, tenantpinnerlist));
}
//This to display the tenancy based on the property code,units or tenant
//$id,$name,$ownerid,$property,$unit,$sdate,$edate,$dateflag
private void getPaymentsListData(String url,String o_id,String s_date,String e_date,
String p_code,String u_code,String t_id){	
    StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject j = null;
                    try {
                        j = new JSONObject(response);
                        result = j.getJSONArray("result");
                        getPaymentsResult(result);
						//loadData(result);
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
			map.put("ownerid",o_id); 
			map.put("startdate",s_date);
			map.put("enddate",e_date);              		
			map.put("pcode",p_code); 
			map.put("ucode",u_code); 
			map.put("id",t_id);
            return map;
        }
    };	
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
}
private void getPaymentsResult(JSONArray j){	
int ft=0;Float totalamount=0.0f;
String totalamountstring="";
  paymentList.clear();
   // map.clear();
    for(int i=0;i<j.length();i++){
			
        try {        	
		JSONObject json = j.getJSONObject(i);
			String count = String.valueOf(i+1);   
			String pdate = json.getString("payment_date");   			
			String tenantname = json.getString("tenant_name");
			String pamount = json.getString("payment_amount");
			 
String[] arr_name = tenantname.split(" ");
			 		if(arr_name.length>1){
						tenantname=arr_name[0] + " "+ arr_name[1];
					}	
            //  hashmap for single match
	if(tenantname==null || tenantname=="null")tenantname=" ";
	///
	
	
	Float amounti=Float.parseFloat(pamount);
	totalamount=totalamount + amounti;
	//DecimalFormat df = new DecimalFormat("0.00");
	DecimalFormat df = new DecimalFormat("#,###.00");
	
	df.setMaximumFractionDigits(2);
	pamount = df.format(amounti);	
	
	totalamountstring = df.format(totalamount);		
	
            //  hashmap for single match
             HashMap<String, String> payment_item = new HashMap<String, String>();
             // adding each child node to HashMap key => value                        
						payment_item.put(TAG_COUNT, count);						
                        payment_item.put(TAG_PAYDATE, pdate);
                        payment_item.put(TAG_TENANTNAME, tenantname);
						payment_item.put(TAG_AMOUNT, pamount);
						
						
						paymentList.add(payment_item);
					ft=ft+1;
						
        } catch (JSONException e) {
            e.printStackTrace();
        }
		
    }

//Put in the last line of total
HashMap<String, String> payment_item2 = new HashMap<String, String>();
HashMap<String, String> payment_item3 = new HashMap<String, String>();
						payment_item2.put(TAG_COUNT, "");	
						payment_item2.put(TAG_PAYDATE, "");	
                        payment_item2.put(TAG_TENANTNAME, "");
						payment_item2.put(TAG_AMOUNT, "");
						
						//===========================
						payment_item3.put(TAG_COUNT, "");	
						payment_item3.put(TAG_PAYDATE, "");
                        payment_item3.put(TAG_TENANTNAME, "TOTAL:");
						payment_item3.put(TAG_AMOUNT, totalamountstring);
						paymentList.add(payment_item3);
						paymentList.add(payment_item2);
							
	ListAdapter adapter = new SimpleAdapter(
                    Payments.this, paymentList,
                    R.layout.list_payments, new String[] {
        TAG_COUNT, TAG_PAYDATE,TAG_TENANTNAME,TAG_AMOUNT
    }, new int[] { R.id.countpaymentid,R.id.pDate,R.id.tName,R.id.pAmount}
    );
    setListAdapter(adapter);
	
//Toast.makeText(getApplicationContext(), String.valueOf(ft), Toast.LENGTH_LONG).show();    
	  
	}
}