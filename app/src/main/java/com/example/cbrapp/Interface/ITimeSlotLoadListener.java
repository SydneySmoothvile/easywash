package com.example.cbrapp.Interface;

import com.example.cbrapp.Model.TimeSlot;

import java.util.List;

public interface ITimeSlotLoadListener {

    void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList);
    void onTimeSlotLoadFailure(String message);
    void onTimeSlotLoadEmpty();
}
