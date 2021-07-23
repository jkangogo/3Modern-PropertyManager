package com.kangogo.threepmanager.Holder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Map;import java.util.HashMap;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.AuthFailureError;
import android.app.ListActivity;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.RequestQueue ;
import com.kangogo.threepmanager.spinnerItems;
import com.kangogo.threepmanager.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.kangogo.threepmanager.AddTenant;
import com.kangogo.threepmanager.R;
import java.util.ArrayList;
import java.util.Calendar;
import android.content.SharedPreferences;

public class Tenancy extends ListActivity {
    DatePickerDialog picker, picker2;
	Spinner spnProperty, spnUnit,spnTenant,spnAction;
ArrayList<com.kangogo.threepmanager.spinnerItems> spinnerlist = new ArrayList<>();
ArrayList<com.kangogo.threepmanager.spinnerItems> unitspinnerlist = new ArrayList<>();
ArrayList<com.kangogo.threepmanager.spinnerItems> tenantspinnerlist = new ArrayList<>();
ArrayList<HashMap<String, String>> tenantList = new ArrayList<HashMap<String, String>>();
	String TAG_COUNT="tcount";
	String TAG_TENANTNAME="t_name";
    String TAG_UNAME="u_name";
    String TAG_TEL="t_tel";
	String pcode, unitcode,unitname,idNo;			 
	JSONArray result; SharedPreferences propertypref ;
	String startdate,enddate,dateflag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenancy);

        EditText dateStart = findViewById(R.id.datestart);
        EditText dateEnd = findViewById(R.id.dateend);
         spnProperty = findViewById(R.id.spnpr);
         spnUnit = findViewById(R.id.spnunitt);
         spnTenant = findViewById(R.id.spntenant);
         spnAction = findViewById(R.id.spnaction);

        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(Tenancy.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateStart.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
						startdate=year  + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }
                }, year, month, day);
                picker.show();

            }
        });

       dateEnd.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final Calendar cldr = Calendar.getInstance();
               int day = cldr.get(Calendar.DAY_OF_MONTH);
               int month = cldr.get(Calendar.MONTH);
               int year = cldr.get(Calendar.YEAR);
	   com.kangogo.threepmanager.Config conf = com.kangogo.threepmanager.Config.getInstance();   
		String url = conf.getSERVERURL()+"select_tenants.php";
               // date picker dialog
               picker2 = new DatePickerDialog(Tenancy.this, new DatePickerDialog.OnDateSetListener() {
                   @Override
                   public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                       dateEnd.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
					   enddate=year  + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
					 if(startdate!="" && enddate!=""){
						 tenantList.clear();
		//url, t_id, t_name, o_id,p_code, u_code, s_date, e_date,  d_flag		
		getTenantsListData(url,"","",idNo,pcode,"",startdate,enddate,"YES");
		Toast.makeText(getApplicationContext(), "dates:="+startdate+" / "+enddate, Toast.LENGTH_LONG).show();		
					 }else{
						 //dates are not well formated
	Toast.makeText(getApplicationContext(), "Ensure that the dates are well formated", Toast.LENGTH_LONG).show();	
					 }					 
                   }
               }, year, month, day);
               picker2.show();

           }
       });

        //ArrayList<String> PropertyList = new ArrayList<>();
        //PropertyList.add("");
        //ArrayAdapter<String> propertyListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, PropertyList);
        //propertyListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spnProperty.setAdapter(propertyListAdapter);
        
		spnProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         //       String PropertyItem = propertyListAdapter.getItem(position).toString();
		
		 com.kangogo.threepmanager.spinnerItems spinerproperty = ( com.kangogo.threepmanager.spinnerItems) parent.getSelectedItem();
         pcode=spinerproperty.getId();
            
		com.kangogo.threepmanager.Config conf = com.kangogo.threepmanager.Config.getInstance();   
		String url = conf.getSERVERURL()+"select_tenants.php";
		String url2 = conf.getSERVERURL()+"list_propertyunits.php";//get the units associated with
		String url3 = conf.getSERVERURL()+"list_tenantidname.php";
		unitspinnerlist.clear();tenantspinnerlist.clear();  tenantList.clear();
	 	//getTenantsListData(url,pcode,"alltenants","allunits");
//url, t_id, t_name, o_id,p_code, u_code, s_date, e_date,  d_flag		
		getTenantsListData(url,"","",idNo,pcode,"","","","");		
		//get the units associated with the property
		getUnitsspinnerData(url2,pcode);	
		
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
			
        });
		

       //ArrayList<String> UnitList = new ArrayList<>();
        //UnitList.add("");
        //ArrayAdapter<String> UnitListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, UnitList);
      //  UnitListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      // spnUnit.setAdapter(UnitListAdapter);
		
        spnUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             //   String ucode = UnitListAdapter.getItem(position).toString();
				tenantspinnerlist.clear();
			 tenantList.clear();
             spinnerItems spinerunitproperty = (spinnerItems) parent.getSelectedItem();
             unitcode=spinerunitproperty.getId();
			 unitname=spinerunitproperty.getName();	
           //  Toast.makeText(parent.getContext(), "Selected: " + unitcode, Toast.LENGTH_LONG).show();				
				Config conf = com.kangogo.threepmanager.Config.getInstance();  
				String url = conf.getSERVERURL()+"list_tenantidname.php";
				String url2 = conf.getSERVERURL()+"select_tenants.php";
				getTenantsspinnerData(url,unitcode)	;
//url, t_id, t_name, o_id,p_code, u_code, s_date, e_date,  d_flag				
				getTenantsListData(url2,"","",idNo,"",unitcode,"","","");	
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       // ArrayList<String> TenantList = new ArrayList<>();
       // TenantList.add("");
       // ArrayAdapter<String> tenantListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TenantList);
        //tenantListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // spnTenant.setAdapter(tenantListAdapter);
        
		spnTenant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // String TenantItem = tenantListAdapter.getItem(position).toString();
				
		spinnerItems spinerunitproperty = (spinnerItems) parent.getSelectedItem();
           String  tenantid=spinerunitproperty.getId();
			
           // Toast.makeText(parent.getContext(), "Selected: " + tenantid, Toast.LENGTH_LONG).show();				
				Config conf = com.kangogo.threepmanager.Config.getInstance();  
				String url = conf.getSERVERURL()+"list_tenantidname.php";
				String url2 = conf.getSERVERURL()+"select_tenants.php";
				//getTenantsspinnerData(url,unitcode)	;	
				// tenantList.clear();
		//url, t_id, t_name, o_id,p_code, u_code, s_date, e_date,  d_flag
				getTenantsListData(url2,tenantid,"",idNo,pcode,unitcode,"","","");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> ActionList = new ArrayList<>();
        ActionList.add("");
        ArrayAdapter<String> actionListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ActionList);
        actionListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAction.setAdapter(actionListAdapter);
        spnAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ActionItem = actionListAdapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
	propertypref = getSharedPreferences("user_details", MODE_PRIVATE);				
	idNo=propertypref.getString("idNo","MisingID");	         
	Config conf = com.kangogo.threepmanager.Config.getInstance();  
	String url = conf.getSERVERURL()+"list_properties.php";	
	getPropertyspinnerData(url,idNo);
		
    }
	//This to display the property on the spinner based on the owner id
	private void getPropertyspinnerData(String url,String owner_id){	
    StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject j = null;
                    try {
                        j = new JSONObject(response);
                        result = j.getJSONArray("result");
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
		spinnerlist.add(new  com.kangogo.threepmanager.spinnerItems(json.getString("property_code"), json.getString("property_name")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
   // property_spinner.setAdapter(new ArrayAdapter<String>(AddUnit.this, android.R.layout.simple_spinner_dropdown_item, properties));
	 spnProperty.setAdapter(new ArrayAdapter< spinnerItems>(Tenancy.this, android.R.layout.simple_spinner_dropdown_item, spinnerlist));
	 
}
//get unitsspinner data


//This to display the tenancy based on the property code,units or tenant
//$id,$name,$ownerid,$property,$unit,$sdate,$edate,$dateflag
private void getTenantsListData(String url,String t_id,String t_name,String o_id,
String p_code,String u_code,String s_date,String e_date, String d_flag){	
    StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject j = null;
                    try {
                        j = new JSONObject(response);
                        result = j.getJSONArray("result");
                        getTenancyResult(result);
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
			map.put("owner_id",o_id); 
			map.put("property",p_code); 
			map.put("unit",u_code); 
			map.put("startdate",s_date);
			map.put("enddate",e_date); 
			map.put("dateflag",d_flag); 		
			
            return map;
        }
    };	
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
}
private void getTenancyResult(JSONArray j){	
int ft=0;
  tenantList.clear();
   // map.clear();
    for(int i=0;i<j.length();i++){
			
        try {        	
		JSONObject json = j.getJSONObject(i);
			String count = String.valueOf(i+1);     
			String tenantname = json.getString("tenant_name");                       
			String tenanttel = json.getString("tenant_tel");                       
			String unitname = json.getString("unit_name");
			
	if(unitname.length()>10){
				unitname= unitname.substring(0, 10);
			 }
			 
	String[] arr_name = tenantname.split(" ");
			 		if(arr_name.length>1){
						tenantname=arr_name[0] + " "+ arr_name[1];
					}	
            //  hashmap for single match
	if(tenantname==null || tenantname=="null") tenantname=" ";
            //  hashmap for single match
             HashMap<String, String> tenant_item = new HashMap<String, String>();
             // adding each child node to HashMap key => value                        
						tenant_item.put(TAG_COUNT, count);
                        tenant_item.put(TAG_UNAME, unitname);
                        tenant_item.put(TAG_TENANTNAME, tenantname);
						tenant_item.put(TAG_TEL, tenanttel);
						tenantList.add(tenant_item);
					ft=ft+1;
						
        } catch (JSONException e) {
            e.printStackTrace();
        }
		
    }	
	ListAdapter adapter = new SimpleAdapter(
                    Tenancy.this, tenantList,
                    R.layout.list_tenants, new String[] {
        TAG_COUNT, TAG_TENANTNAME,TAG_UNAME,TAG_TEL
    }, new int[] { R.id.counttenantid,R.id.tName,R.id.tUnit,R.id.tTelephone}
    );
    setListAdapter(adapter);
	
//Toast.makeText(getApplicationContext(), String.valueOf(ft), Toast.LENGTH_LONG).show();    
	  
	} 
	//Get spinner data for the units
	private void getUnitsspinnerData(String url,String p_code){	
    StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject j = null;
                    try {
                        j = new JSONObject(response);
                        result = j.getJSONArray("result");
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
	 spnUnit.setAdapter(new ArrayAdapter<spinnerItems>(Tenancy.this, android.R.layout.simple_spinner_dropdown_item, unitspinnerlist));
}
	
	//Get spinner data for the tenants
	private void getTenantsspinnerData(String url,String u_code){	
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
             map.put("unit_code",u_code); 	
					 
            return map;
        }
    };	
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
}
private void getTenantsResult(JSONArray j){	
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
	 spnTenant.setAdapter(new ArrayAdapter<spinnerItems>(Tenancy.this, android.R.layout.simple_spinner_dropdown_item, tenantspinnerlist));
}
}