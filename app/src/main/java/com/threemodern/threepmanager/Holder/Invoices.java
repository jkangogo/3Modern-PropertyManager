package com.threemodern.threepmanager.Holder;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
import com.threemodern.threepmanager.spinnerItems;

import com.threemodern.threepmanager.R;

import java.util.Calendar;

public class Invoices extends ListActivity {
    DatePickerDialog picker, picker2;
	JSONArray result;
  Spinner spnTenant,spnAction; 
  String TAG_COUNT="icount";
  String TAG_MONTH="imonth";
  String TAG_TENANTNAME="iname";
  String TAG_UNAME="uname";
  String TAG_AMOUNT="iamount";
  ArrayList<com.threemodern.threepmanager.spinnerItems> tenantspinnerlist = new ArrayList<>();
  ArrayList<HashMap<String, String>> tenantInvoiceList = new ArrayList<HashMap<String, String>>();
  String startdate,enddate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);
		SharedPreferences propertypref ;
        //EditText tenantId =  findViewById(R.id.ettID);
        //EditText tenantName = findViewById(R.id.ettname);
        EditText dateFrom = findViewById(R.id.datefrom);
        EditText dateTo = findViewById(R.id.dateto);
        spnTenant = findViewById(R.id.spntenant);
        Spinner spnAction = findViewById(R.id.spnactionn);

	//spnTenant.setFocusableInTouchMode(true);
	
		propertypref = getSharedPreferences("user_details", MODE_PRIVATE);				
	    String idNo=propertypref.getString("idNo","MisingID");
		
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(Invoices.this, new DatePickerDialog.OnDateSetListener() {
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
				com.threemodern.threepmanager.Config conf = com.threemodern.threepmanager.Config.getInstance();
				String url = conf.getSERVERURL()+"select_tenantinvoices.php";	
                picker2 = new DatePickerDialog(Invoices.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateTo.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
					enddate=year  + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
					 if(startdate!="" && enddate!=""){
						// tenantInvoiceList.clear();
		//url, t_id, t_name, o_id,p_code, u_code, s_date, e_date,  d_flag		
		//getTenantsListData(url,"","",idNo,pcode,"",startdate,enddate,"YES");
		getTenantInvoiceListData(url,"","",idNo,startdate,enddate);
		//Toast.makeText(getApplicationContext(), "OWNER="+ idNo  +"dates:="+startdate+" / "+enddate, Toast.LENGTH_LONG).show();		
					 }else{
						 //dates are not well formated
	Toast.makeText(getApplicationContext(), "Ensure that the dates are well formated", Toast.LENGTH_LONG).show();	
					 }
                    }
                }, year, month, day);
                picker2.show();
            }
        });

        //ArrayList<String> tenantString = new ArrayList<>();
      //  tenantString.add("");
    //    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tenantString);
  //      arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spnTenant.setAdapter(arrayAdapter);
       
	   spnTenant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String tenantList = arrayAdapter.getItem(position).toString();
				com.threemodern.threepmanager.Config conf = com.threemodern.threepmanager.Config.getInstance();
				String url = conf.getSERVERURL()+"select_tenantinvoices.php";
		com.threemodern.threepmanager.spinnerItems spinerinvoices = ( com.threemodern.threepmanager.spinnerItems) parent.getSelectedItem();
         String tid=spinerinvoices.getId();
	
				// tenantInvoiceList.clear();
		//url, t_id, t_name, o_id,p_code, u_code, s_date, e_date,  d_flag		
		 getTenantInvoiceListData(url,tid,"",idNo,startdate,enddate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
		spnTenant.setOnFocusChangeListener(new View.OnFocusChangeListener() {
         @Override
         public void onFocusChange(View v, boolean hasFocus) {			 
		
         if (!hasFocus) {		

          } else {
			  
		//Toast.makeText (Invoices.this, "Got focust", Toast.LENGTH_LONG ).show();        
		//getTenantsspinnerData(url2,idNo);	//populate the units spinner from the property code	
		
            }
         }
      });
        ArrayList<String> actionString = new ArrayList<>();
        actionString.add("Print Invoice(s)");
        actionString.add("Email Invoice");
        actionString.add("Export Invoices");
        ArrayAdapter<String> actionAdapter = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item, actionString);
       actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAction.setAdapter(actionAdapter);
      
	  spnAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String action = actionAdapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
		com.threemodern.threepmanager.Config conf = com.threemodern.threepmanager.Config.getInstance();
		String url2 = conf.getSERVERURL()+"list_tenantidnamebyowner.php";
		String url = conf.getSERVERURL()+"select_tenantinvoices.php";		
		//tenantspinnerlist.clear();
		//Toast.makeText (Invoices.this, "Got focust="+idNo, Toast.LENGTH_LONG ).show();        
		getTenantsspinnerData(url2,idNo);	//populate the units spinner from the property code	
		//$id,$name,$oid,$startdate,$enddate
		getTenantInvoiceListData(url,"","",idNo,"","");
    }
		//Get spinner data for the units
	private void getTenantsspinnerData(String url,String ownerid){	
		StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject j = null;
                    try {
                        j = new JSONObject(response);
                        result = j.getJSONArray("result");
                        getTenantsResult(result);
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
              map.put("owner_id",ownerid); 	
					 
            return map;
        }
    };	
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
	}
private void getTenantsResult(JSONArray j){	
//Toast.makeText (Invoices.this, "J length="+j.length(), Toast.LENGTH_LONG ).show();
    for(int i=0;i<j.length();i++){
        try {
            JSONObject json = j.getJSONObject(i);
           // properties.add(json.getString(json.getString(TAG_propertyname));			
			tenantspinnerlist.add(new spinnerItems(json.getString("tenant_identifier"), json.getString("tenant_name")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
   // property_spinner.setAdapter(new ArrayAdapter<String>(AddUnit.this, android.R.layout.simple_spinner_dropdown_item, properties));
	 spnTenant.setAdapter(new ArrayAdapter<spinnerItems>(Invoices.this, android.R.layout.simple_spinner_dropdown_item, tenantspinnerlist));
}
//$id,$name,$oid,$startdate,$enddate
private void getTenantInvoiceListData(String url,String t_id,String t_name,String o_id,
String s_date,String e_date){	
    StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject j = null;
                    try {
                        j = new JSONObject(response);
                        result = j.getJSONArray("result");
                        getTenantInvoiceResult(result);
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
            map.put("tenant_identifier",t_id);  
			map.put("tenant_name",t_name);  
			map.put("ownerid",o_id); 			
			map.put("startdate",s_date);
			map.put("enddate",e_date); 		
			
            return map;
        }
    };	
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
}
	private void getTenantInvoiceResult(JSONArray j){	
	int ft=0;
	String totalamountstring="";
  tenantInvoiceList.clear();
  Float totalamount=0.0f;
   // map.clear();
    for(int i=0;i<j.length();i++){				
        try {        	
			
			JSONObject json = j.getJSONObject(i);
			String count = String.valueOf(i+1); 
			String invoicemonth = json.getString("invoice_month"); 			
			String tenantname = json.getString("tenant_name");                       
			String invoiceamount = json.getString("rent_payable");                       
			//String unitname = json.getString("unit_name");	
			String[] arr_name = tenantname.split(" ");
			 		if(arr_name.length>1){
						tenantname=arr_name[0] + " "+ arr_name[1];
					}	
            //  hashmap for single match
	if(tenantname==null || tenantname=="null")tenantname=" ";
	///
	
	Float amounti=Float.parseFloat(invoiceamount);
	totalamount=totalamount + amounti;
	//DecimalFormat df = new DecimalFormat("0.00");
	DecimalFormat df = new DecimalFormat("#,###.00");
	df.setMaximumFractionDigits(2);
	invoiceamount = df.format(amounti);	
	
	totalamountstring = df.format(totalamount);	
	
             HashMap<String, String> tenantinvoice_item = new HashMap<String, String>();
             // adding each child node to HashMap key => value                        
						tenantinvoice_item.put(TAG_COUNT, count);	
						tenantinvoice_item.put(TAG_MONTH, invoicemonth);						
                        //tenantinvoice_item.put(TAG_UNAME, unitname.substring(0, 10));
                        tenantinvoice_item.put(TAG_TENANTNAME, tenantname);
						tenantinvoice_item.put(TAG_AMOUNT, invoiceamount);				
					
						tenantInvoiceList.add(tenantinvoice_item);
					ft=ft+1;
						
        } catch (JSONException e) {
            e.printStackTrace();
        }
		
    }	
	
	/// put the last line of summation
	 HashMap<String, String> tenantinvoice_item2 = new HashMap<String, String>();
	  HashMap<String, String> tenantinvoice_item3 = new HashMap<String, String>();
						
						//===========================
						tenantinvoice_item2.put(TAG_COUNT, "");	
						tenantinvoice_item2.put(TAG_MONTH, "");
                        tenantinvoice_item2.put(TAG_TENANTNAME, "TOTAL:");
						tenantinvoice_item2.put(TAG_AMOUNT, totalamountstring);
						tenantInvoiceList.add(tenantinvoice_item2);
						
						tenantinvoice_item3.put(TAG_COUNT, "");	
						tenantinvoice_item3.put(TAG_MONTH, "");	
                        tenantinvoice_item3.put(TAG_TENANTNAME, "");
						tenantinvoice_item3.put(TAG_AMOUNT, "");
						tenantInvoiceList.add(tenantinvoice_item3);
	
	ListAdapter adapter = new SimpleAdapter(
                    Invoices.this, tenantInvoiceList,
                    R.layout.list_invoices, new String[] {
        TAG_COUNT,TAG_MONTH, TAG_TENANTNAME,TAG_AMOUNT
    }, new int[] { R.id.countinvoiceid,R.id.iMonth,R.id.iName,R.id.iamount}
    );
    setListAdapter(adapter);
	
//Toast.makeText(getApplicationContext(), String.valueOf(ft), Toast.LENGTH_LONG).show();    
	  
	}
}