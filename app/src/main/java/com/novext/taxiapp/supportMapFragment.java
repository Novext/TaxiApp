package com.novext.taxiapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by JULIO on 30/09/2016.
 */

public class supportMapFragment extends SupportMapFragment{
        public View view;
        //public TouchableWrapper mTouchView;
        public supportMapFragment(){

        }

        public static supportMapFragment newInstance(){
            return new supportMapFragment();
        }

    public View onCreateView(Bundle savedInstanceState,ViewGroup container,LayoutInflater inflater) {
            view  = super.onCreateView(inflater,container, savedInstanceState);
            return view;

        /*
            mTouchView = new TouchableWrapper(getActivity());
            mTouchView.addView(view);
            return view;
        */
        }
    /*
    public View getView(){
        return view;
    }
    */


}
