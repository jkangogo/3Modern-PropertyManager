package com.threemodern.threepmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JounalEntry extends AppCompatActivity {
    private Spinner property, propertyUnit, Tenant, Reason_spn;
    private EditText amount, journaDate;
    private Button save, close, reset;

    DatePickerDialog picker;
    String propertycode,propertyname,unitcode,unitname,tenantid,tenantname, reason, statementid;
    ProgressBar progressBar;
    ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> reasonpinnerlist = new ArrayList<>();
    SharedPreferences propertypref ;
    private JSONArray result;
    public static final String TAG_jsonarray = "result";
    public static final String TAG_propertycode = "property_code";
    public static final String TAG_propertyname = "property_name";
    String Action_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jounal_entry);
        property = findViewById(R.id.spnProperty);
        propertyUnit = findViewById(R.id.spnPropertyUnit);
        Tenant = findViewById(R.id.spnTenant);
        Reason_spn = findViewById(R.id.spnReason);
        amount = findViewById(R.id.etAmount);
        journaDate = findViewById(R.id.dteJournalDate);
        save = findViewById(R.id.btnPaymentsubmit);
        close = findViewById(R.id.btnPaymentclose);
        reset = findViewById(R.id.btnPaymentreset);
        progressBar=findViewById(R.id.paymentaddprogress);

        journaDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(JounalEntry.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        journaDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        //payDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        Action_date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }
                }, year, month, day);
                picker.show();
            }
        });

        ArrayList<String> Reason = new ArrayList<>();
        Reason.add("Old Balance");
        Reason.add("Over Payment");
        Reason.add("Under Payment");
        Reason.add("Bounced Pay");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Reason);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Reason_spn.setAdapter(arrayAdapter);

        Reason_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String reason = arrayAdapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        propertypref = getSharedPreferences("user_details", MODE_PRIVATE);
        String idNo=propertypref.getString("idNo","MisingID");



        property.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                unitspinnerlist.clear();
                //notifyDataSetChanged();
                spinnerItems spinerproperty = (spinnerItems) parent.getSelectedItem();
                propertycode=spinerproperty.getId();
                propertyname=spinerproperty.getName();
                //call the method to populate unitcode spinner
                Config conf = com.threemodern.threepmanager.Config.getInstance();
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
        propertyUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                tenantpinnerlist.clear();
                //tenantpinnerlist.notifyDataSetChanged();
                spinnerItems spinerunitproperty = (spinnerItems) parent.getSelectedItem();
                unitcode=spinerunitproperty.getId();
                unitname=spinerunitproperty.getName();
                Config conf = com.threemodern.threepmanager.Config.getInstance();
                String url = conf.getSERVERURL()+"list_tenantidname.php";
                getspinnerData(url,unitcode,"unit_code","getSelectedTenantResult");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Tenant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount.setText("");
                journaDate.setText("");
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DataEntry.class);
                startActivity(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  Statement_id,Tenant_identifier, Tenant_name, Property_code, Unit_code, Payment_amount, Reason;
                //Payment_date = String.valueOf(payDate.getText());
                //Payment_date = payDate.getText();
                Statement_id = statementid;
                Tenant_identifier = tenantid;
                Tenant_name = tenantname;
                Property_code = propertycode;
                Unit_code = unitcode;
                Payment_amount = String.valueOf(amount.getText());
                Reason = String.valueOf(Reason_spn.getSelectedItem());
                String transaction_id="";
                String payment_id="";

                if(!Statement_id.equals("") && !Action_date.equals("") && !Tenant_identifier.equals("") && !Tenant_name.equals("") &&
                        !Property_code.equals("") && !Unit_code.equals("") && !Payment_amount.equals("") &&
                        !Reason.equals("") ) {

                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[9];
                            field[0] = "statement_id ";
                            field[1] = "action_date";
                            field[2] = "statement_identifier";
                            field[3] = "tenant_identifier";
                            field[4] = "description";
                            field[5] = "amount";
                            field[6] = "balance";
                            field[7] = "transaction_id";

                            //Creating array for data
                            String[] data = new String[9];
                            data[0] = Statement_id;
                            data[1] = Action_date;
                            data[2] = Tenant_identifier;
                            data[3] = Tenant_name;
                            data[4] = Property_code;
                            data[5] = Unit_code;
                            data[6] = Payment_amount;
                            data[7] = Reason;
                            data[8] = transaction_id;

                            Config conf = com.threemodern.threepmanager.Config.getInstance();
                            PutData putData = new PutData(conf.getSERVERURL()+"saveJournalEntry.php", "POST", field, data);

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
                                        Toast.makeText(getApplicationContext(), message+ " date= "+Action_date, Toast.LENGTH_LONG).show();
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
        Config conf = com.threemodern.threepmanager.Config.getInstance();
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
        property.setAdapter(new ArrayAdapter<spinnerItems>(JounalEntry.this, android.R.layout.simple_spinner_dropdown_item, propertyspinnerlist));
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
        propertyUnit.setAdapter(new ArrayAdapter<spinnerItems>(JounalEntry.this, android.R.layout.simple_spinner_dropdown_item, unitspinnerlist));
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
        Tenant.setAdapter(new ArrayAdapter<spinnerItems>(JounalEntry.this, android.R.layout.simple_spinner_dropdown_item, tenantpinnerlist));
    }
}