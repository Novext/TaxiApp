package com.novext.taxiapp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by JULIO on 4/10/2016.
 */

public class State {


    static SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
    static SharedPreferences.Editor edit = prefs.edit();

    public static void setLogin(boolean state){
        edit.putBoolean("login",state);
        edit.commit();
    }

    public static boolean logged(){
        return prefs.getBoolean("login",false);
    }

    public static void setUserId(String userId){
        edit.putString("userId",userId);
        edit.commit();
    }

    public static String getUserId(){
        return prefs.getString("userId","");
    }

    public static String getStopId(){
        return prefs.getString("stopId","");
    }

    public static void setStopId(String stopId){
        edit.putString("stopId",stopId);
        edit.commit();
    }
}
