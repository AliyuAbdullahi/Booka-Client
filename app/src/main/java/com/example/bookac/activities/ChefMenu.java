package com.example.bookac.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookac.R;
import com.example.bookac.fragments.ActionBarDialog;
import com.example.bookac.fragments.NavigationFragment;
import com.example.bookac.fragments.RatingDialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.squareup.picasso.Picasso;

public class ChefMenu extends AppCompatActivity implements OnMapReadyCallback {

  TextView chefNickName;
  TextView chefAddress;
  LinearLayout first;
  LinearLayout second;
  LinearLayout third;
  String chefAddrss;
  String uid;
  Bundle args = new Bundle();
  ImageView imageBillBoard;
  TextView addressDetails;
  TextView chefFullAddress;
  ImageView backgroundImage;
  ImageView callChef;
  String chefFirstName;
  String chefLastName;
  String chefNick;
  long phoneNumber;
  String chefAddressText;
  double cheflongitude;
  double chefLatitude;
  MapFragment mapFragment;
  GoogleMap googleMap;
  ImageView mapImage;
  ImageView menuForChef;
  private String chefUid;
  DrawerLayout layout;
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_chef_menu);
    Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
    setSupportActionBar (toolbar);

    getSupportActionBar ().setTitle ("");
    getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
    final Drawable upArrow = getResources ().getDrawable (R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    upArrow.setColorFilter (getResources ().getColor (android.R.color.white), PorterDuff.Mode.SRC_ATOP);
    getSupportActionBar ().setHomeAsUpIndicator (upArrow);
    getIntentContent ();
    setUpView ();

    //setup fab

    FloatingActionButton fab = (FloatingActionButton) findViewById (R.id.fab4);
    fab.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View view) {
        setUpIntent (args);
        ActionBarDialog dialog = new ActionBarDialog ();
        dialog.setArguments(args);
        dialog.show (getSupportFragmentManager (),
                "action_bar_frag");
        Snackbar.make (view, "Detail view for "+ chefAddrss , Snackbar.LENGTH_LONG)
                .setAction ("Action", null).show ();
      }
    });

    second.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        first.setBackgroundColor (Color.argb (5, 300, 200, 100));
        Intent moveToMenuActivity = new Intent (ChefMenu.this, com.example.bookac.Menu.class);
        moveToMenuActivity.putExtra ("uid", chefUid);
        startActivity (moveToMenuActivity);
        overridePendingTransition (R.anim.right_in, R.anim.left_out);
      }
    });

    third.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        RatingDialog dialog = new RatingDialog ();
        dialog.show (getFragmentManager (), "rating_bar_frag");
      }
    });

    first.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        Intent callIntent = new Intent (Intent.ACTION_CALL);
        callIntent.setData (Uri.parse ("tel:" + phoneNumber));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          if (checkSelfPermission (Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
          }
        }
        Toast.makeText (getApplicationContext (),"Calling " + chefNick, Toast.LENGTH_SHORT).show ();
        startActivity (callIntent);
      }
    });

  }

  @Override
  protected void onResume () {
    super.onResume ();
    getIntentContent ();
    setUpView ();
  }

  //cleanup toolbar
  public int getStatusBarHeight() {
    int result = 0;
    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater ().inflate (R.menu.chef_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    switch (item.getItemId()) {
      case R.id.share:
        String restaurantBodyString = "Visit us today at "+ chefAddrss+" for your meal and you will be glad you did!";
        shareIt ("Visit "+ chefNick+" Resataurant", restaurantBodyString );
        break;
      case android.R.id.home:
        onBackPressed ();
        overridePendingTransition (R.anim.left_in, R.anim.right_out);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public boolean isOnline() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
    if (info != null && info.isConnectedOrConnecting ()) {
      return true;
    } else {
      Toast.makeText (ChefMenu.this, "Check your internet connection", Toast.LENGTH_SHORT).show ();
      return false;
    }

  }

  public void setUpView(){
    getIntentContent ();
    first = (LinearLayout)findViewById (R.id.first);
    second = (LinearLayout)findViewById (R.id.second);
    third = (LinearLayout)findViewById (R.id.third);
    menuForChef = (ImageView)findViewById (R.id.menuForChef);
    chefNickName = (TextView)findViewById (R.id.chefNickName);
    chefAddress = (TextView)findViewById (R.id.addressOfChef);
    callChef = (ImageView)findViewById (R.id.callChef);
    imageBillBoard = (ImageView)findViewById (R.id.imageBillboard);
    imageBillBoard.setImageResource (R.drawable.food);
//    Glide.with (this).load(R.raw.mygif).asGif().into(imageBillBoard);
    addressDetails = (TextView)findViewById (R.id.addressdetails);
    chefNickName.setText (chefNick);
    chefAddress.setText (chefAddrss.split (",")[1] + " " + chefAddrss.split (",")[3]);
    addressDetails.setText (chefAddrss);

    mapImage = (ImageView)findViewById (R.id.map4);
    try {
      Picasso.with (ChefMenu.this).load ("http://maps.google.com/maps/api/staticmap?center="
              +chefLatitude + "," + cheflongitude + "&zoom=17&size=600x600&sensor=true")
              .error (R.drawable.logo).placeholder (R.drawable.logo)
              .into (mapImage);
    }catch (Exception e){
      e.printStackTrace ();
    }

  }

  public void getIntentContent(){
    Intent intent = getIntent ();
    chefUid = intent.getStringExtra ("uid");
    chefFirstName = intent.getStringExtra ("firstname");
    chefLastName = intent.getStringExtra ("lastname");
    chefNick = intent.getStringExtra ("nickname");
    chefAddrss = intent.getStringExtra ("address");
    phoneNumber = intent.getLongExtra ("phoneNumber", 0);
    cheflongitude = intent.getDoubleExtra ("longitude", 0);
    chefLatitude = intent.getDoubleExtra ("latitude",0);
  }

  @Override
  public void onMapReady (GoogleMap googleMap) {

  }

  private void shareIt(String header, String shareBody) {
    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    sharingIntent.putExtra (android.content.Intent.EXTRA_SUBJECT, header);
    sharingIntent.putExtra (android.content.Intent.EXTRA_TEXT, shareBody);
    startActivity (Intent.createChooser (sharingIntent, "Share via"));
  }

  public void setUpIntent( Bundle args){
    args.putString ("title", chefAddrss);
    args.putDouble ("longitude", cheflongitude);
    args.putDouble ("latitude", chefLatitude);
  }

  public void getIntentObject(){

  }

  @Override
  public void onBackPressed () {
    super.onBackPressed ();
  }
}





