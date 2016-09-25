package com.novext.taxiapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {

    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    CallbackManager callbackManager;
    LoginButton loginButton;
    SignInButton btnLoginGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginFacebook();
        LoginGoggle();
    }
    public void LoginFacebook(){

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();
                final JSONObject obj = new JSONObject();
                final OkHttp okHttp = new OkHttp();
                final String url  = "https://dietapplication.herokuapp.com/api/users/social";

//              Toast.makeText(LoginActivity.this, loginResult.getAccessToken().getUserId(), Toast.LENGTH_SHORT).show();
//              Toast.makeText(LoginActivity.this, loginResult.getAccessToken().getToken(), Toast.LENGTH_SHORT).show();


                GraphRequestAsyncTask request_faccebook = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {


                        try {
                            obj.put("firstname",user.optString("name"));
                            obj.put("lastname",user.optString("email"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).executeAsync();
            }
            @Override
            public void onCancel() {

            }
            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    public void LoginGoggle(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        btnLoginGoogle = (SignInButton) findViewById(R.id.sign_in_button);
        btnLoginGoogle.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        callbackManager = CallbackManager.Factory.create();

        String txtGoogle = "Google";
        setGooglePlusButtonText(btnLoginGoogle,txtGoogle);
    }

    protected void setGooglePlusButtonText(SignInButton signInButton,String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(15);
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setText(buttonText);
                return;
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            startActivity(new Intent(this, MainActivity.class));
        }

        callbackManager.onActivityResult(requestCode,resultCode,data);

    }

    private void handleSignInResult(GoogleSignInResult result) {
        final String MY_PREFS_NAME = "MyPrefsFile";
        JSONObject obj = new JSONObject();
        OkHttp okHttp = new OkHttp();
        String url = "https://dietapplication.herokuapp.com/api/users/social";

        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            if (account!=null){
                try {
                    Toast.makeText(LoginActivity.this, account.getDisplayName(), Toast.LENGTH_SHORT).show();
                    obj.put("firstname",account.getDisplayName());
                    obj.put("email",account.getEmail());

                    SharedPreferences.Editor editor_profile = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor_profile.putString("firstname",account.getDisplayName());
                    editor_profile.putString("email",account.getEmail());
                    editor_profile.commit();
                    okHttp.post(url,obj);

                } catch (JSONException e) { e.printStackTrace();
                } catch (IOException e) { e.printStackTrace();
                }
            }
        } else{

        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }




}
