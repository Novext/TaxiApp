package com.novext.taxiapp;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerCount);
        ArrayAdapter<CharSequence> adapter_count = ArrayAdapter.createFromResource(this,R.array.count_array, android.R.layout.simple_spinner_item);
        adapter_count.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter_count);

        Spinner spinnerTime = (Spinner) findViewById(R.id.spinnerTime);
        ArrayAdapter<CharSequence> adapter_Time = ArrayAdapter.createFromResource(this,R.array.time_array, android.R.layout.simple_spinner_item);
        adapter_Time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(adapter_Time);

    }

}
