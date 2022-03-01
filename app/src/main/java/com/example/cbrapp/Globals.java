package com.example.cbrapp;

import android.app.Application;

public class Globals extends Application {
    private String mobileNumber=" ";

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
