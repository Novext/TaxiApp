package com.novext.taxiapp;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LoginActivity extends AppCompatActivity{


    Button btnLogin;
    EditText editEmail;
    EditText editPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail    = (EditText) findViewById(R.id.edtEmail);
        editPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin     = (Button) findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (  ( !editEmail.getText().toString().equals("")) && ( !editPassword.getText().toString().equals("")) ){
                    //NetAsync(view);
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



}





