package com.kangogo.threepmanager;

import android.content.Context;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Properties;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewholder> {
    Context context;
    List<Properties> propertyList;
    OnPropertyClickedListener listener;

    public PropertyAdapter(Context context, List propertyList) {
        this.context = context;
        this.propertyList = propertyList;
      //  this.listener = listener;
    }


    @Override
    public PropertyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.property_list,parent, false);
        return new PropertyViewholder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewholder holder, int position) {
       // Properties properties = propertyList.get(position);
		//com.kangogo.threepmanager.Holder.allRentalData properties = propertyList.get(position);
        //holder.txtPrtName.setText(properties.getPropertyname());
          //holder.tvCode.setText(properties.getCode());
       // holder.txtOwnId.setText(properties.getOwnerid());
        //holder.txtPrtDesc.setText(properties.getProperydesc());

    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public class PropertyViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvCode, tvOwnerId, tvPrtyName, tvDesc;
        public PropertyViewholder(@NonNull View itemView) {
            super(itemView);
            tvCode=itemView.findViewById(R.id.txtcode);
            tvOwnerId=itemView.findViewById(R.id.txtOwnId);
            tvPrtyName=itemView.findViewById(R.id.txtPrtName);
            tvDesc=itemView.findViewById(R.id.txtPrtDesc);

            tvPrtyName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            listener.onPropertyClicked(position);
        }
    }
    public interface OnPropertyClickedListener{
        void onPropertyClicked(int position);
    }
}
