package com.threemodern.threepmanager;

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
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue ;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import android.content.SharedPreferences;
import java.util.Map;import java.util.HashMap;

public class AddTenant extends AppCompatActivity {
    DatePickerDialog picker, picker1;
    Spinner tenantType, Property,propertyunit_spinner,Status;
    EditText unitRent,electriBill,serviceFee,ID,Name,Pin,Tell,Address,
            dateFrom,dateTo,WaterBill;

    Button Save,Close,Reset;
    ProgressBar progressBar;
    SharedPreferences propertypref ;
    private Spinner property_spinner;
    private ArrayList<String> properties;
    ArrayList<spinnerItems> spinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    private JSONArray result;
    public static final String TAG_propertycode = "property_code";
    public static final String TAG_propertyname = "property_name";
    public static final String TAG_jsonarray = "result";
    String propertycode,propertyname,unitcode,unitname,Date_from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tenant);
        tenantType=findViewById(R.id.spnTenantTypeAdd);
        property_spinner=findViewById(R.id.spnTenantPAdd);
        propertyunit_spinner=findViewById(R.id.spnTenantPUAdd);
        Status=findViewById(R.id.spnTenantStatusAdd);
        unitRent=findViewById(R.id.etTenantUNAdd);
        electriBill=findViewById(R.id.ettenantElecAdd);
        WaterBill=findViewById(R.id.ettenantWaterAdd);
        serviceFee=findViewById(R.id.etTenantSAdd);
        ID=findViewById(R.id.etTenantIDAdd);
        Name=findViewById(R.id.etTenantNameAdd);
        Pin=findViewById(R.id.etTenantpinAdd);
        Tell=findViewById(R.id.ettenantTelAdd);
        Address=findViewById(R.id.ettenantAddAdd);
        dateFrom=findViewById(R.id.dteTenantFrmAdd);
        // dateTo=findViewById(R.id.dteTenantToAdd);
        Save=findViewById(R.id.btTenantsubmit);
        Close=findViewById(R.id.btnTenantclose);
        Reset=findViewById(R.id.btnTenantreset);
        progressBar = findViewById(R.id.tenantaddprogress);

        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(AddTenant.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateFrom.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        Date_from=year  + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }
                }, year, month, day);
                picker.show();
            }
        });
/*
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker1 = new DatePickerDialog(AddTenant.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateTo.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
                picker1.show();
            }
        });*/

        ArrayList<String> TenantTypeList = new ArrayList<>();
        TenantTypeList.add("Individual");
        TenantTypeList.add("Cooperate");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TenantTypeList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tenantType.setAdapter(arrayAdapter);
        tenantType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String TenantTypeItem = arrayAdapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> StatusList = new ArrayList<>();
        StatusList.add("Active");
        StatusList.add("Inactive");
        ArrayAdapter<String> statusListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StatusList);
        statusListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Status.setAdapter(statusListAdapter);
        Status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String StatusItem = statusListAdapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        property_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerItems spinerproperty = (spinnerItems) parent.getSelectedItem();
                propertycode=spinerproperty.getId();
                propertyname=spinerproperty.getName();
                //call the method to populate unitcode spinner
                Config conf = com.threemodern.threepmanager.Config.getInstance();
                String url2 = conf.getSERVERURL()+"list_propertyunits.php";
                //String url = "http://3modernsystems.com/threepmobileserver/list_propertyunits.php";
                getUnitsspinnerData(url2,propertycode);
                // Toast.makeText(getApplicationContext(), "Property Code: "+spinerproperty.getId()+",  Property Name : "+spinerproperty.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        propertyunit_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerItems spinerunitproperty = (spinnerItems) parent.getSelectedItem();
                unitcode=spinerunitproperty.getId();
                unitname=spinerunitproperty.getName();
                //call the method to populate unitcode spinner
                //String url = "http://3modernsystems.com/threepmobileserver/list_propertyunits.php";
                //	 getUnitsspinnerData(url,propertycode);
                // Toast.makeText(getApplicationContext(), "Property Code: "+spinerproperty.getId()+",  Property Name : "+spinerproperty.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unitRent.setText("");
                electriBill.setText("");
                WaterBill.setText("");
                serviceFee.setText("");
                ID.setText("");
                Name.setText("");
                Pin.setText("");
                Tell.setText("");
                Address.setText("");
                dateFrom.setText("");
            }
        });

        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DataEntry.class);
                startActivity(intent);
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Tenant_identifier, Tenant_name, Tenant_pin, Tenant_address,
                        Tenant_type, Tenant_tel, Property_code, Unit_code, Rent_payable,
                        Waterbill, Electricbill, Servicefee, Date_to, Active;
                Tenant_identifier = String.valueOf(ID.getText());
                Tenant_name = String.valueOf(Name.getText());
                Tenant_pin = String.valueOf(Pin.getText());
                Tenant_address = String.valueOf(Address.getText());
                Tenant_type = String.valueOf(tenantType.getSelectedItem());
                Tenant_tel = String.valueOf(Tell.getText());
                Property_code =propertycode;
                Unit_code = unitcode;
                Rent_payable = String.valueOf(unitRent.getText());
                Waterbill = String.valueOf(WaterBill.getText());
                Electricbill = String.valueOf(electriBill.getText());
                Servicefee = String.valueOf(serviceFee.getText());
                // Date_from = String.valueOf(dateFrom.getText());
                //  Date_to = String.valueOf(dateTo.getText());
                Active = String.valueOf(Status.getSelectedItem());
                // String tenant_id="";

                if(!Tenant_identifier.equals("") && !Tenant_name.equals("") && !Tenant_pin.equals("") &&
                        !Tenant_address.equals("") && !Tenant_type.equals("")  && !Tenant_tel.equals("") &&
                        !Property_code.equals("") && !Unit_code.equals("") && !Rent_payable.equals("") &&
                        !Waterbill.equals("") && !Electricbill.equals("") && !Date_from.equals("") &&
                        !Active.equals("") ) {

                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[14];
                            field[0] = "tenant_identifier";
                            field[1] = "tenant_name";
                            field[2] = "tenant_pin";
                            field[3] = "tenant_address";
                            field[4] = "tenant_type";
                            field[5] = "tenant_tel";
                            field[6] = "property_code";
                            field[7] = "unit_code";
                            field[8] = "rent_payable";
                            field[9] = "waterbill";
                            field[10] = "electricbill";
                            field[11] = "servicefee";
                            field[12] = "date_from";
                            //field[13] = "date_to";
                            field[13] = "active";
                            //field[14] = "tenant_id";

                            //Creating array for data
                            String[] data = new String[14];
                            data[0] = Tenant_identifier;
                            data[1] = Tenant_name;
                            data[2] = Tenant_pin;
                            data[3] = Tenant_address;
                            data[4] = Tenant_type;
                            data[5] = Tenant_tel;
                            data[6] = Property_code;
                            data[7] = Unit_code;
                            data[8] = Rent_payable;
                            data[9] = Waterbill;
                            data[10] = Electricbill;
                            data[11] = Servicefee;
                            data[12] = Date_from;
                            //data[13] = Date_to;
                            data[13]= Active;
                            //data[14] = tenant_id;

                            Config conf = com.threemodern.threepmanager.Config.getInstance();
                            String url = conf.getSERVERURL()+"save_new_tenants.php";

//PutData putData = new PutData("http://3modernsystems.com/threepmobileserver/save_new_tenants.php", "POST", field, data);
                            PutData putData = new PutData(url, "POST", field, data);
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
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
        String idNo=propertypref.getString("idNo","MisingID");
        Config conf = com.threemodern.threepmanager.Config.getInstance();
        String url = conf.getSERVERURL()+"list_properties.php";
//String url = "http://3modernsystems.com/threepmobileserver/list_properties.php";
        getspinnerData(url,idNo);

    }

    private void getspinnerData(String url,String owner_id){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            result = j.getJSONArray(TAG_jsonarray);
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
                spinnerlist.add(new spinnerItems(json.getString(TAG_propertycode), json.getString(TAG_propertyname)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // property_spinner.setAdapter(new ArrayAdapter<String>(AddUnit.this, android.R.layout.simple_spinner_dropdown_item, properties));
        property_spinner.setAdapter(new ArrayAdapter<spinnerItems>(AddTenant.this, android.R.layout.simple_spinner_dropdown_item, spinnerlist));
    }
    private void getUnitsspinnerData(String url,String p_code){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            result = j.getJSONArray(TAG_jsonarray);
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
        propertyunit_spinner.setAdapter(new ArrayAdapter<spinnerItems>(AddTenant.this, android.R.layout.simple_spinner_dropdown_item, unitspinnerlist));
    }


}