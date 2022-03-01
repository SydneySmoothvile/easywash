package com.example.cbrapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Attendant implements Parcelable {
    private String name, username, password, attendantId;
    private long rating;

    public Attendant() {
    }

    protected Attendant(Parcel in) {
        name = in.readString();
        username = in.readString();
        password = in.readString();
        attendantId = in.readString();
        rating = in.readLong();
    }

    public static final Creator<Attendant> CREATOR = new Creator<Attendant>() {
        @Override
        public Attendant createFromParcel(Parcel in) {
            return new Attendant(in);
        }

        @Override
        public Attendant[] newArray(int size) {
            return new Attendant[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public String getAttendantId() {
        return attendantId;
    }

    public void setAttendantId(String attendantId) {
        this.attendantId = attendantId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(attendantId);
        parcel.writeLong(rating);
    }
}
