package com.example.cbrapp.Interface;

import com.example.cbrapp.Model.Garage;

import java.util.List;

public interface IBranchLoadListener {
    void onBranchLoadSuccess(List<Garage> garageList);
    void onBranchLoadFailed(String message);
}
