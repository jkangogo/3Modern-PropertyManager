package com.kangogo.threepmanager.Holder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.os.Handler;
import android.widget.AdapterView.OnItemClickListener;
import com.kangogo.threepmanager.AddProperty;
import com.kangogo.threepmanager.R;
import java.util.ArrayList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.os.AsyncTask;
//import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity; 
import android.widget.FrameLayout; 
import android.widget.LinearLayout;	 
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Map;import java.util.HashMap;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import java.math.BigDecimal;
import android.content.SharedPreferences;
import java.math.BigDecimal;
import java.util.Date;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.RequestQueue ;
import android.widget.ProgressBar;
import android.os.Looper;



import android.util.Log;  
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class Property_old extends AppCompatActivity {
	GridView paymentgridView; 		
	public static final String TAG_ownerid = "onwerid";
    public static final String TAG_NAME = "name";
	public static final String TAG_DESC = "DESC";
	ProgressDialog mProgressBar ;
	TableLayout mTableLayout;
	SharedPreferences propertypref ;
	String OwnerTypeItem; String ActionItem;
	 private  JSONArray result;
	Button loaddata;
	ArrayList<allRentalData> rentalist = new ArrayList<>();
	ProgressBar progressBar;
	JSONArray propertylist = null;
    ArrayList<HashMap<String, String>> propertiesList = new ArrayList<HashMap<String, String>>();
	public static final String TAG_jsonarray = "result";
	private RecyclerView recyclerView;

	//private ArrayList<TagItem> tagItemArrayList = new ArrayList<>();
	//private TagAdapter adapter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);
        Spinner spnProperty = findViewById(R.id.spinnerProperty);
        Spinner spnAction = findViewById(R.id.spinneraction);
        Button addProperty = findViewById(R.id.btnAddProperty);
	//	loaddata=findViewById(R.id.btnloadProperty);

		RecyclerView recyclerView = findViewById(R.id.RclProperty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
		
		
		int numberOfColumns = 3;
  
    recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
    recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
   // adapter = new TagAdapter(this, tagItemArrayList);
    //recyclerView.setAdapter(adapter);

        addProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent intent= new Intent(getApplicationContext(), AddProperty.class);
               //startActivity(intent);
             //  finish();
	propertypref = getSharedPreferences("user_details", MODE_PRIVATE);				
	String idNo=propertypref.getString("idNo","MisingID");	        
 	com.kangogo.threepmanager.Config conf = com.kangogo.threepmanager.Config.getInstance();   
	String url = conf.getSERVERURL()+"list_properties.php";
	Toast.makeText(getApplicationContext(), url+"_"+idNo, Toast.LENGTH_LONG).show();
	Log.i("Property",url);
	//System.out.println(url+"_"+idNo);
      getpropertiesData(url,idNo);
            }
        });

        ArrayList<String> OwnerTypeList = new ArrayList<>();
		OwnerTypeList.add("Owner Type");
        OwnerTypeList.add("Cooperate");
        OwnerTypeList.add("Individual");
        OwnerTypeList.add("All");
		
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, OwnerTypeList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnProperty.setAdapter(arrayAdapter);
        spnProperty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 OwnerTypeItem = arrayAdapter.getItem(position).toString();
		//Toast.makeText(getApplicationContext(), OwnerTypeItem, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> ActioList = new ArrayList<>();
		ActioList.add("Select Action");
        ActioList.add("Print");
        ActioList.add("Export(Excel)");
        ActioList.add("Print Property");
        ArrayAdapter<String> actionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ActioList);
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAction.setAdapter(actionAdapter);
        spnAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 ActionItem = actionAdapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
	
	
		// mProgressBar = new ProgressDialog(this);
		// mTableLayout = (TableLayout) findViewById(R.id.propertytable);
		// mTableLayout.setStretchAllColumns(true);
		// startLoadData();
		/*loaddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {         

		if(!OwnerTypeItem.equals("")) {
                    //progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
							//call method to load data to table
							Toast.makeText(getApplicationContext(), "Clicked button", Toast.LENGTH_LONG).show();
             // getpropertiesData(String url,String owner_id);
				}
			});
			}
                else{
                    Toast.makeText(getApplicationContext(), "All Fields are Required", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
		
    }
	public void startLoadData() {
	        mProgressBar.setCancelable(false);
	        mProgressBar.setMessage("Fetching Invoices..");
	        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        mProgressBar.show();
	        new LoadDataTask().execute(0);
	    }
		
	  
	  class LoadDataTask extends AsyncTask<Integer, Integer, String> {
	        @Override
	        protected String doInBackground(Integer... params) { 
	            try {
	                Thread.sleep(2000);	
					} catch (InterruptedException e) {
	                e.printStackTrace();

	            }
	            return "Task Completed.";
	        }

	        @Override
	        protected void onPostExecute(String result) {
	           // mProgressBar.hide();
	           // loadData();
				}

	        @Override
	        protected void onPreExecute() {
	        }

	        @Override
	        protected void onProgressUpdate(Integer... values) {
 }
}
private void getpropertiesData(String url,String owner_id){	
    StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject j = null;
                    try {
                        j = new JSONObject(response);
                        result = j.getJSONArray(TAG_jsonarray);
                        getpropertiesResult(result);
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
            map.put("owner_id",owner_id);          
            return map;
        }
    };	
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
}
private void getpropertiesResult(JSONArray j){	
int ft=0;
 
    for(int i=0;i<j.length();i++){
        try {
			
            JSONObject json = j.getJSONObject(i);
			   			
			rentalist.add(new allRentalData(json.getString("owner_identifier"), json.getString("property_name"),json.getString("property_desc")));
			ft=ft+1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
 
  
	
   // recyclerView.setAdapter(new ArrayAdapter<allRentalData>(Property.this, android.R.layout.simple_list_item_1, rentalist));
Toast.makeText(getApplicationContext(), String.valueOf(rentalist), Toast.LENGTH_LONG).show(); 
	   
	  
	}  
	}