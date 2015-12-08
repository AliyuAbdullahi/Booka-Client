package com.example.bookac.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.bookac.R;
import com.example.bookac.singletons.User;
import com.example.bookac.tools.PicassoImageLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.WeakHashMap;

public class About extends AppCompatActivity {
  WebView aboutText;
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    getWindow ().setFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_about);
    Toolbar toolbar = (Toolbar) findViewById (R.id.toolbarAbout);
    setSupportActionBar (toolbar);
    com.pkmmte.view.CircularImageView userImage = (com.pkmmte.view.CircularImageView)
            findViewById (R.id.myAvartar);
    PicassoImageLoader loader = new PicassoImageLoader (About.this);
    loader.loadImage (userImage, User.imageUrl);
    aboutText = (WebView)findViewById (R.id.abouttext);
    BufferedReader reader = null;
    try {
      reader =  new BufferedReader (new InputStreamReader (getAssets ().open ("about.txt")));
      StringBuilder builder = new StringBuilder ();
      String line;
      while((line = reader.readLine ()) != null){
        builder.append (line);
      }

               String text = "<html><body>"
                       + "<p align=\"justify\" style=\" font-size: 16px; color: #999\">"
                       + builder.toString ()
                       + "</p> "
                       + "</body></html>";

             aboutText.loadData (text, "text/html", "utf-8");
    } catch (IOException e) {
      e.printStackTrace ();
    }
    finally {
      if(reader != null){
        try {
          reader.close ();
        } catch (IOException e) {
          e.printStackTrace ();
        }
      }
    }
  }

}
