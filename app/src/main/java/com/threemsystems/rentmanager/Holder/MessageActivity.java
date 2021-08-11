package com.threemsystems.rentmanager.Holder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.threemsystems.rentmanager.Config;
import com.threemsystems.rentmanager.R;
import com.threemsystems.rentmanager.spinnerItems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    Spinner recipient;
    EditText contact,message;
    Button send;

    ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>();
    private JSONArray result;
    public static final String TAG_jsonarray = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        recipient = findViewById(R.id.spnRecipient);
        message = findViewById(R.id.etMessage);
        contact = findViewById(R.id.etRecipient);
        send = findViewById(R.id.btnSend);


        recipient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerItems spinertenants = (spinnerItems) parent.getSelectedItem();
                String  tenantid=spinertenants.getId();

                Config conf = com.threemsystems.rentmanager.Config.getInstance();
                String url = conf.getSERVERURL()+"list_tenantidname.php";
                String url2 = conf.getSERVERURL()+"select_tenants.php";
                getspinnerData(url,tenantid,"","");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void getspinnerData(String url,String t_id,String t_name,String t_tel){
            StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject j = null;
                            try {
                                j = new JSONObject(response);
                                result = j.getJSONArray("result");
                                getTenantSpinnerResult(result);
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
                    map.put("tenant_tel",t_tel);

                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
    }
    private void getTenantSpinnerResult(JSONArray j){
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                tenantpinnerlist.add(new spinnerItems(json.getString("tenant_identifier"), json.getString("tenant_name")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // property_spinner.setAdapter(new ArrayAdapter<String>(AddUnit.this, android.R.layout.simple_spinner_dropdown_item, properties));
        recipient.setAdapter(new ArrayAdapter<spinnerItems>(MessageActivity.this, android.R.layout.simple_spinner_dropdown_item, tenantpinnerlist));
    }

}