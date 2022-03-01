package com.example.cbrapp.Common;

import com.google.firebase.Timestamp;
import com.example.cbrapp.DashboardActivity;
import com.example.cbrapp.Model.Attendant;
import com.example.cbrapp.Model.BookingInformation;
import com.example.cbrapp.Model.Garage;

import com.example.cbrapp.Model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Common {
    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_GARAGE_STORE = "GARAGE_SAVE";
    public static final String KEY_ATTENDANT_LOAD_DONE = "ATTENDANT_LOAD_DONE";

    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";
    public static final String KEY_STEP = "STEP";

    public static final String KEY_ATTENDANT_SELECTED = "ATTENDANT_SELECTED";

    public static final int TIME_SLOT_TOTAL = 5 ;
    public static final Object DISABLE_TAG ="DISABLE" ;
    public static final String KEY_TIME_SLOT ="TIME_SLOT" ;
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static final String USER_MOBILE_NUMBER = DashboardActivity.userMobileNumber;
    public static final String EVENT_URI_CACHE = "URI_EVENT_SAVE" ;

    public static Garage currentGarage;
    public static int step = 0; // init first step is 0
    public static String location="";
    public static Attendant currentAttendant;
    public static int currentTimeSlot= -1;
    public static Calendar bookingDate=Calendar.getInstance();
    public static User currentUser;


    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy", Locale.UK);// use when need formatting
    public static BookingInformation currentBooking;
    public static String currentBookingId="";

    public static String convertTimeSlotToString(int slot) {
        switch (slot){
            case 0:
                return "9:00-11:00";
            case 1:
                return "11:00-13:00";
            case 2:
                return "13:00-15:00";
            case 3:
                return "15:00-17:00";
            case 4:
                return "17:00-19:00";
            default:
                return "Closed";

        }
    }

    public static String convertTimeSlotToStringKey(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy", Locale.UK);
        return simpleDateFormat.format(date);
    }

    public static String formatServiceItemName(String name) {
        return name.length()>13 ? new StringBuilder(name.substring(0,10)).append("...").toString() : name;
    }

}
