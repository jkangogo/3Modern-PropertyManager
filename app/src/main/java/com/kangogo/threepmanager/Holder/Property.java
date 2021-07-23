package com.kangogo.threepmanager.Holder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import java.util.Map;import java.util.HashMap;
import com.android.volley.RequestQueue ;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import com.kangogo.threepmanager.AddProperty;
import com.kangogo.threepmanager.PropertyAdapter;
import com.kangogo.threepmanager.R;
import android.content.SharedPreferences;
import java.util.List;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import org.json.JSONArray;
import java.net.URL;
import java.util.ArrayList;
import android.app.ListActivity;
import android.widget.TableLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.view.View;
import android.widget.LinearLayout;
import android.graphics.Color;

public class Property extends ListActivity {
	SharedPreferences propertypref ;
	//List<allRentalData> propertyList;
 ArrayList<HashMap<String, String>> propertyList = new ArrayList<HashMap<String, String>>();
	 RecyclerView.Adapter mAdapter;
	 RecyclerView recyclerView;
	  private  JSONArray result;
	  public static final String TAG_jsonarray = "result";
	  private final String TAG_PTYPE="property_desc";
	  private final String TAG_PNAME="property_name";
	  private final String TAG_OID="owner_identifier";
	  private final String ptype="Individual";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);
        Spinner spnProperty = findViewById(R.id.spinnerProperty);
        Spinner spnAction = findViewById(R.id.spinneraction);
        Button loadProperty = findViewById(R.id.btnloadProperties);

        loadProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    
	propertypref = getSharedPreferences("user_details", MODE_PRIVATE);				
	String idNo=propertypref.getString("idNo","MisingID");	        
 	com.kangogo.threepmanager.Config conf = com.kangogo.threepmanager.Config.getInstance();   
	String url = conf.getSERVERURL()+"select_properties.php";
	//Toast.makeText(getApplicationContext(), url+"_"+idNo, Toast.LENGTH_LONG).show();	 
  getpropertiesData(url,idNo,ptype);

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
        String OwnerTypeItem = arrayAdapter.getItem(position).toString();
	propertypref = getSharedPreferences("user_details", MODE_PRIVATE);				
	String idNo=propertypref.getString("idNo","MisingID");	          
		com.kangogo.threepmanager.Config conf = com.kangogo.threepmanager.Config.getInstance();   
		String url = conf.getSERVERURL()+"select_properties.php";
		//Toast.makeText(getApplicationContext(), url+"_"+idNo, Toast.LENGTH_LONG).show();
	 	getpropertiesData(url,idNo,OwnerTypeItem);
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
                String ActionItem = actionAdapter.getItem(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

       // getDataFromApi();
    }
	private void getpropertiesData(String url,String owner_id,String p_type){	
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
			map.put("p_type",p_type); 			
            return map;
        }
    };	
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    requestQueue.add(stringRequest);
}
private void getpropertiesResult(JSONArray j){	
int ft=0;
  propertyList.clear();
   // map.clear();
    for(int i=0;i<j.length();i++){			
        try {        	
		JSONObject json = j.getJSONObject(i);
		String count = String.valueOf(i+1);     
			String ownerid = json.getString("owner_identifier");                       
			String pname = json.getString("property_name");                       
			String ptype = json.getString("property_type");                     
            //  hashmap for single match
             HashMap<String, String> property_item = new HashMap<String, String>();
             // adding each child node to HashMap key => value                        
						property_item.put(TAG_OID, count);
						//property_item.put(TAG_OID, ownerid);
                        property_item.put(TAG_PNAME, pname);
                        property_item.put(TAG_PTYPE, ptype);
						propertyList.add(property_item);
					ft=ft+1;
						
        } catch (JSONException e) {
            e.printStackTrace();
        }
		
    }
	
	
	ListAdapter adapter = new SimpleAdapter(
                    Property.this, propertyList,
                    R.layout.list_properties, new String[] {
        TAG_OID, TAG_PNAME,TAG_PTYPE
    }, new int[] { R.id.countid,R.id.pName,R.id.pType}
    );
    setListAdapter(adapter);	
//Toast.makeText(getApplicationContext(), String.valueOf(ft), Toast.LENGTH_LONG).show(); 
	} 
	}
