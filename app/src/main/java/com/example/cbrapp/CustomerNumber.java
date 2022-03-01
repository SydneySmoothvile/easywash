package com.example.cbrapp;

public class CustomerNumber {
    String s;
    private static final CustomerNumber ourInstance = new CustomerNumber();
    public static CustomerNumber getInstance() {
        return ourInstance;
    }
    private CustomerNumber() {
    }
    public void setData(String s) {
        this.s = s;
    }
    public String getData() {
        return s;
    }
}
