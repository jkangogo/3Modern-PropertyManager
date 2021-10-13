package com.threemsystems.rentmanager;

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

public class NewExpenditure extends AppCompatActivity {
    private Spinner property, paymentMode;
    private EditText payee, description, payDate, amount, payRef, mobilecode;
    private Button save, close, reset;
    DatePickerDialog picker;

    String propertycode,propertyname;
    ProgressBar progressBar;
    ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>();
    SharedPreferences propertypref ;
    private JSONArray result;
    public static final String TAG_jsonarray = "result";
    public static final String TAG_propertycode = "property_code";
    public static final String TAG_propertyname = "property_name";
    String Pay_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expenditure);
        property = findViewById(R.id.spnProperty);
        paymentMode = findViewById(R.id.spnPaymode);
        payee = findViewById(R.id.etpayee);
        description = findViewById(R.id.etdesc);
        payDate = findViewById(R.id.dtePay);
        amount = findViewById(R.id.etPayAmount);
        payRef = findViewById(R.id.etPayRefNo);
        mobilecode = findViewById(R.id.etmobileCode);
        save = findViewById(R.id.btnPaymentsubmit);
        close = findViewById(R.id.btnPaymentclose);
        reset = findViewById(R.id.btnPaymentreset);
        progressBar=findViewById(R.id.paymentaddprogress);

        payDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(NewExpenditure.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        payDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        //payDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        Pay_date=year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    }
                }, year, month, day);
                picker.show();
            }
        });

        ArrayList<String> PaymentMode = new ArrayList<>();
        PaymentMode.add("M-Pesa");
        PaymentMode.add("Cash");
        PaymentMode.add("Bank Transfer");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, PaymentMode);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMode.setAdapter(arrayAdapter);

        paymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                Config conf = com.threemsystems.rentmanager.Config.getInstance();
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

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payee.setText("");
                description.setText("");
                payDate.setText("");
                amount.setText("");
                payRef.setText("");
                mobilecode.setText("");
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DataEntry.class);
                startActivity(intent);
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Property_code, Unit_code, Tenant_identifier, Tenant_name, Payee, Expense_desc, Payment_amount, Pay_mode, Pay_refno, Mobilemoneycode, PayeeExp;
                Property_code = propertycode;
                //Unit_code = unitcode;
                // Tenant_identifier = tenantid;
                // Tenant_name = tenantname;
                Payee = String.valueOf(payee.getText());
                Expense_desc = String.valueOf(description.getText());
                Payment_amount = String.valueOf(amount.getText());
                Pay_mode = String.valueOf(paymentMode.getSelectedItem());
                Pay_refno = String.valueOf(payRef.getText());
                Mobilemoneycode = String.valueOf(mobilecode.getText());
                String payment_id = "";
                String payeeExp="";

                // if (!Pay_date.equals("") && !Property_code.equals("") && !Payee.equals("") && !Expense_desc.equals("") && !Payment_amount.equals("") && !Pay_mode.equals("") && !Pay_refno.equals("") &&
                //    !Mobilemoneycode.equals("")) {

                progressBar.setVisibility(View.VISIBLE);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String[] field = new String[8];
                        field[0] = "payment_date";
                        field[1] = "pay_refno";
                        field[2] = "mobilemoneycode";
                        field[3] = "payeeExp";
                        field[4] = "payment_amount";
                        field[5] = "pay_mode";
                        field[6] = "expense_desc";
                        field[7] = "property_code";


                        //Creating array for data
                        String[] data = new String[8];
                        data[0] = Pay_date;
                        data[1] = Pay_refno;
                        data[2] = Mobilemoneycode;
                        data[3] = Payee;
                        data[4] = Payment_amount;
                        data[5] = Pay_mode;
                        data[6] = Expense_desc;
                        data[7] = Property_code;

                        Config conf = com.threemsystems.rentmanager.Config.getInstance();
                        PutData putData = new PutData(conf.getSERVERURL() + "save_rentalExpenditure.php", "POST", field, data);

                        if (putData.startPut()) {
                            if (putData.onComplete()) {
                                progressBar.setVisibility(View.GONE);
                                String result = putData.getResult();
                                JSONObject jObject;
                                String flag = "", message = "";
                                try {
                                    jObject = new JSONObject(result);
                                    flag = jObject.getString("success");
                                    message = jObject.getString("message");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    System.out.println("JSON Exception");
                                }
                                if (flag.equals("true")) {
                                    //if (flag) {
                                    Toast.makeText(getApplicationContext(), message + " date= " + Pay_date, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                });
                //} else {
                //   Toast.makeText(getApplicationContext(), "All Fields are Required", Toast.LENGTH_SHORT).show();
                //}
            }
        });
        propertypref = getSharedPreferences("user_details", MODE_PRIVATE);
        idNo=propertypref.getString("idNo","MisingID");
        Config conf = com.threemsystems.rentmanager.Config.getInstance();
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
        property.setAdapter(new ArrayAdapter<spinnerItems>(NewExpenditure.this, android.R.layout.simple_spinner_dropdown_item, propertyspinnerlist));
    }

}
