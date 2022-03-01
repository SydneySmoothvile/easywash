package com.example.cbrapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cbrapp.Common.Common;
import com.example.cbrapp.Interface.IRecyclerItemSelectedListener;
import com.example.cbrapp.Model.Garage;


import java.util.ArrayList;
import java.util.List;

import example.cbrapp.R;

public class MyGarageAdapter extends RecyclerView.Adapter<MyGarageAdapter.MyViewHolder> {

    Context context;
    List<Garage> garageList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyGarageAdapter(Context context, List<Garage> garageList){
        this.context=context;
        this.garageList=garageList;
        cardViewList = new ArrayList<>();
        localBroadcastManager=LocalBroadcastManager.getInstance(context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_garage,viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_garage_name.setText(garageList.get(i).getName());
        myViewHolder.txt_garage_address.setText(garageList.get(i).getAddress());

        if(!cardViewList.contains(myViewHolder.card_garage)){
            cardViewList.add(myViewHolder.card_garage);
        }

        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                //set white background for all card not selected
                for(CardView cardView:cardViewList)
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

                //set selected background for only selected item
                myViewHolder.card_garage.setCardBackgroundColor(context.getResources()
                .getColor(android.R.color.holo_orange_dark));

                //Send Broadcast to tell Booking Activity enable Button next
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_GARAGE_STORE, garageList.get(pos));
                intent.putExtra(Common.KEY_STEP, 1);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return garageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_garage_name, txt_garage_address;
        CardView card_garage;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_garage = (CardView)itemView.findViewById(R.id.card_garage);
            txt_garage_address = (TextView)itemView.findViewById(R.id.txt_garage_address);
            txt_garage_name = (TextView)itemView.findViewById(R.id.txt_garage_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view,getAdapterPosition());
        }
    }
}
