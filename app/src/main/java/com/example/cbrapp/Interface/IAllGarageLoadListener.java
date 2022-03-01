package com.example.cbrapp.Interface;


import java.util.List;

public interface IAllGarageLoadListener {
    void onAllGarageLoadSuccess(List<String> areaNameList);
    void onAllGarageLoadFailed(String message);
}
