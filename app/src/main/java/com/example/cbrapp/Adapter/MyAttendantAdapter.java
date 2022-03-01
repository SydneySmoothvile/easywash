package com.example.cbrapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cbrapp.Common.Common;
import com.example.cbrapp.Interface.IRecyclerItemSelectedListener;
import com.example.cbrapp.Model.Attendant;


import java.util.ArrayList;
import java.util.List;

import example.cbrapp.R;


public class MyAttendantAdapter extends RecyclerView.Adapter<MyAttendantAdapter.MyViewHolder> {

    Context context;
    List<Attendant> attendantList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyAttendantAdapter(Context context, List<Attendant> attendantList) {
        this.context = context;
        this.attendantList = attendantList;
        cardViewList = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_attendant,viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_attendant_name.setText(attendantList.get(i).getName());
        myViewHolder.ratingBar.setRating((float)attendantList.get(i).getRating());

        if(!cardViewList.contains(myViewHolder.card_attendant)) {
            cardViewList.add(myViewHolder.card_attendant);
        }

        myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {
                //set background for all item not chosen
                for(CardView cardView:cardViewList) {
                    cardView.setCardBackgroundColor(context.getResources()
                            .getColor(android.R.color.white));
                }
                //set background for chosen item
                myViewHolder.card_attendant.setCardBackgroundColor(
                        context.getResources()
                        .getColor(android.R.color.holo_orange_dark)
                );
                //send Local broadcast to enable next
                Intent intent = new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_ATTENDANT_SELECTED, attendantList.get(i));
                intent.putExtra(Common.KEY_STEP, 2);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendantList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_attendant_name;
        RatingBar ratingBar;
        CardView card_attendant;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_attendant = (CardView)itemView.findViewById(R.id.card_attendant);
            txt_attendant_name = (TextView) itemView.findViewById(R.id.txt_attendant_name);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rtb_attendant);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view,getAdapterPosition());
        }
    }
}
