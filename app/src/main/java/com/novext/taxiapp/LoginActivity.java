package com.novext.taxiapp;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LoginActivity extends AppCompatActivity{


    Button btnLogin;
    EditText editEmail;
    EditText editPassword;
    OkHttpRequest okHttpRequest;
    String emailSend,passwordSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail    = (EditText) findViewById(R.id.edtEmail);
        editPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin     = (Button) findViewById(R.id.btnLogin);

        okHttpRequest = App.postInstanceOkHttpRequest();
        emailSend = editEmail.getText().toString();
        passwordSend = editPassword.getText().toString();




        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (  ( !editEmail.getText().toString().equals("")) && ( !editPassword.getText().toString().equals("")) ){
                    SignIn(emailSend,passwordSend);
                }
                else if ( ( !editEmail.getText().toString().equals("")) ){
                    Toast.makeText(getApplicationContext(),"Password field empty", Toast.LENGTH_SHORT).show();
                }
                else if ( ( !editPassword.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),"Email field empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Email and Password field are empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    public void SignIn(final String emailSend, final String passwordSend){
        new AsyncTask<String,Void, Response>(){

            @Override
            protected Response doInBackground(String... params) {
                //String data = null;
                JSONObject data = new JSONObject();
                try {
                    data.put("email",emailSend);
                    data.put("password",passwordSend);

                    Log.d("TODOS LOS DATOS", String.valueOf(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return okHttpRequest.post(String.valueOf(data),"/taxi/login");
            }

            protected void onPostExecute(Response response){
                if (response!=null){
                    if (response.code() == 200){
                        try {
                            JSONObject data = new JSONObject(response.body().string());
                            Log.d("email",data.getString("email"));
                            Log.d("password",data.getString("password"));
                            Log.d("DATA COMPLETO", String.valueOf(data));

                            Intent intent  = new Intent (LoginActivity.this,MainActivity.class );
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }.execute(null,null,null);
    }




}





