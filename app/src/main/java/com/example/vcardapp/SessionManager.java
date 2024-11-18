package com.example.vcardapp;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.vcardapp.User;

public class SessionManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String SESSION_KEY = "session_user";

    public SessionManager(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(Integer userID){
        //save session of user whenever user is logged in
        editor.putInt(SESSION_KEY, userID).commit();
    }

    public Integer getSession(){
        //return user id whose session is saved
        return sharedPreferences.getInt(SESSION_KEY, -1);
    }

    public void removeSession(){
        editor.putInt(SESSION_KEY, -1).commit();
    }
}