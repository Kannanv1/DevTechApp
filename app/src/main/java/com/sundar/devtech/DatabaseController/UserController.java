package com.sundar.devtech.DatabaseController;

import android.content.Context;

public class UserController {
    private Context context;

    public UserController(Context context){
        this.context = context;
    }

    public String loginUsers(String userName, String password){
        if (userName.equals("admin") && password.equals("admin123")){
            return "Login Successfully";
        }else {
            return "Login Failed";
        }

    }
}
