package com.example.cbrapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Garage implements Parcelable {
    private String name,address,website,phone,openHours,garageId;

    public Garage() {
    }

    protected Garage(Parcel in) {
        name = in.readString();
        address = in.readString();
        website = in.readString();
        phone = in.readString();
        openHours = in.readString();
        garageId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(website);
        dest.writeString(phone);
        dest.writeString(openHours);
        dest.writeString(garageId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Garage> CREATOR = new Creator<Garage>() {
        @Override
        public Garage createFromParcel(Parcel in) {
            return new Garage(in);
        }

        @Override
        public Garage[] newArray(int size) {
            return new Garage[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    public String getGarageId() {
        return garageId;
    }

    public void setGarageId(String garageId) {
        this.garageId = garageId;
    }
}
