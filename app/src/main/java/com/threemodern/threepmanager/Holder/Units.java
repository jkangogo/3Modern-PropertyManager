package com.threemodern.threepmanager.Holder;

import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.AuthFailureError;
import android.app.ListActivity;

import com.android.volley.RequestQueue ;
import com.threemodern.threepmanager.Config;
import com.threemodern.threepmanager.spinnerItems;
import com.threemodern.threepmanager.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Map;import java.util.HashMap;
import android.content.SharedPreferences;
import java.util.ArrayList;

import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class Units extends ListActivity {
    ArrayList<String> properties;
    JSONArray result;
	SharedPreferences propertypref ;
	private  String	TAG_COUNT = "ucount";
	private  String	TAG_UNAME = "u_name";
	private  String	TAG_RENT = "u_rent";
	private  String	TAG_STATUS="u_status";
	String pcode, unitcode ;
    Spinner spnProperty, spnStatus, spnUnit;
	ArrayList<com.threemodern.threepmanager.spinnerItems> spinnerlist = new ArrayList<>();
	ArrayList<HashMap<String, String>> unitList = new ArrayList<HashMap<String, String>>();
	ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_units);

        EditText UnitName = findViewById(R.id.ettunitname);
        spnProperty =(Spinner) findViewById(R.id.spnp);
        spnStatus =(Spinner) findViewById(R.id.spnstatus);
        spnUnit =(Spinner) findViewById(R.id.spnunit);
		
		spnProperty.setFocusableInTouchMode(true);
		spnUnit.setFocusableInTouchMode(true);
		
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);

        ArrayList<String> StatusList = new ArrayList<>();
        StatusList.add("Occupied");
        StatusList.add("Vacant");
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StatusList);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(statusAdapter);
        
		spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
       public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        
		String StatusItem = statusAdapter.getItem(position).toString();
		//spinnerItems spinerproperty = (spinnerItems) parent.getSelectedItem();
        // pcode=spinerproperty.getId();      
		com.threemodern.threepmanager.Config conf = com.threemodern.threepmanager.Config.getInstance();
		String url = conf.getSERVERURL()+"select_propertyunits.php";		
	 	getUnitsListData(url,pcode,StatusItem,"allunits");
		//Toast.makeText(getApplicationContext(), pcode, Toast.LENGTH_LONG).show(); 
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       // ArrayList<String> UnitList = new ArrayList<>();
       //UnitList.add("Select unit");
      // ArrayAdapter<String> unitListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, UnitList);
        //unitListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spnUnit.setAdapter(unitListAdapter);
       //When the units spinner is clicked
	   spnUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              com.threemodern.threepmanager.spinnerItems spinerpropertyunits = ( com.threemodern.threepmanager.spinnerItems) parent.getSelectedItem();
			   
		 //String UnitItem =spinerpropertyunits.getName();
		 unitcode =spinerpropertyunits.getId();
		// String UnitItem =parent.getSelectedItem();
		//spinnerItems spinerproperty = (spinnerItems) parent.getSelectedItem();
        // String pcode=spinerproperty.getId();      
		com.threemodern.threepmanager.Config conf = com.threemodern.threepmanager.Config.getInstance();
		String url = conf.getSERVERURL()+"select_propertyunits.php";		
	 	getUnitsListData(url,pcode,"allstatus",unitcode);
		
		//Toast.makeText(getApplicationContext(), UnitItem, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
		
		
        //getData();
		spnProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       
	   com.threemodern.threepmanager.spinnerItems spinerproperty = ( com.threemodern.threepmanager.spinnerItems) parent.getSelectedItem();
         pcode=spinerproperty.getId();          
		com.threemodern.threepmanager.Config conf = com.threemodern.threepmanager.Config.getInstance();
		String url = conf.getSERVERURL()+"select_propertyunits.php";
		String url2 = conf.getSERVERURL()+"list_propertyunits.php";
	//	unitspinnerlist.clear();		
		//				
	 	getUnitsListData(url,pcode,"allstatus","allunits");	
	   // getUnitsspinnerData(url2,pcode);
		//get the units associated with the property
		
	//	Toast.makeText(getApplicationContext(), pcode, Toast.LENGTH_LONG).show(); 
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {    
        }
    });
	
	//spnProperty.setOnTouchListener(new View.OnTouchListener(){
   // @Override
   // public boolean onTouch(View v, MotionEvent event) {
     //   Toast.makeText ( Units.this, "test", Toast.LENGTH_SHORT ).show();        
	
		// return false;
   // }
	

//});
spnUnit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
         @Override
         public void onFocusChange(View v, boolean hasFocus) {			 
		com.threemodern.threepmanager.Config conf = com.threemodern.threepmanager.Config.getInstance();
		String url = conf.getSERVERURL()+"select_propertyunits.php";
		String url2 = conf.getSERVERURL()+"list_propertyunits.php";
		
		unitspinnerlist.clear();
         if (!hasFocus) {		
	// 	getUnitsListData(url,pcode,"allstatus","allunits");	//list the selected unit	
//	getUnitsListData(url,pcode,"allstatus",unitcode);
                     } else {
		//Toast.makeText (Units.this, "Got focust", Toast.LENGTH_LONG ).show();        
		getUnitsspinnerData(url2,pcode);	//populate the units spinner from the property code	
		
            }
         }
      });
 spnProperty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
         @Override
         public void onFocusChange(View v, boolean hasFocus) {
			 com.threemodern.threepmanager.Config conf = com.threemodern.threepmanager.Config.getInstance();
		String url = conf.getSERVERURL()+"select_propertyunits.php";
		String url2 = conf.getSERVERURL()+"list_propertyunits.php";
		unitspinnerlist.clear();
            if (!hasFocus) {		
		//getUnitsspinnerData(url2,pcode);	//populate the units spinner from the property code	
	 //	getUnitsListData(url,pcode,"allstatus","allunits");	//list the selected unt
       // Toast.makeText(Units.this, "focus loosed", Toast.LENGTH_LONG).show();
            } else {
		//com.threemodern.threepmanager.spinnerItems spinerproperty = ( com.threemodern.threepmanager.spinnerItems) parent.getSelectedItem();
        // pcode=spinerproperty.getId();  
		getUnitsspinnerData(url2,pcode);		
		
              // Toast.makeText(Units.this, "focused", Toast.LENGTH_LONG).show();
            }
         }
      });

	
	propertypref = getSharedPreferences("user_details", MODE_PRIVATE);				
	String idNo=propertypref.getString("idNo","MisingID");	         
	Config conf = com.threemodern.threepmanager.Config.getInstance();
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
			spinnerlist.add(new  com.threemodern.threepmanager.spinnerItems(json.getString("property_code"), json.getString("property_name")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
   // property_spinner.setAdapter(new ArrayAdapter<String>(AddUnit.this, android.R.layout.simple_spinner_dropdown_item, properties));
	 spnProperty.setAdapter(new ArrayAdapter< spinnerItems>(Units.this, android.R.layout.simple_spinner_dropdown_item, spinnerlist));
	 
}
//This to display the units based on the property code and staus
private void getUnitsListData(String url,String p_code,String u_status,String u_code){	
    StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject j = null;
                    try {
                        j = new JSONObject(response);
                        result = j.getJSONArray("result");
                        getUnitsResult(result);
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
            map.put("property_code",p_code);  
			map.put("u_status",u_status); 
			map.put("u_code",u_code); 				
            return map;
        }
    };	
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
}
private void getUnitsResult(JSONArray j){	
int ft=0; Float totalamount=0.0f;
  unitList.clear();
   // map.clear();
    for(int i=0;i<j.length();i++){
			
        try {        	
		JSONObject json = j.getJSONObject(i);
			String count = String.valueOf(i+1);     
			String unitname = json.getString("unit_name");                       
			String ustatus = json.getString("status");                       
			String urent = json.getString("unit_rent_amount");
			
			if(unitname.length()>10){
				unitname= unitname.substring(0, 10);
			 }
			 
			 Float amounti=Float.parseFloat(urent);
	totalamount=totalamount + amounti;
	//DecimalFormat df = new DecimalFormat("0.00");
	DecimalFormat df = new DecimalFormat("#,###.00");
	df.setMaximumFractionDigits(2);
	urent = df.format(amounti);	
            //  hashmap for single match
             HashMap<String, String> unit_item = new HashMap<String, String>();
             // adding each child node to HashMap key => value                        
						unit_item.put(TAG_COUNT, count);						
                        unit_item.put(TAG_UNAME, unitname);
                        unit_item.put(TAG_RENT, urent);
						unit_item.put(TAG_STATUS, ustatus);
						unitList.add(unit_item);
					ft=ft+1;
						
        } catch (JSONException e) {
            e.printStackTrace();
        }
		
    }
	
	
	ListAdapter adapter = new SimpleAdapter(
                    Units.this, unitList,
                    R.layout.list_units, new String[] {
        TAG_COUNT, TAG_UNAME,TAG_STATUS,TAG_RENT
    }, new int[] { R.id.countunitid,R.id.uName,R.id.uStatus,R.id.uRent}
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
	 spnUnit.setAdapter(new ArrayAdapter<spinnerItems>(Units.this, android.R.layout.simple_spinner_dropdown_item, unitspinnerlist));
}
}
