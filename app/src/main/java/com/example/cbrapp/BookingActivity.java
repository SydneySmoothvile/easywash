package com.example.cbrapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shuhart.stepview.StepView;
import com.example.cbrapp.Adapter.MyViewPagerAdapter;
import com.example.cbrapp.Common.Common;
import com.example.cbrapp.Common.NonSwipePager;
import com.example.cbrapp.Model.Attendant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import example.cbrapp.R;

public class BookingActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;
    AlertDialog dialog;
    CollectionReference attendantRef;

    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.view_pager)
    NonSwipePager viewPager;
    @BindView(R.id.previous_step_btn)
    Button previous_step_btn;
    @BindView(R.id.next_step_btn)
    Button next_step_btn;

    //Event
    @OnClick(R.id.previous_step_btn)
    void previousStep(){
        if(Common.step == 3 || Common.step > 0 ){
            Common.step--;
            viewPager.setCurrentItem(Common.step);
            if(Common.step < 3)
            {
                next_step_btn.setEnabled(true);
                setColorButton();
            }
        }
    }
    @OnClick(R.id.next_step_btn)
    void nextClick(){
        if(Common.step < 3|| Common.step == 0){
            Common.step++; //increase
            //choosing salon
            if (Common.step == 1) {
                if(Common.currentGarage != null){
                    loadAttendantByGarage(Common.currentGarage.getGarageId());
                }
            }
            else if(Common.step==2)// pick time slot
            {
                if(Common.currentAttendant!=null){
                    loadTimeSlotAttendant(Common.currentAttendant.getAttendantId());
                }
            }
            else if(Common.step==3)// confirm
            {
                if(Common.currentTimeSlot != -1){
                    confirmBooking();
                }
            }
            viewPager.setCurrentItem(Common.step);
        }
    }

    private void confirmBooking() {
        // send broadcast to fragment step four
        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadTimeSlotAttendant(String attendantId) {
        //send local broadcast to fragment step 3
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadAttendantByGarage(String garageId) {
        dialog.show();

        //Now, select all attendants of Garage
        ///Garages/Rongai/Branch/U82ADQo1MT32ttnG5sva/Attendants
        if(!TextUtils.isEmpty(Common.location)) {
            attendantRef = FirebaseFirestore.getInstance()
                    .collection("Garages")
                    .document(Common.location)
                    .collection("Branch")
                    .document(garageId)
                    .collection("Attendants");

            attendantRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            ArrayList<Attendant> attendants = new ArrayList<>();
                            for(QueryDocumentSnapshot attendantSnapshot:task.getResult()){
                                Attendant attendant = attendantSnapshot.toObject(Attendant.class);
                                attendant.setPassword("");// remove password because in client
                                attendant.setAttendantId(attendantSnapshot.getId());// get id of attendant

                                attendants.add(attendant);
                            }
                            //send Broadcast to bookingstep2fragment to load Recycler
                            Intent intent = new Intent(Common.KEY_ATTENDANT_LOAD_DONE);
                            intent.putParcelableArrayListExtra(Common.KEY_ATTENDANT_LOAD_DONE, attendants);
                            localBroadcastManager.sendBroadcast(intent);

                            //dismiss dialog
                            dialog.dismiss();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            dialog.dismiss();

                        }
                    });
        }
    }

    //Broadcast Receiver
    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int step = intent.getIntExtra(Common.KEY_STEP, 0);
            if(step==1){
                Common.currentGarage = intent.getParcelableExtra(Common.KEY_GARAGE_STORE);
            }else if(step==2)//pick time slot
            {
                Common.currentAttendant = intent.getParcelableExtra(Common.KEY_ATTENDANT_SELECTED);
            }else if(step==3)//confirm
            {
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT,-1);
            }
            next_step_btn.setEnabled(true);
            setColorButton();
        }
    };

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(BookingActivity.this);

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver, new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

        setupStepView();
        setColorButton();

        //View
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4);// we have 4 fragment so we need keep state of this 4
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //show step
                stepView.go(position, true);
                if(position == 0)
                    previous_step_btn.setEnabled(false);
                else
                    previous_step_btn.setEnabled(true);

                //set disable button next here
                next_step_btn.setEnabled(false);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("Garage");
        stepList.add("Attendant");
        stepList.add("Time");
        stepList.add("Confirm");
        stepView.setSteps(stepList);
    }

    private void setColorButton() {
        if(next_step_btn.isEnabled()){
            next_step_btn.setBackgroundResource(R.color.colorCbrYellow);
        }
        else{
            next_step_btn.setBackgroundResource(R.color.colorCbrBlack);
        }

        if(previous_step_btn.isEnabled()){
            previous_step_btn.setBackgroundResource(R.color.colorCbrYellow);
        }
        else{
            previous_step_btn.setBackgroundResource(R.color.colorCbrBlack);
        }
    }



}