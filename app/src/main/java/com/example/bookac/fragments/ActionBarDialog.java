package com.example.bookac.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.example.bookac.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by aliyuolalekan on 10/21/15.
 */
public class ActionBarDialog extends DialogFragment {
  GoogleMap mapFragment;
  static LatLng POSITION;
  static final LatLng KIEL = new LatLng(53.551, 9.993);
  private GoogleMap map;
  double lng,lat;
  String title;
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    Bundle args = getArguments ();
    lng = args.getDouble ("longitude");
    lat = args.getDouble ("latitude");
    title = args.getString ("title");
    POSITION = new LatLng (lat, lng);
    View v = inflater.inflate(R.layout.action_bar_dialog, container, false);
    Toolbar toolbar = (Toolbar) v.findViewById(R.id.my_toolbar);
    toolbar.setTitle (title);
    toolbar.setBackgroundColor (Color.BLUE);
    mapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mapdialogOne))
            .getMap();
    // Move the camera instantly to hamburg with a zoom of 15.
    mapFragment.moveCamera (CameraUpdateFactory.newLatLngZoom (POSITION, 15));

    // Zoom in, animating the camera.
    mapFragment.animateCamera(CameraUpdateFactory.zoomTo(10),500, null);

    return v;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    // request a window without the title
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    return dialog;
  }
  public void onDestroyView() {
    super.onDestroyView();
    FragmentManager fm = getActivity().getSupportFragmentManager();
    Fragment fragment = (fm.findFragmentById(R.id.mapdialogOne));
    FragmentTransaction ft = fm.beginTransaction ();
    ft.remove(fragment);
    ft.commit ();
  }
}