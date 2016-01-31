package com.example.bookac.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.bookac.R;

public class SplashScreen extends AppCompatActivity {

  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_splash_screen);
    ImageView mImageViewFilling = (ImageView) findViewById(R.id.my_animation);
    ((AnimationDrawable) mImageViewFilling.getBackground()).start ();
    DelayAndGoToNextScreen task = new DelayAndGoToNextScreen ();
    task.execute ();

  }

  private class DelayAndGoToNextScreen extends AsyncTask<Void, Void, Void>{

    @Override
    protected Void doInBackground (Void... params) {
      try {
        Thread.sleep (5000);
      } catch (InterruptedException e) {
        e.printStackTrace ();
      }
      return null;
    }

    @Override
    protected void onPostExecute (Void aVoid) {
      Intent intent = new Intent (SplashScreen.this, GoogleFacebook.class);
      startActivity (intent);
    }
  }

}
