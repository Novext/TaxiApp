package com.novext.taxiapp;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

import static com.google.android.gms.maps.GoogleMap.*;

public class MapActivity extends AppCompatActivity implements  OnCameraChangeListener,OnMapReadyCallback,OnMarkerDragListener {

    LatLng centerOfMap;
    Marker myLocationMarker;
    supportMapFragment mapFragment;
    GoogleMap mMap;
    public String minutes;
    OkHttpRequest okHttpRequest;

    public String description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapFragment = supportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mapParade, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
        okHttpRequest = App.postInstanceOkHttpRequest();

        Spinner spinnerTime = (Spinner) findViewById(R.id.spinnerTime);
        ArrayAdapter<CharSequence> adapter_Time = ArrayAdapter.createFromResource(this, R.array.time_array, android.R.layout.simple_spinner_item);
        adapter_Time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(adapter_Time);

        EditText edtDescription = (EditText) findViewById(R.id.edtAddress);

        minutes = spinnerTime.getSelectedItem().toString();
        description = edtDescription.getText().toString();
    }
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        centerOfMap = mMap.getCameraPosition().target;
        double latitude = centerOfMap.latitude;
        double longitude = centerOfMap.longitude;
        Log.d("GET CENTER LOCATIO",String.valueOf(latitude));
        Log.d("GET CENTER LOCATIO",String.valueOf(longitude));

        postLocationCenter(latitude,longitude,minutes,description);

    }
    public void postLocationCenter(final double latitude,
                                   final double longitude,
                                   final String minutes,
                                   final String description){

        new AsyncTask<String,Void, Response>(){

            @Override
            protected Response doInBackground(String... params) {
                JSONObject data = new JSONObject();
                try {
                    data.put("latitude",latitude);
                    data.put("longitude",longitude);
                    data.put("description",description);
                    data.put("minutes",minutes);


                    Log.d("TODOS LOS DATOS", String.valueOf(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return okHttpRequest.post(String.valueOf(data),"/stops");
            }
            protected  void onPostExecute(Response response){
                if (response!=null){
                    if (response.code()==200){
                        try{
                            JSONObject data = new JSONObject(response.body().string());

                            Log.d("RETURN ID", data.getJSONObject("taxi_ID").getString(("id_")));
                        }catch (Exception e){

                        }
                    }
                }
            }
        }.execute(null,null,null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //double  latitud = -17.9750154;
        //double  longitud = -70.2351666;
        if (mMap!=null){

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
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}