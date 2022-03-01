package com.example.cbrapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.example.cbrapp.Common.Common;
import com.example.cbrapp.Interface.IRecyclerItemSelectedListener;
import com.example.cbrapp.Model.ServicesItem;


import java.util.List;

import example.cbrapp.R;

public class MyServiceItemAdapter extends RecyclerView.Adapter<MyServiceItemAdapter.MyViewHolder> {

    Context context;
    List<ServicesItem> servicesItemList;

    public MyServiceItemAdapter(Context context, List<ServicesItem> servicesItemList) {
        this.context = context;
        this.servicesItemList = servicesItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_services_item, viewGroup,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Picasso.get().load(servicesItemList.get(i).getImage()).into(myViewHolder.img_services_item);
        myViewHolder.txt_service_item_name.setText(Common.formatServiceItemName(servicesItemList.get(i).getName()));
        myViewHolder.txt_service_item_price.setText(new StringBuilder("KSh").append(servicesItemList.get(i).getPrice()));
    }

    @Override
    public int getItemCount() {
        return servicesItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_service_item_name, txt_service_item_price, txt_add_to_cart;
        ImageView img_services_item;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public IRecyclerItemSelectedListener getiRecyclerItemSelectedListener() {
            return iRecyclerItemSelectedListener;
        }

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_services_item = (ImageView) itemView.findViewById(R.id.img_services_item);
            txt_service_item_name = (TextView) itemView.findViewById(R.id.txt_service_item_name);
            txt_service_item_price = (TextView) itemView.findViewById(R.id.txt_service_item_price);
            txt_add_to_cart = (TextView) itemView.findViewById(R.id.txt_add_to_cart);
            txt_add_to_cart.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
