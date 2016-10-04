package com.novext.taxiapp;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

import static com.google.android.gms.maps.GoogleMap.*;

public class MapActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
                                                                LocationListener,
                                                                OnCameraChangeListener,
                                                                OnMapReadyCallback,
                                                                OnMarkerDragListener,
                                                                GoogleApiClient.ConnectionCallbacks {

    LatLng centerOfMap;
    Marker myLocationMarker;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    public String minutes;
    OkHttpRequest okHttpRequest;
    double longitude;
    double latitude;
    public String description;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private Location mLastLocation;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapParade);

        mapFragment.getMapAsync(this);
        okHttpRequest = App.getInstanceOkHttpRequest();

        final Spinner spinnerTime = (Spinner) findViewById(R.id.spinnerTime);
        ArrayAdapter<CharSequence> adapter_Time = ArrayAdapter.createFromResource(this, R.array.time_array, android.R.layout.simple_spinner_item);
        adapter_Time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(adapter_Time);

        final EditText edtDescription = (EditText) findViewById(R.id.edtAddress);


        Button btnStop = (Button) findViewById(R.id.btnStop);

        btnStop.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                minutes = spinnerTime.getSelectedItem().toString();
                description = edtDescription.getText().toString();
                postLocationCenter(latitude, longitude, minutes, description);
            }
        });

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        centerOfMap = mMap.getCameraPosition().target;
        latitude = centerOfMap.latitude;
        longitude = centerOfMap.longitude;
        Log.d("GET CENTER LOCATIO", String.valueOf(latitude));
        Log.d("GET CENTER LOCATIO", String.valueOf(longitude));


    }

    public void postLocationCenter(final double latitude,
                                   final double longitude,
                                   final String minutes,
                                   final String description) {


        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Creating stop");
        progress.setMessage("Wait a second ...");
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();

        new AsyncTask<String, Void, Response>() {

            @Override
            protected Response doInBackground(String... params) {
                JSONObject data = new JSONObject();
                try {
                    data.put("latitude", latitude);
                    data.put("longitude", longitude);
                    data.put("description", description);
                    data.put("minutes", minutes);
                    data.put("taxiId", State.getUserId());


                    Log.d("TODOS LOS DATOS", String.valueOf(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return okHttpRequest.post(String.valueOf(data), "/stops");
            }

            protected void onPostExecute(Response response) {
                progress.dismiss();
                if (response != null) {
                    if (response.code() == 200) {
                        try {

                            Intent intent = new Intent(MapActivity.this, StopActivity.class);
                            startActivity(intent);

                        } catch (Exception e) {

                        }
                    } else if (response.code() == 401) {
                        Toast.makeText(MapActivity.this, "Already exist a stop, cancel before this", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }.execute(null, null, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        //double  latitud = -17.9750154;
        //double  longitud = -70.2351666;

        if (mMap != null) {

            mMap.setOnMarkerDragListener(this);
            mMap.setOnCameraChangeListener(this);
            adjustZoomMap(0.0, 0.0, 15);

            /*
            if (mMap.setMyLocationEnabled(true) != null)
                adjustZoomMap(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude(), 15);*/
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.stopAnimation();
        }
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
    }

    public void adjustZoomMap(Double latitude, Double longitude, int zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(zoom).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void addMyPositionMarker(Double latitude, Double longitude) {

        myLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Mi posicion")
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)));
        //Actualiza la poscicion del pasajero con respecto al marcador
        //presenter.setMyCurrentLocation(myLocationMarker.getPosition().latitude, myLocationMarker.getPosition().longitude);
        Log.i("LocationMarkerCurrent", myLocationMarker.getPosition().latitude + " " + myLocationMarker.getPosition().longitude);

    }

    public void locationMe() {
        if (mLastLocation != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15);
            mMap.animateCamera(cameraUpdate);
            //addMarkerUser(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
        }

    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override

    public void onLocationChanged(Location location) {
        mLastLocation = location;

        if(!flag){
            locationMe();
            flag = true;
        }

    }
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

        }
        createLocationRequest();
        startLocationUpdates();
    }
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    @Override
    protected void onStop() {
        if(mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}