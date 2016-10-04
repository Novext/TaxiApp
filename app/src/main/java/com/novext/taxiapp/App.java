package com.novext.taxiapp;

import android.app.Application;

/**
 * Created by JULIO on 3/10/2016.
 */

public class App extends Application {

    private static OkHttpRequest okHttpRequest;
    private static App app;
    @Override
    public void onCreate() {
        super.onCreate();
        okHttpRequest = new OkHttpRequest("https://taxerapi.herokuapp.com/");
        app = this;


    }

    public static App getInstance(){
        return app;
    }

    public static OkHttpRequest getInstanceOkHttpRequest(){
        return okHttpRequest;
    }

}
