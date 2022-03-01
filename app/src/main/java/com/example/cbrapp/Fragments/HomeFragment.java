package com.example.cbrapp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;
import com.example.cbrapp.BookingActivity;

import com.example.cbrapp.Common.Common;
import com.example.cbrapp.Interface.IBookingInfoLoadListener;
import com.example.cbrapp.Interface.IBookingInformationChangeListener;
import com.example.cbrapp.Model.BookingInformation;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import example.cbrapp.R;

public class HomeFragment extends Fragment implements IBookingInfoLoadListener, IBookingInformationChangeListener {
    Unbinder unbinder;

    AlertDialog dialog;

    @BindView(R.id.services_notifications_badge)
    NotificationBadge notificationBadge;

    //Material card view declarations
    CardView carWashBooking, cbrServices, washHistory, cbrNotifications, bookingInformationCard;

    //TextView declarations
    TextView bookingGarageAddress, bookingTime, attendantBooked, timeRemain, userMobileNumber;

    //Button declarations
    Button changeButton, deleteButton;

    SimpleDateFormat simpleDateFormat;

    //Interface
    IBookingInfoLoadListener iBookingInfoLoadListener;
    IBookingInformationChangeListener iBookingInformationChangeListener;

    //String variables
    String customerPhoneNumber, userID;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View itemView;
        itemView = inflater.inflate(R.layout.fragment_home,container, false);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        //init firebase
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseUser user = fauth.getCurrentUser();
        userID = fauth.getCurrentUser().getUid();


        //init interface
        iBookingInfoLoadListener = this;
        iBookingInformationChangeListener = this;


        //init card views
        bookingInformationCard = itemView.findViewById(R.id.card_booking_info);
        carWashBooking = itemView.findViewById(R.id.card_view_booking);
        cbrServices = itemView.findViewById(R.id.card_view_services);
        washHistory = itemView.findViewById(R.id.card_view_history);
        cbrNotifications = itemView.findViewById(R.id.card_view_notification);

        //init TextViews
        bookingGarageAddress = itemView.findViewById(R.id.txt_booking_garage_address);
        bookingTime = itemView.findViewById(R.id.txt_booking_time_info);
        attendantBooked = itemView.findViewById(R.id.txt_booking_attendant_name);
        timeRemain = itemView.findViewById(R.id.txt_time_remain);
        userMobileNumber = itemView.findViewById(R.id.txt_user_phone);

        //init Buttons
        changeButton = itemView.findViewById(R.id.change_btn);
        deleteButton = itemView.findViewById(R.id.delete_btn);

        //get date
        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy", Locale.UK);

        //check if user is load in
        if(user!=null){
            loadUserBooking();
        }else {
            Toast.makeText(getActivity(),"Error!! ",Toast.LENGTH_SHORT).show();
        }


        //on click the booking card
        carWashBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(),"Book now!!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), BookingActivity.class));
            }
        });

        //on click the Services card
        cbrServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Services!!",Toast.LENGTH_SHORT).show();
                ServicesFragment servicesFragment = new ServicesFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, servicesFragment);
                transaction.commit();
            }
        });

        //on click the History card
        washHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"History is Fun!!",Toast.LENGTH_SHORT).show();
            }
        });

        //on click the Notifications card
        cbrNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Notifications!!",Toast.LENGTH_SHORT).show();
            }
        });

        //on click change button
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"CHANGING BOOKING DATE!!",Toast.LENGTH_SHORT).show();
                changingBookingFromUser();
            }

            private void changingBookingFromUser() {
                //show dialog
                androidx.appcompat.app.AlertDialog.Builder confirmDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                        .setCancelable(false)
                        .setTitle("Hey")
                        .setMessage("Do you want to change your booking information?\nBecause we will delete your old booking information\n Just confirm")
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteBookingFromUser(true);//True because we call from button change
                            }
                        });
                confirmDialog.show();

            }
        });
        //on click delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBookingFromAttendant(false);
                Toast.makeText(getActivity(),"DELETION SUCCESS!!",Toast.LENGTH_SHORT).show();
            }

            private void deleteBookingFromAttendant(boolean isChange) {
                /**
                 * /Garages/Rongai/Branch/U82ADQo1MT32ttnG5sva/Attendants/BzetQSEQqt2M4hrOeGA7/23_09_2020/4
                 * first delete from barber collection
                 * second delete from user booking collection
                 * finally delete event
                 * **/

                if(Common.currentBooking != null){

                    dialog.show();

                    //Get Booking Information in barber object
                    DocumentReference washBookingInfo = FirebaseFirestore.getInstance()
                            .collection("Garages")
                            .document("Rongai")
                            .collection("Branch")
                            .document(Common.currentBooking.getGarageId())
                            .collection("Attendants")
                            .document(Common.currentBooking.getAttendantID())
                            .collection(Common.convertTimeSlotToStringKey(Common.currentBooking.getTimestamp()))
                            .document(String.valueOf(Common.currentBooking.getSlot()));
                    //when we get the document we just delete it
                    washBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //After deleting from attendant
                            //we start delete from User collection

                            deleteBookingFromUser(isChange);
                        }
                    });


                }else {
                    Toast.makeText(getActivity(),"CURRENT BOOKING IS MUST NOT BE NULL!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        unbinder = ButterKnife.bind(this, itemView);
        return itemView;
    }

    private void deleteBookingFromUser(boolean isChange) {
        // first we get information from User
        ///User/El2ZnDNV69fM5oV44PNVNfG5yrH3/Booking/BcEql0RtRsPmOzBV2BGj
        if(!TextUtils.isEmpty(Common.currentBookingId)){

            DocumentReference userBookingInfo = FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(userID)
                    .collection("Booking")
                    .document(Common.currentBookingId);

            //delete
            userBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //after deleting user info, delete the event
                    //first we get the Uri
                    //Paper.init(getActivity());
                    //Uri eventUri = Uri.parse(Paper.book().read(Common.EVENT_URI_CACHE).toString());
                    //getActivity().getContentResolver().delete(eventUri, null, null);

                    //Toast.makeText(getActivity(),"SUCCESS DELETING BOOKING!!",Toast.LENGTH_SHORT).show();

                    //refresh
                    loadUserBooking();
                    //check if isChange-> call from change button, we will fired interface
                    if(isChange){
                        iBookingInformationChangeListener.onBookingInformationChange();
                    }

                }
            });


        }else{
            Toast.makeText(getActivity(),"CURRENT BOOKING IS MUST NOT BE EMPTY!!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserBooking();
    }

    private void loadUserBooking() {

        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("User")
                .document(userID)
                .collection("Booking");

        //get current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());

        //Select booking information from Firebase with done=false and timestamp greater today
        userBooking
                .whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1)// only take 1
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {

                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    BookingInformation bookingInformation =
                                            queryDocumentSnapshot.toObject(BookingInformation.class);
                                    iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformation, queryDocumentSnapshot.getId());
                                    break;//Exit loop
                                }

                            } else {
                                iBookingInfoLoadListener.onBookingInfoLoadEmpty();
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage());
            }
        });
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    public void onBookingInfoLoadEmpty() {
        bookingInformationCard.setVisibility(View.GONE);
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInformation, String bookingId) {

        Common.currentBooking = bookingInformation;
        Common.currentBookingId = bookingId;

        bookingGarageAddress.setText(bookingInformation.getGarageAddress());
        attendantBooked.setText(bookingInformation.getAttendantName());
        bookingTime.setText(bookingInformation.getTime());
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                bookingInformation.getTimestamp().toDate().getTime(),
                Calendar.getInstance().getTimeInMillis(), 0).toString();

        timeRemain.setText(dateRemain);
        userMobileNumber.setText(bookingInformation.getCustomerPhone());
        bookingInformationCard.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBookingInfoLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInformationChange() {
        //Here we just start activity booking
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }
}
