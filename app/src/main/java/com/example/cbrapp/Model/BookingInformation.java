package com.example.cbrapp.Model;

import com.google.firebase.Timestamp;

public class BookingInformation {
    private String cityBooking, customerName, customerCarNumberPlate, customerPhone, time, attendantID,
            attendantName, garageId, garageName, garageAddress;

    private long slot;

    private Timestamp timestamp;
    private boolean done;

    public BookingInformation() {
    }

    public BookingInformation(String customerName, String customerCarNumberPlate, String customerPhone, String time, String attendantID, String attendantName, String garageId, String garageName, String garageAddress, long slot) {
        this.customerName = customerName;
        this.customerCarNumberPlate = customerCarNumberPlate;
        this.customerPhone = customerPhone;
        this.time = time;
        this.attendantID = attendantID;
        this.attendantName = attendantName;
        this.garageId = garageId;
        this.garageName = garageName;
        this.garageAddress = garageAddress;
        this.slot = slot;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCarNumberPlate() {
        return customerCarNumberPlate;
    }

    public void setCustomerCarNumberPlate(String customerCarNumberPlate) {
        this.customerCarNumberPlate = customerCarNumberPlate;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAttendantID() {
        return attendantID;
    }

    public void setAttendantID(String attendantID) {
        this.attendantID = attendantID;
    }

    public String getAttendantName() {
        return attendantName;
    }

    public void setAttendantName(String attendantName) {
        this.attendantName = attendantName;
    }

    public String getGarageId() {
        return garageId;
    }

    public void setGarageId(String garageId) {
        this.garageId = garageId;
    }

    public String getGarageName() {
        return garageName;
    }

    public void setGarageName(String garageName) {
        this.garageName = garageName;
    }

    public String getGarageAddress() {
        return garageAddress;
    }

    public void setGarageAddress(String garageAddress) {
        this.garageAddress = garageAddress;
    }

    public long getSlot() {
        return slot;
    }

    public void setSlot(long slot) {
        this.slot = slot;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getCityBooking() {
        return cityBooking;
    }

    public void setCityBooking(String cityBooking) {
        this.cityBooking = cityBooking;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
