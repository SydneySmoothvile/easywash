package com.example.cbrapp.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.cbrapp.Model.User;
import com.example.cbrapp.Common.Common;
import com.example.cbrapp.Model.BookingInformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import example.cbrapp.R;

public class BookingStep4Fragment extends Fragment {

    //get user info
    FirebaseFirestore fstore;
    FirebaseAuth fauth;
    FirebaseUser user;

    String userID, userName, userMobileNumber, userCarNumberPlate, userEmail, userDocID;
    public static String customerNumber;

    SimpleDateFormat simpleDateFormat;

    LocalBroadcastManager localBroadcastManager;

    Unbinder unbinder;

    AlertDialog dialog;

    @BindView(R.id.txt_booking_attendant_txt)
    TextView txt_booking_attendant_txt;
    @BindView(R.id.txt_booking_time_date_txt)
    TextView txt_booking_time_date_txt;
    @BindView(R.id.txt_garage_address_txt)
    TextView txt_garage_address_txt;
    @BindView(R.id.txt_booking_garage_name)
    TextView txt_booking_garage_name;
    @BindView(R.id.txt_garage_phone_number)
    TextView txt_garage_phone_number;
    @BindView(R.id.txt_garage_opening_hours)
    TextView txt_garage_opening_hours;

    @OnClick(R.id.btn_confirm)
    void confirmBooking(){

        dialog.show();

        /**
         * Process Timestamp
         * We will use timestamp to filter all booking with date is greater than today
         * For only displaying all future booking
         **/

        String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
        String [] convertTime = startTime.split("-"); //split ex : 9:00 - 11:00
        //get start time: get 9:00
        String[] startConvertTime = convertTime[0].split(":"); //we get 9
        int startHourInt = Integer.parseInt(startConvertTime[0].trim());//we get 9
        int startMinInt = Integer.parseInt(startConvertTime[1].trim());//we get 00

        Calendar bookingDateWithourHouse = Calendar.getInstance();
        bookingDateWithourHouse.setTimeInMillis(Common.bookingDate.getTimeInMillis());
        bookingDateWithourHouse.set(Calendar.HOUR_OF_DAY,startHourInt);
        bookingDateWithourHouse.set(Calendar.MINUTE,startMinInt);

        //create timestamp object and apply to BookingInformation
        Timestamp timestamp = new Timestamp(bookingDateWithourHouse.getTime());



        //create booking information
        BookingInformation bookingInformation = new BookingInformation();

        bookingInformation.setTimestamp(timestamp);
        bookingInformation.setDone(false);// always false, because we use this field to filter for display to user dashboard
        bookingInformation.setAttendantID(Common.currentAttendant.getAttendantId());
        bookingInformation.setAttendantName(Common.currentAttendant.getName());
        bookingInformation.setCustomerName(userName);
        bookingInformation.setCustomerPhone(userMobileNumber);
        bookingInformation.setCustomerCarNumberPlate(userCarNumberPlate);
        bookingInformation.setGarageId(Common.currentGarage.getGarageId());
        bookingInformation.setGarageAddress(Common.currentGarage.getAddress());
        bookingInformation.setGarageName(Common.currentGarage.getName());
        bookingInformation.setCityBooking(Common.location);

        bookingInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append(" on ")
                .append(simpleDateFormat.format(bookingDateWithourHouse.getTime())).toString());

        bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));

        //create user information
        User userInfo = new User();
        userInfo.setMobile_number(userMobileNumber);
        userInfo.setName(userName);
        userInfo.setCar_number_plate(userCarNumberPlate);




        //Submit of attendant document
        DocumentReference bookingDate = FirebaseFirestore.getInstance()
                .collection("Garages")
                .document(Common.location)
                .collection("Branch")
                .document(Common.currentGarage.getGarageId())
                .collection("Attendants")
                .document(Common.currentAttendant.getAttendantId())
                .collection(Common.simpleDateFormat.format(Common.bookingDate.getTime()))
                .document(String.valueOf(Common.currentTimeSlot));

        //write data
        bookingDate.set(bookingInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        /**
                         * Here we can write a function to check
                         * If already exist a booking we prevent a new booking**/
                        addToUserBooking(bookingInformation);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void addToUserBooking(BookingInformation bookingInformation) {


        /**
         * First we create a collection
         * check if  document is in this collection**/
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

        userBooking
                .whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1)// only take 1
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    //set data
                    userBooking.document()
                            .set(bookingInformation)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if(dialog.isShowing()){
                                        dialog.dismiss();
                                    }
                                    //add to google calendar
                                    addToCalendar(Common.bookingDate,
                                            Common.convertTimeSlotToString(Common.currentTimeSlot));
                                    resetStaticData();
                                    getActivity().finish(); //close activity
                                    Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    if(dialog.isShowing())
                                        dialog.dismiss();

                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else {
                    if(dialog.isShowing())
                        dialog.dismiss();

                    resetStaticData();
                    getActivity().finish(); //close activity
                    Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void  addToCalendar(Calendar bookingDate, String startDate) {

        String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
        String [] convertTime = startTime.split("-"); //split ex : 9:00 - 11:00
        //get start time: get 9:00
        String[] startConvertTime = convertTime[0].split(":");
        int startHourInt = Integer.parseInt(startConvertTime[0].trim());//we get 9
        int startMinInt = Integer.parseInt(startConvertTime[1].trim());//we get 00

        String[] endConvertTime = convertTime[0].split(":");
        int endHourInt = Integer.parseInt(endConvertTime[0].trim());//we get 11
        int endMinInt = Integer.parseInt(endConvertTime[1].trim());//we get 00

        Calendar startEvent = Calendar.getInstance();
        startEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        startEvent.set(Calendar.HOUR_OF_DAY, startHourInt); // set Event start hour
        startEvent.set(Calendar.MINUTE, startMinInt);// set Event start minute

        Calendar endEvent = Calendar.getInstance();
        endEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        endEvent.set(Calendar.HOUR_OF_DAY, endHourInt); // set Event end hour
        endEvent.set(Calendar.MINUTE, endMinInt);// set Event end minute

        //after we have startEvent and endEvent, convert it to format String
        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.UK);
        String startEventTime = calendarDateFormat.format(startEvent.getTime());
        String endEventTime = calendarDateFormat.format(endEvent.getTime());

        addToDeviceCalendar(startEventTime,endEventTime, "Car Wash Booking","wash from Malik Brendan","5th avenue Ongata Rongai");
                /**new StringBuilder("Wash from")
        .append(startTime)
        .append(" with ")
        .append(Common.currentAttendant.getName())
        .append(" at ")
        .append(Common.currentGarage.getName()).toString(),
                new StringBuilder("Address: ")
                        .append(Common.currentGarage.getAddress()).toString());**/


    }

    private void addToDeviceCalendar(String startEventTime, String endEventTime, String title, String description, String location) {

        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.UK);

        try {
            Date start = calendarDateFormat.parse(startEventTime);
            Date end = calendarDateFormat.parse(endEventTime);

            ContentValues event = new ContentValues();

            //put
            event.put(CalendarContract.Events.CALENDAR_ID,getCalendar(getContext()));
            event.put(CalendarContract.Events.TITLE,title);
            event.put(CalendarContract.Events.DESCRIPTION,description);
            event.put(CalendarContract.Events.EVENT_LOCATION,location);

            //Time
            event.put(CalendarContract.Events.DTSTART,start.getTime());
            event.put(CalendarContract.Events.DTEND,end.getTime());
            event.put(CalendarContract.Events.ALL_DAY,0);
            event.put(CalendarContract.Events.HAS_ALARM,1);

            String timeZone = TimeZone.getDefault().getID();
            event.put(CalendarContract.Events.EVENT_TIMEZONE,timeZone);

            Uri calendars = Uri.parse("content://com.android.calendar/calendars");

            //insert(calendars,event)

            ///Uri uri_save = getActivity().getContentResolver().insert(calendars, event);
            //Paper.init(getActivity());
            //Paper.book().write(Common.EVENT_URI_CACHE, uri_save.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private String getCalendar(Context context) {
        //get default calendar ID of Calendar of Gmail
        String gmailIdCalendar = "";
        String projection[] = {"_id", "calendar_displayName"};
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = context.getContentResolver();

        //select all calendar
        Cursor managedCursor = contentResolver.query(calendars,projection,null, null,null);
        if(managedCursor.moveToFirst()){
            String calName;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);
            do{
                calName = managedCursor.getString(nameCol);
                if(calName.contains("@gmail.com")){
                    gmailIdCalendar = managedCursor.getString(idCol);
                    break;
                }
            }while (managedCursor.moveToNext());
            managedCursor.close();
        }


        return gmailIdCalendar;
    }

    private void resetStaticData() {
        Common.step = 0;
        Common.currentTimeSlot = -1;
        Common.currentGarage = null;
        Common.currentAttendant = null;
        Common.bookingDate.add(Calendar.DATE, 0);//Current date added
    }

    BroadcastReceiver confirmBookingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    private void setData() {
        txt_booking_attendant_txt.setText(Common.currentAttendant.getName());
        txt_booking_time_date_txt.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
        .append(" on ")
        .append(simpleDateFormat.format(Common.bookingDate.getTime())));

        txt_garage_address_txt.setText(Common.currentGarage.getAddress());
        txt_booking_garage_name.setText(Common.currentGarage.getName());
        txt_garage_opening_hours.setText(Common.currentGarage.getOpenHours());
        txt_garage_phone_number.setText(Common.currentGarage.getPhone());
    }

    static BookingStep4Fragment instance;

    public static BookingStep4Fragment getInstance(){
        if (instance==null){
            instance = new BookingStep4Fragment();
        }
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Applying date format for confirmation
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmBookingReceiver, new IntentFilter(Common.KEY_CONFIRM_BOOKING));

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false)
                .build();

    }

    @Override
    public void onDestroy() {

        localBroadcastManager.unregisterReceiver(confirmBookingReceiver);

        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView = inflater.inflate(R.layout.fragment_booking_step_4,container,false);

        //init firebase
        fauth = FirebaseAuth.getInstance();
        user = fauth.getCurrentUser();
        fstore = FirebaseFirestore.getInstance();
        userID = fauth.getCurrentUser().getUid();

        //get documents collections from firestore
        DocumentReference documentReference = fstore.collection("users").document(userID);

        //get user information
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                /**
                 * nameTv.setText(documentSnapshot.getString("username"));
                emailTv.setText(documentSnapshot.getString("email"));
                phoneNumberTv.setText(documentSnapshot.getString("mobile_number"));
                carNumberPlateTv.setText(documentSnapshot.getString("car_number_plate"));**/

                userName = documentSnapshot.getString("username");
                userMobileNumber = documentSnapshot.getString("mobile_number");
                customerNumber = userMobileNumber;
                userCarNumberPlate = documentSnapshot.getString("car_number_plate");
                userEmail = documentSnapshot.getString("email");

            }
        });
        userDocID = userMobileNumber;

        unbinder = ButterKnife.bind(this, itemView);

        return itemView;
    }

}
