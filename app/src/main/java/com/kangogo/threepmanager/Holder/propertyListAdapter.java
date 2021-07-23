
package com.kangogo.threepmanager.Holder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.kangogo.threepmanager.R;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import android.widget.LinearLayout;


public class propertyListAdapter extends BaseAdapter {
    private Context context;
	private ArrayList<String> ownerid;
    private ArrayList<String> pnames;
	 private ArrayList<String> pdesc;
	 
    //private final String[] propertyvalues;

    public propertyListAdapter
	(Context context, ArrayList<String> ownerid, ArrayList<String> pnames, ArrayList<String> pdesc) {
        this.context = context;
        this.ownerid = ownerid;
		 this.pnames = pnames;
		  this.pdesc = pdesc;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

         //Creating a linear layout
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
 
        
        //Creating a textview to show the title
		TextView textViewownerid = new TextView(context);
        textViewownerid.setText(ownerid.get(position));
       
	    TextView textViewnames = new TextView(context);
        textViewnames.setText(pnames.get(position));
		
		TextView textViewdesc = new TextView(context);
        textViewdesc.setText(pdesc.get(position));
 
       
        //Adding views to the layout
        linearLayout.addView(textViewownerid);  
		linearLayout.addView(textViewnames);  
		linearLayout.addView(textViewdesc);  		
 
        //Returnint the layout
        return linearLayout;
    }

    @Override
    public int getCount() {
        //return propertyvalues.length;
		 return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}