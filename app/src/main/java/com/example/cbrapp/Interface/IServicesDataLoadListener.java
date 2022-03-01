package com.example.cbrapp.Interface;

import com.example.cbrapp.Model.ServicesItem;

import java.util.List;

public interface IServicesDataLoadListener {
    void onServicesDataLoadSuccess(List<ServicesItem> servicesItemList);
    void onServicesDataLoadFailure(String message);

}
