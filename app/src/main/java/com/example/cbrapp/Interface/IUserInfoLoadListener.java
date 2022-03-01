package com.example.cbrapp.Interface;

import com.example.cbrapp.Model.User;

public interface IUserInfoLoadListener {
    void onUserInfoLoadEmpty();
    void onUserInfoLoadSuccess(User user);
    void onUserInfoLoadFail();
}
