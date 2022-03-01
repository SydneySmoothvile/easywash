package com.example.cbrapp.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cbrapp.Adapter.MyAttendantAdapter;
import com.example.cbrapp.Common.Common;
import com.example.cbrapp.Common.SpacesItemDecoration;
import com.example.cbrapp.Model.Attendant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import example.cbrapp.R;

public class BookingStep2Fragment extends Fragment {

    Unbinder unbinder;
    LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.recycler_attendant)
    RecyclerView recycler_attendant;

    private BroadcastReceiver attendantDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Attendant> attendantArrayList = intent.getParcelableArrayListExtra(Common.KEY_ATTENDANT_LOAD_DONE);
            //create adapter
            MyAttendantAdapter adapter = new MyAttendantAdapter(getContext(), attendantArrayList);
            recycler_attendant.setAdapter(adapter);
        }
    };

    static BookingStep2Fragment instance;

    public static BookingStep2Fragment getInstance(){
        if (instance==null){
            instance = new BookingStep2Fragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(attendantDoneReceiver, new IntentFilter(Common.KEY_ATTENDANT_LOAD_DONE));
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(attendantDoneReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_booking_step_two,container,false);

        unbinder = ButterKnife.bind(this, itemView);

        initView();
        
        return itemView;
    }

    private void initView() {
        recycler_attendant.setHasFixedSize(true);
        recycler_attendant.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recycler_attendant.addItemDecoration(new SpacesItemDecoration(4));
    }

}
