package com.example.cbrapp.Model;

public class User {
    private String name, mobile_number, car_number_plate, email;

    public User() {
    }

    public User(String name, String mobile_number, String car_number_plate, String email) {
        this.name = name;
        this.mobile_number = mobile_number;
        this.car_number_plate = car_number_plate;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getCar_number_plate() {
        return car_number_plate;
    }

    public void setCar_number_plate(String car_number_plate) {
        this.car_number_plate = car_number_plate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
