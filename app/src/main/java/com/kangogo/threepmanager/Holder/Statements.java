
package com.kangogo.threepmanager.Holder;
import android.app.DatePickerDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.SharedPreferences;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.kangogo.threepmanager.AddPayment;
import com.kangogo.threepmanager.R;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;

import com.kangogo.threepmanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class Statements extends ListActivity {
    DatePickerDialog picker, picker2;
	Spinner spnProperty,spnUnit,spnTenant,spnAction,spnType;
	EditText dateStart ,dateEnd;
	
	private JSONArray result;
	public static final String TAG_jsonarray = "result";
	public static final String TAG_propertycode = "property_code";
	public static final String TAG_propertyname = "property_name";
	String propertycode, propertyname,unitcode,unitname;
	Config conf = com.kangogo.threepmanager.Config.getInstance(); 
	
	SharedPreferences propertypref ;
	String TAG_DATE="statement_date"; 
	String TAG_TENANTNAME="tenant_name";
	String TAG_DESC="statement_desc";	
	String TAG_AMOUNT="statement_amount";
	String TAG_BALANCE="statement_balance";
	String startdate="";String enddate="";String tenantid="";

	ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();
	ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
	ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>(); 
	ArrayList<HashMap<String, String>> statementList = new ArrayList<HashMap<String, String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statements);

         dateStart = findViewById(R.id.datestt);
         dateEnd = findViewById(R.id.datend);
		 spnProperty = findViewById(R.id.spnprty);
		  spnUnit = findViewById(R.id.spnunt); 
         spnTenant = findViewById(R.id.spntt);
         spnType = findViewById(R.id.spntype);
         spnAction = findViewById(R.id.spnacton);
		 
		 propertypref = getSharedPreferences("user_details", MODE_PRIVATE);
		 String idNo=propertypref.getString("idNo","MisingID");

        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(Statements.this, new DatePickerDialog.OnDateSetListener() {
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
                // date picker dialog
                picker2 = new DatePickerDialog(Statements.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateEnd.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
						enddate=year  + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }
                }, year, month, day);
                picker2.show();
            }
        });
	
	spnProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          	
			spinnerItems spinerproperty = (spinnerItems) parent.getSelectedItem();
            propertycode=spinerproperty.getId();
			propertyname=spinerproperty.getName();	
		/*			
			String url2 = conf.getSERVERURL()+"select_tenantpayments.php";	
			paymentList.clear();			
			getPaymentsListData(url2,idNo,startdate,enddate, propertycode, "","");
			unitspinnerlist.clear(); */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
	spnProperty.setOnTouchListener(new View.OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) {			
		String url = conf.getSERVERURL()+"list_properties.php";	
		getspinnerData(url,idNo,"owner_id","getPropertySpinnerResult");	
			return false;
		}
		});		
	spnUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             //Config conf = com.kangogo.threepmanager.Config.getInstance();		 	
			 spinnerItems spinerunitproperty = (spinnerItems) parent.getSelectedItem();
             unitcode=spinerunitproperty.getId();
			 unitname=spinerunitproperty.getName();			
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
		//	unitspinnerlist.clear();				    
			getspinnerData(url,propertycode,"property_code","getSelectunitsResult");
			
			return false;
		}

		});
    spnTenant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         spinnerItems spinertenants = (spinnerItems) parent.getSelectedItem();
         
				tenantid=spinertenants.getId();		
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

        ArrayList<String> TypeList = new ArrayList<>();
		TypeList .add("Select type");
		TypeList .add("All");
        TypeList .add("Payments");
        TypeList .add("Unpaid");
        TypeList .add("Invoices");
        TypeList .add("Credit Note");
        TypeList .add("Bounced");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TypeList );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(typeAdapter);
		
        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       String stdate=dateStart.getText().toString() ; String edate= dateEnd.getText().toString();
	  
	  if(stdate.equals("")  || edate.equals("")){
		   Toast.makeText(getApplicationContext(), "Make sure your dates are set", Toast.LENGTH_LONG).show();		
		}	else{
		String url = conf.getSERVERURL()+"select_tenantstatement.php";	
		String selecttype = typeAdapter.getItem(position).toString();
		//statementList.clear();			
		getStatementsListData(url,idNo,startdate,enddate,propertycode,unitcode,tenantid,selecttype);
		
		}	
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> ActionList = new ArrayList<>();
        ActionList.add("Print Statement");
        ActionList.add("Print Debts");
        ActionList.add("Journal Entry");
        ActionList.add("Email");
        ActionList.add("Excel");
        ArrayAdapter<String> actionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ActionList);
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAction.setAdapter(actionAdapter);
        spnAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ActionItem = actionAdapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
		
		//175599	mtrh 771
		RequestQueue requestQueue = Volley.newRequestQueue(this);
		requestQueue.add(stringRequest);
	}
	private void getPropertySpinnerResult(JSONArray j){
		propertyspinnerlist.clear();
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
		 spnProperty.setAdapter(new ArrayAdapter<spinnerItems>(Statements.this, android.R.layout.simple_spinner_dropdown_item, propertyspinnerlist));
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
		 spnUnit.setAdapter(new ArrayAdapter<spinnerItems>(Statements.this, android.R.layout.simple_spinner_dropdown_item, unitspinnerlist));
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
		 spnTenant.setAdapter(new ArrayAdapter<spinnerItems>(Statements.this, android.R.layout.simple_spinner_dropdown_item, tenantpinnerlist));
	}
	private void getStatementsListData(String url,String o_id,String s_date,String e_date,
	String p_code,String u_code,String t_id, String selecttype){	
		StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						JSONObject j = null;
						try {
							j = new JSONObject(response);
							result = j.getJSONArray("result");
							getStatementsResult(result);
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
				map.put("owner_id",o_id); 
				map.put("start_date",s_date);
				map.put("end_date",e_date);              		
				map.put("pcode",p_code); 
				map.put("ucode",u_code); 
				map.put("id",t_id);
				map.put("stype",selecttype);
				
				return map;
			}
		};	
		RequestQueue requestQueue = Volley.newRequestQueue(this);
		requestQueue.add(stringRequest);
	}
	private void getStatementsResult(JSONArray j){	
	int ft=0;Float totalamount=0.0f;
	String sbalance="";
	  statementList.clear();
	   // map.clear();
		for(int i=0;i<j.length();i++){
				
			try {        	
			JSONObject json = j.getJSONObject(i);
				String count = String.valueOf(i+1);   
				String sdate = json.getString("action_date");   			
			//	String tenantname = json.getString("tenant_name");
				String tenantname = "";
				String sdesc = json.getString("description");
				String samount = json.getString("amount");
				 sbalance = json.getString("balance");
				 
	String[] arr_name = tenantname.split(" ");
						if(arr_name.length>0){
							tenantname=arr_name[0];
						}
						
	String[] arr_desc = sdesc.split(" ");
						if(arr_name.length>0){
							sdesc=arr_desc[0];
						}					
				//  hashmap for single match
		if(tenantname==null || tenantname=="null")tenantname=" ";
		///
				
		Float amounts=Float.parseFloat(samount);
		Float amountbalance=Float.parseFloat(sbalance);	
		DecimalFormat df = new DecimalFormat("#,###.00");
		df.setMaximumFractionDigits(2);
		samount = df.format(amounts);	
		sbalance = df.format(amountbalance);
		
				//  hashmap for single match
				 HashMap<String, String> statement_item = new HashMap<String, String>();
				 // adding each child node to HashMap key => value                        
							statement_item.put(TAG_DATE, sdate);						
							statement_item.put(TAG_DESC, sdesc);
						   // statement_item.put(TAG_TENANTNAME, tenantname);
							statement_item.put(TAG_AMOUNT, samount);
							statement_item.put(TAG_BALANCE, sbalance);
							/*
							if(i==(j.length()-1)){								
							statement_item.put(TAG_DATE, "");						
							statement_item.put(TAG_DESC, "");
						  //  statement_item.put(TAG_TENANTNAME, "");
							statement_item.put(TAG_AMOUNT, "BALANCE");						
							statement_item.put(TAG_BALANCE, sbalance);
							}*/
							statementList.add(statement_item);
						ft=ft+1;
							
			} catch (JSONException e) {
				e.printStackTrace();
			}		
		}	
		HashMap<String, String> statement_item2 = new HashMap<String, String>();
		HashMap<String, String> statement_item3 = new HashMap<String, String>();
				statement_item2.put(TAG_DATE, "");						
				statement_item2.put(TAG_DESC, "");
			  //  statement_item.put(TAG_TENANTNAME, "");
				statement_item2.put(TAG_AMOUNT, "BALANCE");						
				statement_item2.put(TAG_BALANCE, sbalance);
				statementList.add(statement_item2);
				
				statement_item3.put(TAG_DATE, "");						
				statement_item3.put(TAG_DESC, "");			 
				statement_item3.put(TAG_AMOUNT, "");						
				statement_item3.put(TAG_BALANCE, "");
				statementList.add(statement_item3);
							
		ListAdapter adapter = new SimpleAdapter(
						Statements.this, statementList,
						R.layout.list_statements, new String[] {
			TAG_DATE, TAG_DESC,TAG_AMOUNT,TAG_BALANCE
		}, new int[] { R.id.stDate,R.id.stDesc,R.id.stAmount,R.id.stBalance}
		);
		setListAdapter(adapter);
		}

}