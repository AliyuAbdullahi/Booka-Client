package com.example.bookac.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RatingBar;

import com.example.bookac.R;

/**
 * Created by aliyuolalekan on 27/10/2015.
 */
public class RatingDialog extends DialogFragment {

  private static final String RATIING = "Rating";
  RatingBar ratingBar;

  @Nullable
  @Override
  public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate (R.layout.rating_dialog, container, false);
    Toolbar toolbar = (Toolbar) view.findViewById(R.id.my_toolbar_rating);
    toolbar.setTitle (RATIING);
    toolbar.setBackgroundColor (Color.rgb (156,49,43));
    ratingBar = (RatingBar)view.findViewById (R.id.rating_bar);
    ratingBar.setOnRatingBarChangeListener (new RatingBar.OnRatingBarChangeListener () {
      @Override
      public void onRatingChanged (RatingBar ratingBar, float rating, boolean fromUser) {

      }
    });
    return view;
  }

  //  public void onDestroyView() {
//    super.onDestroyView();
//    FragmentManager fm = getActivity().getSupportFragmentManager();
//    Fragment fragment = (fm.findFragmentById(R.id.mapdialogOne));
//    FragmentTransaction ft = fm.beginTransaction ();
//    ft.remove(fragment);
//    ft.commit ();
//  }
  @Override
  public Dialog onCreateDialog (Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog (savedInstanceState);
    // request a window without the title
    dialog.getWindow ().requestFeature (Window.FEATURE_NO_TITLE);
    return dialog;
  }
}
