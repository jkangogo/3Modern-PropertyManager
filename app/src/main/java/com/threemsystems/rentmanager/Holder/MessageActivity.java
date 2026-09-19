package com.threemsystems.rentmanager.Holder;

import com.threemsystems.rentmanager.ApiClient;
import com.threemsystems.rentmanager.Config;
import com.threemsystems.rentmanager.DateUi;
import com.threemsystems.rentmanager.R;
import com.threemsystems.rentmanager.ReportSupport;
import com.threemsystems.rentmanager.ScreenNav;
import com.threemsystems.rentmanager.SessionManager;
import com.threemsystems.rentmanager.UiNotifier;
import com.threemsystems.rentmanager.spinnerItems;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    Spinner recipient;
    EditText message, smsmonth;
    Button send;
    String sms_month;
    String msg_type = "";
    String propertycode, propertyname, unitcode, unitname, tenantid, tenantname = "";
    String idnumbers = "";

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> unitspinnerlist = new ArrayList<>();
    ArrayList<spinnerItems> tenantpinnerlist = new ArrayList<>();
    private Spinner property_spinner, unit_spinner, msgtype;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ScreenNav.bind(this);
        recipient = findViewById(R.id.spnRecipient);
        message = findViewById(R.id.etMessage);
		msgtype = findViewById(R.id.spMSGtype);		
       // contact = findViewById(R.id.etRecipient);
		smsmonth = findViewById(R.id.etsmsmonth);
		
        send = findViewById(R.id.btnSend);
		
		
		property_spinner=findViewById(R.id.spnPropertysms);
        unit_spinner=findViewById(R.id.spnUnitsms);
        //tenant_spinner=findViewById(R.id.spnTenantsms);
		
		ArrayList<String> msgtypelist = new ArrayList<>();
		msgtypelist.add("Invoices");
		msgtypelist.add("Payments");
        msgtypelist.add("Reminder");
       // msgtypelist.add("Stern Warning");
      //  msgtypelist.add("Clear Balance");
		msgtypelist.add("General Message");
		ArrayAdapter<String> arrayAdapter = ReportSupport.stringAdapter(this, msgtypelist);
        msgtype.setAdapter(arrayAdapter);
		
		checkForSmsPermission();
	
	property_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {		
		unitspinnerlist.clear();
             spinnerItems spinerproperty = ReportSupport.selected(parent);
             if (spinerproperty == null) {
                 return;
             }
             propertycode=spinerproperty.getId();
			 propertyname=spinerproperty.getName();
	ReportSupport.loadRows(MessageActivity.this, Config.getInstance().getSERVERURL()+"list_propertyunits.php", "property_code", propertycode,
            rows -> ReportSupport.fillSpinner(unit_spinner, unitspinnerlist, rows, "unit_code", "unit_name",
                    new spinnerItems("All", "All units")));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {    
        }
    });
	
	unit_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			
			 tenantpinnerlist.clear();
             spinnerItems spinerunitproperty = ReportSupport.selected(parent);
             if (spinerunitproperty == null) {
                 return;
             }
             unitcode=spinerunitproperty.getId();
			 unitname=spinerunitproperty.getName();
	 ReportSupport.loadRows(MessageActivity.this, Config.getInstance().getSERVERURL()+"list_tenantidname.php", "unit_code", unitcode,
             rows -> ReportSupport.fillSpinner(recipient, tenantpinnerlist, rows, "tenant_identifier", "tenant_name",
                     new spinnerItems("All", "All Tenants")));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {    
        }
    });
	recipient.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             spinnerItems spinertenant = ReportSupport.selected(parent);
             if (spinertenant == null) {
                 return;
             }
            tenantid=spinertenant.getId();
	if(ReportSupport.same(tenantid, "All")){
		idnumbers="";
		//contact.setText("");
		//contact.getText().clear();
	}else{	
// replace All tenants with empty string 	
	//tenantname=tenantname+spinertenant.getName()+";";
	//tenantname=tenantname.replace("All Tenants;","");
	//idnumbers=idnumbers+tenantid+";";  
		idnumbers=tenantid; 	
	}	
	//contact.setText(tenantname);
     	
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
	if(ReportSupport.same(msgtyperesult, "Stern Warning")){
		msg_type="Stern Warning";
	msg="Dear <client name>,\n Last reminder: You have NOT paid your rent\n";
	msg=msg+"Comply to avoid any inconvenience. Reply with this number";	
	}else if(ReportSupport.same(msgtyperesult, "Clear Balance")){
	msg="Dear <client name>,\n A reminder: pay your rent \n";
	msg=msg+" to avoid any inconveniences. For queries, complaints/compliments reply with this number";	
	}
	else if(ReportSupport.same(msgtyperesult, "General Message")){
		msg_type="General Message";
	msg="Dear <client name>,\nWishing you happy stay. \n";
	msg=msg+"Contact us for any queries,complaints/compliments\n";
	msg=msg+"By Property Managers";	
	}
	else if(ReportSupport.same(msgtyperesult, "Reminder")){
		msg_type="Reminder";
	msg="Dear <client name>,\n A kind reminder that rent payment is on or before 5th. \n";
	msg=msg+"Contact us for any queries, complaints or compliments.\n";
	msg=msg+"By Property Managers";	
	}
	else if(ReportSupport.same(msgtyperesult, "Invoices")){
		msg_type="Invoices";
	msg="Dear <client name>,\n Will send an invoice ";
	}
	else if(ReportSupport.same(msgtyperesult, "Payments")){
		msg_type="Payments";
	msg="Dear <client name>,\n Will send Payments ";
	}
		message.setText(msg);			
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {    
        }
    });
        DateUi.bindPicker(this, smsmonth, iso -> sms_month = iso);

	 send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
		String month = ReportSupport.filled(sms_month) ? sms_month : String.valueOf(smsmonth.getText());
		if(!ReportSupport.filled(month) && (ReportSupport.same(msg_type, "Invoices") || ReportSupport.same(msg_type, "Payments"))){
		UiNotifier.snack(MessageActivity.this, "The month field is required");
			
		}else{
		if (!ReportSupport.filled(propertycode)) {
			UiNotifier.snack(MessageActivity.this, "Choose a property first.");
			return;
		}
		sms_month = month;
		boolean sendToAll = ReportSupport.same(tenantid, "All") || ReportSupport.same(unitname, "All units") || !ReportSupport.filled(idnumbers);
		String contactType = sendToAll ? "All Tenants" : "Selected Tenants";
		String alertmsg = sendToAll
				? "You are about to send SMSs to several tenants under this category. Are you sure?"
				: "You are about to send SMS to the selected tenants . Are you sure?";
				AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                builder.setMessage(alertmsg)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
 public void onClick(DialogInterface dialog, int id) {
		if(ReportSupport.same(msg_type, "Invoices")){
			getMessageContent(contactType,"Invoices",idnumbers);
		}else if(ReportSupport.same(msg_type, "Payments")){
			getMessageContent(contactType,"Payments",idnumbers);
		}else if(ReportSupport.same(msg_type, "Reminder")){
		   getTelephoneNums(contactType,"Reminder",idnumbers);
		}else if(ReportSupport.same(msg_type, "General Message")){
		   getTelephoneNums(contactType,"generalsms",idnumbers);
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
            }
        });
	//populate the spinner for contacts
	String idNo=SessionManager.get(this).getOwnerId();
	ReportSupport.loadRows(this, Config.getInstance().getSERVERURL()+"list_properties.php", "owner_id", idNo,
            rows -> ReportSupport.fillSpinner(property_spinner, propertyspinnerlist, rows, "property_code", "property_name"));
    }
	
	protected void getTelephoneNums(String contacttype,String smstype,String idnumbers) {
				String smsMessage = message.getText().toString();
                boolean allContacts = ReportSupport.same(contacttype, "All Tenants");
                if (ReportSupport.filled(smsMessage) && (allContacts || ReportSupport.filled(idnumbers))) {
                    send.setEnabled(false);
                    String[] field = new String[4];
                    field[0] = "contacttype";
                    field[1] = "idnumbers";
                    field[2] = "pcode";
                    field[3] = "smstype";
                    String[] data = new String[4];
                    data[0] = contacttype;
                    data[1] = idnumbers;
                    data[2] = propertycode;
                    data[3] = smstype;
                    ApiClient.get().post(MessageActivity.this, Config.getInstance().getSERVERURL()+"getTelnumbers.php", field, data, new ApiClient.Callback() {
                        @Override
                        public void onSuccess(String result) {
                            send.setEnabled(true);
                            JSONArray jsonArray;
                            try {
                                jsonArray = new JSONArray(result);
                            } catch (JSONException e) {
                                UiNotifier.snack(MessageActivity.this, "Could not load contact numbers.");
                                return;
                            }
                            if (jsonArray.length() == 0) {
                                UiNotifier.snack(MessageActivity.this, "No matching contacts were found.");
                                return;
                            }
                            int sent = 0;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    sendSMS(obj.getString("name"), obj.getString("telno"), smsMessage);
                                    sent++;
                                } catch (JSONException e) {
                                    Log.e("PManager", "Could not send SMS row", e);
                                }
                            }
                            UiNotifier.snack(MessageActivity.this, sent + " message(s) queued.");
                        }

                        @Override
                        public void onError(String message) {
                            send.setEnabled(true);
                            UiNotifier.snack(MessageActivity.this, message);
                        }
                    });
        }
        else{
        UiNotifier.snack(MessageActivity.this, "All fields are required.");
         }
	}
	protected void getMessageContent(String contacttype,String messagetype,String idnumbers) {
                boolean allContacts = ReportSupport.same(contacttype, "All Tenants");
                if (allContacts || ReportSupport.filled(idnumbers)) {
                    send.setEnabled(false);
                            String[] field = new String[5];
                            field[0] = "messagetype";
                            field[1] = "idnumbers";
							field[2] = "pcode";
							field[3] = "contacttype";
							field[4] = "sms_month";
                            String[] data = new String[5];
                            data[0] = messagetype;
                            data[1] = idnumbers;
							data[2] = propertycode;
							data[3] = contacttype;
							data[4] = sms_month;
                    ApiClient.get().post(MessageActivity.this, Config.getInstance().getSERVERURL()+"getMSGContent.php", field, data, new ApiClient.Callback() {
                        @Override
                        public void onSuccess(String result) {
                            send.setEnabled(true);
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                String msg1 = "";
                                if ("Invoices".equals(messagetype)) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String[] namerrays = object.getString("name").split(" ", -1);
                                        msg1 = "Hello " + namerrays[0] + "," + "\n" + "Invoice for: " + object.getString("invoice_month") + ":" + "\n";
                                        msg1 = msg1 + "Rent = " + object.getString("rent") + "\n";
                                        String stritems = object.getString("invoice_items");
                                        String[] itemarrays = stritems.split(",", -1);
                                        for (int j = 0; j < itemarrays.length; j++) {
                                            msg1 = msg1 + itemarrays[j] + "\n";
                                        }
                                        msg1 = msg1 + "TOTAL INVOICED = " + object.getString("total_invoiced") + "\n";
                                        msg1 = msg1 + "NET BALANCE = " + object.getString("net_balance") + "\n";
                                        msg1 = msg1 + "By property managers";
                                        sendSMS(namerrays[0], object.getString("telno"), msg1);
                                    }
                                } else if ("Payments".equals(messagetype)) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String[] namerrays = object.getString("name").split(" ", -1);
                                        msg1 = "Hello " + namerrays[0] + "," + "\n" + "Payment for: " + object.getString("pay_date") + ":" + "\n";
                                        msg1 = msg1 + "Amount Paid = " + object.getString("pay_amount") + "\n";
                                        msg1 = msg1 + "Mpesa Code = " + object.getString("mpesacode") + "\n";
                                        msg1 = msg1 + "Bank Ref = " + object.getString("refno") + "\n";
                                        msg1 = msg1 + "NET BALANCE = " + object.getString("net_balance") + "\n";
                                        msg1 = msg1 + "By property managers";
                                        sendSMS(namerrays[0], object.getString("telno"), msg1);
                                    }
                                }
                                UiNotifier.snack(MessageActivity.this, "Messages queued.");
                            } catch (JSONException e) {
                                UiNotifier.snack(MessageActivity.this, "Could not build the message content.");
                            }
                        }

                        @Override
                        public void onError(String message) {
                            send.setEnabled(true);
                            UiNotifier.snack(MessageActivity.this, message);
                        }
                    });
        }
        else{
        UiNotifier.snack(MessageActivity.this, "All fields are required.");
         }
	}

	
	protected void sendSMS(String name,String contact,String msg) {
    if (contact == null || contact.trim().isEmpty() || msg == null || msg.trim().isEmpty()) {
        return;
    }
    String scAddress = null;
    PendingIntent sentIntent = null, deliveryIntent = null;
    SmsManager smsManager;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        smsManager = getSystemService(SmsManager.class);
    } else {
        smsManager = SmsManager.getDefault();
    }
	    msg = msg.replaceFirst("Dear <client name>", "Hello "+name);
		try {
		smsManager.sendTextMessage(contact, scAddress, msg, sentIntent, deliveryIntent);
	} catch (Exception e) {
		Log.e("PManager", "Could not send SMS", e);
	}
       }
	   
    @Override
   public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
    case MY_PERMISSIONS_REQUEST_SEND_SMS: {
        if (permissions[0].equalsIgnoreCase
            (Manifest.permission.SEND_SMS)
            && grantResults[0] ==
            PackageManager.PERMISSION_GRANTED) {
            // Permission was granted. Enable sms button.
            //enableSmsButton();
			send.setEnabled(true); 
			 UiNotifier.snack(MessageActivity.this, "Permission allowed.");
			
        } else {
            // Permission denied.
                     
		UiNotifier.snack(MessageActivity.this, "Permission to send message was denied.");
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

}