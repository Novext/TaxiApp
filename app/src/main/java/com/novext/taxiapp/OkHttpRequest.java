package com.novext.taxiapp;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by JULIO on 2/10/2016.
 */

public class OkHttpRequest {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    RequestBody body;
    Request request;
    Response response;
    String url;
    OkHttpClient client = new OkHttpClient();

    public OkHttpRequest(String _url){
        url = _url;
    }

    public Response get(String path){
        String uri = url + path;
        try{
            request = new Request.Builder()
                    .url(uri)
                    .get()
                    .build();
            return client.newCall(request).execute();
        }catch (IOException e){

        }
        return null;
    }

    public Response post(String values,String path){

        String uri = url + path;

        try {
            body = RequestBody.create(JSON, values);
            request = new Request.Builder()
                    .url(uri)
                    .post(body)
                    .build();
            return client.newCall(request).execute();
        }catch (IOException e){
            Log.e("[Connection Error]",e.toString());
        }
        return null;
    }

    public Response put(String values,String path){
        String uri = url + path;

        try {
            body = RequestBody.create(JSON, values);
            request = new Request.Builder()
                    .url(uri)
                    .put(body)
                    .build();
            return client.newCall(request).execute();
        }catch (IOException e){
            Log.e("[Connection Error]",e.toString());
        }
        return null;
    }
}
