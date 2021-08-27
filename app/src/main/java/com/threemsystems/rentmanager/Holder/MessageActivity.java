package com.threemsystems.rentmanager.Holder;
import com.threemsystems.rentmanager.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.DialogInterface;

import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.vishnusivadas.advanced_httpurlconnection.PutData;
import android.widget.ProgressBar;
import android.telephony.SmsManager;
import android.content.SharedPreferences;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;


//import androidx.core.app.ContextCompat;
import androidx.core.app.ActivityCompat;


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
    String phoneNo;
    String tomessage;
    String propertycode,propertyname,unitcode,unitname,tenantid,tenantname="";
    String idnumbers="";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    SharedPreferences propertypref ;
    ProgressBar progressBar;
    private JSONArray result;
    public static final String TAG_jsonarray = "result";
    public static final String TAG_propertycode = "property_code";
    public static final String TAG_propertyname = "property_name";
    ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>();
    private Spinner property_spinner,unit_spinner,tenant_spinner,msgtype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        recipient = findViewById(R.id.spnRecipient);
        message = findViewById(R.id.etMessage);
        msgtype = findViewById(R.id.spMSGtype);
        contact = findViewById(R.id.etRecipient);
        send = findViewById(R.id.btnSend);


        property_spinner=findViewById(R.id.spnPropertysms);
        unit_spinner=findViewById(R.id.spnUnitsms);
        //tenant_spinner=findViewById(R.id.spnTenantsms);

        ArrayList<String> msgtypelist = new ArrayList<>();
        msgtypelist.add("General Reminder");
        msgtypelist.add("Stern Warning");
        msgtypelist.add("Clear Balance");
        msgtypelist.add("Good Will");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, msgtypelist);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        msgtype.setAdapter(arrayAdapter);

        checkForSmsPermission();

        property_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                unitspinnerlist.add(new spinnerItems("All", "All units"));
                tenantpinnerlist.add(new spinnerItems("All", "All Tenants"));
                getspinnerData(url,propertycode,"property_code","getSelectunitsResult");
                // Toast.makeText(getApplicationContext(), "Property Code: "+spinerproperty.getId()+",  Property Name : "+spinerproperty.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        unit_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                tenantpinnerlist.clear();
                //tenantpinnerlist.notifyDataSetChanged();
                tenantpinnerlist.add(new spinnerItems("All", "All Tenants"));
                spinnerItems spinerunitproperty = (spinnerItems) parent.getSelectedItem();
                unitcode=spinerunitproperty.getId();
                unitname=spinerunitproperty.getName();
                Config conf = com.threemsystems.rentmanager.Config.getInstance();
                String url = conf.getSERVERURL()+"list_tenantidname.php";
                getspinnerData(url,unitcode,"unit_code","getSelectedTenantResult");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        recipient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerItems spinertenant= (spinnerItems) parent.getSelectedItem();
                tenantid=spinertenant.getId();
                if(tenantid=="All Tenants"){
                    idnumbers="";
                    //contact.setText("");
                    contact.getText().clear();
                }else{
// replace All tenants with empty string

                    tenantname=tenantname+spinertenant.getName()+";";
                    tenantname=tenantname.replace("All Tenants;","");
                    idnumbers=idnumbers+tenantid+";";

                }
                contact.setText(tenantname);

//create an array of id numbers
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        msgtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //spinnerItems spinertenant= (spinnerItems) parent.getSelectedItem();
                String msgtyperesult = arrayAdapter.getItem(position).toString();
                //contact.setText(tenantname);

                String msg="";
                if(msgtyperesult=="Stern Warning"){
                    msg="Dear <client name>,\n Last reminder: You have NOT paid your rent\n";
                    msg=msg+"Comply to avoid any inconvenience. Reply with this number";
                }else if(msgtyperesult=="Clear Balance"){
                    msg="Dear <client name>,\n A reminder: pay your rent \n";
                    msg=msg+" to avoid any inconveniences. For queries, complaints/compliments reply with this number";
                }
                else if(msgtyperesult=="Good Will"){
                    msg="Dear <client name>,\nWishing you happy stay. ";
                    msg=msg+"Thank you for being a good tenant.\nFor queries, complaints/compliments, reply with this number";
                }
                else if(msgtyperesult=="General Reminder"){
                    msg="Dear <client name>,\n Be reminded that rent payment is on or before 5th every month\n";
                    msg=msg+"For any queries, complaints or compliments reply with this number.";
                }
                message.setText(msg);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendSMSMessage();
                //get the contact type
                String alertmsg="";
                //==============================
                if(unitname=="All units"){
                    alertmsg="You are about to send SMSs to all tenants in this property. Are you sure?";
                }else {
                    alertmsg="You are about to send SMS to the selected tenants . Are you sure?";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setMessage(alertmsg)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(unitname=="All units"){
                                    getTelephoneNums("All Tenants",idnumbers);
                                }else {
                                    getTelephoneNums("Selected Tenants",idnumbers);
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }


        });

        //populate the spinner for contacts
        propertypref = getSharedPreferences("user_details", MODE_PRIVATE);
        String idNo=propertypref.getString("idNo","MisingID");
        Config conf = com.threemsystems.rentmanager.Config.getInstance();
        String url = conf.getSERVERURL()+"list_properties.php";
        getspinnerData(url,idNo,"owner_id","getPropertySpinnerResult");
        //set the text
    }

    protected void getTelephoneNums(String contacttype,String idnumbers) {
        String smsMessage = message.getText().toString();
        String scAddress = null;
        //String currentString = contact.getText().toString();
        //String[] separated = idnumbers.split(";");

        if(!smsMessage.equals("") && !idnumbers.equals("")) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[3];
                    field[0] = "contacttype";
                    field[1] = "idnumbers";
                    field[2] = "pcode";

                    //Creating array for data
                    String[] data = new String[3];
                    data[0] = contacttype;
                    data[1] = idnumbers;
                    data[2] = propertycode;

                    Config conf = com.threemsystems.rentmanager.Config.getInstance();
                    String url=conf.getSERVERURL()+"getTelnumbers.php";
                    PutData putData = new PutData(url, "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {

                            //progressBar.setVisibility(View.GONE);
                            String result = putData.getResult();
                            Toast.makeText(getApplicationContext(), "Results=  "+result, Toast.LENGTH_LONG).show();
                            JSONArray jsonArray = null;
                            try {
                                jsonArray = new JSONArray(result);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // String[] contact = new String[jsonArray.length()];
                            //String[] name = new String[jsonArray.length()];


                            if(jsonArray.length()>0){
                                //iterate and send sms
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = null;
                                    try {
                                        obj = jsonArray.getJSONObject(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //contact[i] = obj.getString("telno");
                                    //name[i] = obj.getString("name");
                                    try {
                                        sendSMS(obj.getString("name"),obj.getString("telno"));

                                        //sendSMSMessage();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    //  try {
                                    //     Toast.makeText(getApplicationContext(), "SMS sent to. "+ obj.getString("telno"),
//Toast.LENGTH_LONG).show();
                                    //} catch (JSONException e) {
                                    // e.printStackTrace();
                                    //}

                                }
                                Toast.makeText(getApplicationContext(), "Mesage sent" ,
                                        Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Incorrect contact  details", Toast.LENGTH_LONG).show();
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

    protected void sendSMS(String name,String contact) {
        String smsMessage = message.getText().toString();
        // Set the service center address if needed, otherwise null.
        String scAddress = null;
        // Set pending intents to broadcast
        // when message sent and when delivered, or set to null.
        PendingIntent sentIntent = null, deliveryIntent = null;
        // Use SmsManager.
        SmsManager smsManager = SmsManager.getDefault();

        //for(int i=0;i<contacts.length;i++) {
        // sendMsg(numberlist.get(i));
        smsMessage = smsMessage.replaceFirst("Dear <client name>", "Hello "+name);
        smsManager.sendTextMessage
                (contact, scAddress, smsMessage,
                        sentIntent, deliveryIntent);
        // }


    }

    protected void sendSMSMessage() {
        phoneNo = "0722977672";
        //tomessage ="Try message again";
        // Set the destination phone number to the string in editText.
        // Get the text of the SMS message.
        String smsMessage = message.getText().toString();
        //smsMessage="Dear <client name>,\n Be reminded rent payment is on or before 5th every month\n";
        //smsMessage=smsMessage+"For any queries, complaints or compliments reply with this number";
        // Set the service center address if needed, otherwise null.
        String scAddress = null;
        // Set pending intents to broadcast
        // when message sent and when delivered, or set to null.
        PendingIntent sentIntent = null, deliveryIntent = null;
        // Use SmsManager.
        SmsManager smsManager = SmsManager.getDefault();
        //String currentString = contact.getText().toString();
        //String[] separated = currentString.split(";");


        // for(int i=0;i<separated.length;i++) {
        // sendMsg(numberlist.get(i));
        smsManager.sendTextMessage
                (phoneNo, scAddress, smsMessage,
                        sentIntent, deliveryIntent);
        // }

        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (permissions[0].equalsIgnoreCase
                        (Manifest.permission.SEND_SMS)
                        && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted. Enable sms button.
                    //enableSmsButton();
                    send.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Permission allowed.",
                            Toast.LENGTH_LONG).show();

                } else {
                    // Permission denied.

                    Toast.makeText(getApplicationContext(), "Permission to send message was denied.",
                            Toast.LENGTH_LONG).show();
                    // Disable the sms button.
                    // disableSmsButton();
                    send.setEnabled(false);
                }
            }

        }
    }

    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            //Log.d(TAG, "Permission granted");
            // Permission not yet granted. Use requestPermissions().
            // MY_PERMISSIONS_REQUEST_SEND_SMS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            // Permission already granted. Enable the SMS button.
            // enableSmsButton();
        }
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
        property_spinner.setAdapter(new ArrayAdapter<spinnerItems>(MessageActivity.this, android.R.layout.simple_spinner_dropdown_item, propertyspinnerlist));
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
        unit_spinner.setAdapter(new ArrayAdapter<spinnerItems>(MessageActivity.this, android.R.layout.simple_spinner_dropdown_item, unitspinnerlist));
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
        recipient.setAdapter(new ArrayAdapter<spinnerItems>(MessageActivity.this, android.R.layout.simple_spinner_dropdown_item, tenantpinnerlist));
    }

}