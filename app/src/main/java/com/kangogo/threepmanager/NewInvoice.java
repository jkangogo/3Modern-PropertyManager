package com.kangogo.threepmanager;

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

public class NewInvoice extends AppCompatActivity {
    private Spinner property, propertyUnit, Tenant, invoiceType;
    private EditText unitRent, electBill, serviceFee, invoiceMonth;
    private Button save, close, reset;
    DatePickerDialog picker;

    String propertycode,propertyname,unitcode,unitname,tenantid,tenantname;
    ProgressBar progressBar;
    ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>();
    SharedPreferences propertypref ;
    private JSONArray result;
    public static final String TAG_jsonarray = "result";
    public static final String TAG_propertycode = "property_code";
    public static final String TAG_propertyname = "property_name";
    String Invoice_month;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_invoice);
        property = findViewById(R.id.spnProperty);
        propertyUnit = findViewById(R.id.spnPropertyUnit);
        Tenant = findViewById(R.id.spnTenant);
        invoiceType = findViewById(R.id.spnInvoiceType);
        unitRent = findViewById(R.id.etUnitRent);
        electBill = findViewById(R.id.etElectBill);
        serviceFee = findViewById(R.id.etServiceFee);
        invoiceMonth = findViewById(R.id.dteInvoicemonth);
        save = findViewById(R.id.btnPaymentsubmit);
        close = findViewById(R.id.btnPaymentclose);
        reset = findViewById(R.id.btnPaymentreset);

        progressBar=findViewById(R.id.paymentaddprogress);

        invoiceMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(NewInvoice.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        invoiceMonth.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        //payDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        Invoice_month=year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }
                }, year, month, day);
                picker.show();
            }
        });

        ArrayList<String> InvoiceType = new ArrayList<>();
        InvoiceType.add("Temporary");
        InvoiceType.add("Permanent");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, InvoiceType);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        invoiceType.setAdapter(arrayAdapter);

        invoiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String payMode = arrayAdapter.getItem(position).toString();
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
                Config conf = com.kangogo.threepmanager.Config.getInstance();
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
                Config conf = com.kangogo.threepmanager.Config.getInstance();
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
                unitRent.setText("");
                electBill.setText("");
                serviceFee.setText("");
                invoiceMonth.setText("");
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
                String  Tenant_identifier, Tenant_name, Property_code, Unit_code, Rent_payable, Electric_bill, Service_fee, Invoice_type;
                //Payment_date = String.valueOf(payDate.getText());
                //Payment_date = payDate.getText();
                Tenant_identifier = tenantid;
                Tenant_name = tenantname;
                Property_code = propertycode;
                Unit_code = unitcode;
                Service_fee = String.valueOf(serviceFee.getText());
                Rent_payable = String.valueOf(unitRent.getText());
                Electric_bill = String.valueOf(electBill.getText());
                Invoice_type = String.valueOf(invoiceType.getSelectedItem());
                String Invoice_id="";
                String Water_bill="";

                if(!Invoice_month.equals("") && !Tenant_identifier.equals("") && !Tenant_name.equals("") &&
                        !Property_code.equals("") && !Unit_code.equals("") && !Service_fee.equals("") && !Rent_payable.equals("") && !Water_bill.equals("") && !Electric_bill.equals("") &&
                        !Invoice_type.equals("") ) {

                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[9];
                            field[0] = "tenant_identifier";
                            field[1] = "property_code";
                            field[2] = "unit_code";
                            field[3] = "rent_payable";
                            field[4] = "invoice_month";
                            field[5] = "waterbill";
                            field[6] = "electricbill";
                            field[7] = "servicefee";
                            field[8] = "invoice_id";

                            //Creating array for data
                            String[] data = new String[9];
                            data[0] = Tenant_identifier;
                            data[1] = Property_code;
                            data[2] = Unit_code;
                            data[3] = Rent_payable;
                            data[4] = Invoice_month;
                            data[5] = Water_bill;
                            data[6] = Electric_bill;
                            data[7] = Service_fee ;
                            data[8] = Invoice_id ;

                            Config conf = com.kangogo.threepmanager.Config.getInstance();
                            PutData putData = new PutData(conf.getSERVERURL()+"createtenant_invoice.php", "POST", field, data);

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
                                        Toast.makeText(getApplicationContext(), message+ " date= "+Invoice_month, Toast.LENGTH_LONG).show();
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
        Config conf = com.kangogo.threepmanager.Config.getInstance();
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
        property.setAdapter(new ArrayAdapter<spinnerItems>(NewInvoice.this, android.R.layout.simple_spinner_dropdown_item, propertyspinnerlist));
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
        propertyUnit.setAdapter(new ArrayAdapter<spinnerItems>(NewInvoice.this, android.R.layout.simple_spinner_dropdown_item, unitspinnerlist));
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
        Tenant.setAdapter(new ArrayAdapter<spinnerItems>(NewInvoice.this, android.R.layout.simple_spinner_dropdown_item, tenantpinnerlist));
    }
}