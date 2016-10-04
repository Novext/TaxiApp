package com.novext.taxiapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.w3c.dom.Text;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    //ConnectionCallbacks
    public static boolean mMapIsTouched = false;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "210201";
    IconGenerator iconFactory;


    GoogleMap mMap;
    supportMapFragment mapFragment;
    GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;

    OkHttpRequest okHttpRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        mapFragment = supportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        okHttpRequest = App.getInstanceOkHttpRequest();
    }
    public void sendMessage(View view)
    {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    public void getAllStopsAvailable(){

        new AsyncTask<Void,Void, Response>(){
            @Override
            protected Response doInBackground(Void... params) {
                return okHttpRequest.get("/stops");
            }

            @Override
            protected void onPostExecute(Response res) {
                if(res!=null){
                    if(res.code()==200){
                        try{

                            JSONArray values = new JSONArray(res.body().string());
                            for (int i = 0; i < values.length(); i++) {
                                LatLng latlng = new LatLng(values.getJSONObject(i).getDouble("latitude"),
                                        values.getJSONObject(i).getDouble("longitude"));
                                setMarker(latlng,values.getJSONObject(i).getString("_id"),values.getJSONObject(i).getString("description"),
                                        values.getJSONObject(i).getInt("minutes"),values.getJSONObject(i).getInt("seconds"));
                            }
                        }catch (Exception e){
                            Log.e("Exception",e.toString());
                        }

                    }
                }

            }
        }.execute(null,null,null);
    }

    public void setMarker(LatLng latLng,String _id,String description,int minutes,int seconds){
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .snippet(_id)
                .title(description)
                .anchor(iconFactory.getAnchorU(),iconFactory.getAnchorV())
                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(minutes + " : " + seconds))));

//        stopsList.add(marker);
        timer(marker,minutes,seconds);
    }

    public void timer(Marker marker,int minutes,int seconds){
        Timeout timeout = new Timeout(minutes,seconds,this,marker);
        timeout.start(0,1000);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                return true;
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
        getAllStopsAvailable();
    }

    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;

        } else {
            return true;
        }

    }



    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permissions denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    protected void onStart() {
        mGoogleApiClient.connect();
        checkLocationPermission();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
