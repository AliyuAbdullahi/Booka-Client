package com.example.bookac;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookac.activities.UserHomePage;
import com.example.bookac.fragments.NavigationFragment;
import com.example.bookac.singletons.Chef;
import com.example.bookac.singletons.User;
import com.example.bookac.tools.PicassoImageLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.pkmmte.view.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import logger.Log;

public class GetPeopleAround extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, LocationListener,
        GoogleApiClient.OnConnectionFailedListener {
  private DrawerLayout mdrawerLayout;
  private ActionBarDrawerToggle toggle;
  NavigationFragment navigationFragment;
  private RelativeLayout goNow;
  TextView goToLocation;
  Bitmap image;
  double latitud;
  Bitmap myRoundedImage;
  double longitud;
  private Location mLastLocation;
  private String WAITING_FOR_LOCATION = "Waiting for Location";
  ImageView enterLocation;
  private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

  private GoogleApiClient mGoogleApiClient;
  private GoogleMap mMap;
  android.support.v7.widget.CardView cardView;
  TextView location;
  LinearLayout body;
  boolean visible = false;
  private Animation hide;
  private Animation show;
  int REQUEST_CODE = 1;
  Toolbar toolbar;
  com.pkmmte.view.CircularImageView userImage;

  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_get_people_around);
    PicassoImageLoader picassoImageLoader = new PicassoImageLoader (GetPeopleAround.this);
    mdrawerLayout = (DrawerLayout)findViewById (R.id.drawerLayout);

     navigationFragment = (NavigationFragment)getSupportFragmentManager ().findFragmentById (R.id.navigation_fragment);

    showGpsPlease ();
    if (checkPlayServices ()) {

      // Building the GoogleApi client
      buildGoogleApiClient ();
    }
    toolbar = (Toolbar) findViewById (R.id.toolbarpeople);
    setSupportActionBar (toolbar);



    getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
    userImage = (CircularImageView)
            findViewById (R.id.myAvartar);
    if(savedInstanceState != null){
      image = (Bitmap) savedInstanceState.getParcelable ("BitmapImage");
      userImage.setImageBitmap (image);
    }
    else {
      picassoImageLoader.loadImage (userImage, User.getImageUrl (GetPeopleAround.this, "Id"));
    }

    userImage.setDrawingCacheEnabled (true);
    myRoundedImage = userImage.getDrawingCache();

    navigationFragment.setUp (R.id.navigation_fragment, mdrawerLayout, toolbar);
    final ImageView clearText = (ImageView) findViewById (R.id.cancelImage);
    location = (TextView) findViewById (R.id.location);
    body = (LinearLayout) findViewById (R.id.body);
    enterLocation = (ImageView) findViewById (R.id.enter_one);
    goToLocation = (TextView) findViewById (R.id.go_to_location);
    goNow = (RelativeLayout) findViewById (R.id.go_now);
    enterLocation.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        if (location.getText () == null) {
          Toast.makeText (getApplicationContext (), "Search for a location", Toast.LENGTH_SHORT).show ();
        } else if (visible && isOnline ()) {
          Intent go = new Intent (GetPeopleAround.this, UserHomePage.class);
          setUpListView upListView = new setUpListView (GetPeopleAround.this, "http://mybukka.herokuapp.com/api/v1/bukka/chefs/"
                  + mMap.getCameraPosition ().target.latitude + "/" + mMap.getCameraPosition ().target.longitude);
          upListView.execute ();
          go.putExtra ("longitude", mMap.getCameraPosition ().target.longitude);
          go.putExtra ("latitude", mMap.getCameraPosition ().target.latitude);
          startActivity (go);
        }
      }
    });

    location.setOnTouchListener (new View.OnTouchListener () {
      @Override
      public boolean onTouch (View v, MotionEvent event) {
        switch (event.getAction ()) {
          case MotionEvent.ACTION_DOWN:
            if (isOnline () && location.getText () != null) {
              Intent getAddress = new Intent (GetPeopleAround.this, AutoComplete.class);
              startActivityForResult (getAddress, REQUEST_CODE);
            } else {
              Toast.makeText (getApplicationContext (), "Check your network connection",
                      Toast.LENGTH_SHORT).show ();
            }
        }
        return true;
      }
    });
    cardView = (CardView) findViewById (R.id.cardview);
    cardView.setVisibility (View.GONE);
    clearText.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        location.setText ("");
      }
    });


    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ()
            .findFragmentById (R.id.map);
    mapFragment.getMapAsync (this);
  }

  protected synchronized void buildGoogleApiClient () {
    mGoogleApiClient = new GoogleApiClient.Builder (this)
            .addConnectionCallbacks (this)
            .addOnConnectionFailedListener (this)
            .addApi (LocationServices.API).build ();
  }

  private boolean checkPlayServices () {
    int resultCode = GooglePlayServicesUtil
            .isGooglePlayServicesAvailable (this);
    if (resultCode != ConnectionResult.SUCCESS) {
      if (GooglePlayServicesUtil.isUserRecoverableError (resultCode)) {
        GooglePlayServicesUtil.getErrorDialog (resultCode, this,
                PLAY_SERVICES_RESOLUTION_REQUEST).show ();
      } else {
        Toast.makeText (getApplicationContext (),
                "This device is not supported.", Toast.LENGTH_LONG)
                .show ();
        finish ();
      }
      return false;
    }
    return true;
  }


  public int getStatusBarHeight() {
    int result = 0;
    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }


  @Override
  protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
      try {
        String result = data.getStringExtra ("result");
        double latitude = data.getDoubleExtra ("lat", 0);
        double longitude = data.getDoubleExtra ("lng", 0);
        LatLng newLatLng = new LatLng (latitude, longitude);
        location.setText (result);
        CameraPosition newPosition = CameraPosition.builder ().target (newLatLng)
                .zoom (15).build ();
        mMap.animateCamera (CameraUpdateFactory.newCameraPosition (newPosition), 2000, null);
      } catch (NullPointerException e) {

        e.printStackTrace ();
      } catch (Exception e) {
        e.printStackTrace ();
      }

    }
  }


  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady (GoogleMap googleMap) {
    initAnimation ();
    mMap = googleMap;
//    getMyLocation ();
    displayLocation (mMap);
    mMap.setTrafficEnabled (true);
    mMap.setMyLocationEnabled (true);
    //Add a marker in Sydney and move the camera
    LatLng sydney = new LatLng (-34, 151);
    mMap.setOnCameraChangeListener (new GoogleMap.OnCameraChangeListener () {

      @Override
      public void onCameraChange (CameraPosition cameraPosition) {
        UpdateMap map = new UpdateMap (GetPeopleAround.this, cameraPosition);

        if (isOnline ()) {
          map.execute ();
        } else {
          Toast.makeText (getApplicationContext (), "Network not available",
                  Toast.LENGTH_SHORT).show ();
        }
      }
    });

    mMap.setOnMapClickListener (new GoogleMap.OnMapClickListener () {
      @Override
      public void onMapClick (LatLng latLng) {
        if (!visible) {
          cardView.setVisibility (View.VISIBLE);
          cardView.startAnimation (show);
          enterLocation.setImageResource (R.drawable.getcheflocationactive);
          goToLocation.setText ("Find Chefs in " + location.getText ().toString ());
          CameraPosition newPosition = CameraPosition.builder ().target (latLng)
                  .zoom (15).build ();
          visible = true;
        } else {
          cardView.startAnimation (hide);
          cardView.setVisibility (View.GONE);
          goToLocation.setText (" --- ---");
          enterLocation.setImageResource (R.drawable.gotocheflocation);
          CameraPosition newPosition = CameraPosition.builder ().target (latLng)
                  .zoom (15).build ();
          visible = false;
        }
      }

    });
  }

  @Override
  public void onConnected (Bundle bundle) {
    displayLocation (mMap);
  }

  @Override
  public void onConnectionSuspended (int i) {
    mGoogleApiClient.connect ();
  }

  @Override
  public void onLocationChanged (Location location) {
    mMap.moveCamera (CameraUpdateFactory.newLatLng (new LatLng (6, 3)));
  }

  @Override
  public void onStatusChanged (String provider, int status, Bundle extras) {

  }

  @Override
  public void onProviderEnabled (String provider) {

  }

  @Override
  public void onProviderDisabled (String provider) {

  }

  private GoogleMap.OnMyLocationChangeListener myLocationChangeListener
          = new GoogleMap.OnMyLocationChangeListener () {
    @Override
    public void onMyLocationChange (Location location) {
      LatLng loc = new LatLng (location.getLatitude (), location.getLongitude ());
      if (mMap != null) {
        mMap.animateCamera (CameraUpdateFactory.newLatLngZoom (loc, 16.0f));
      }
    }
  };

  public boolean isOnline () {
    ConnectivityManager connectivityManager = (ConnectivityManager)
            getSystemService (Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectivityManager.getActiveNetworkInfo ();
    if (info != null && info.isConnectedOrConnecting ()) {
      return true;
    } else {
      Toast.makeText (getApplicationContext (), "Check your internet connection",
              Toast.LENGTH_SHORT).show ();
      return false;
    }

  }

  @Override
  public void onConnectionFailed (ConnectionResult connectionResult) {
    Log.i ("ERROR_TYPE=", "Connection failed: ConnectionResult.getErrorCode() = "
            + connectionResult.getErrorCode ());

  }

  public class UpdateMap extends AsyncTask<Void, Void, Void> {
    Context context;
    CameraPosition position;
    List<Address> addresses;

    public UpdateMap (Context context, CameraPosition position) {
      this.context = context;
      this.position = position;
    }

    @Override
    protected Void doInBackground (Void... params) {
      try {
        Geocoder geo = new Geocoder (GetPeopleAround.this.getApplicationContext (),
                Locale.getDefault ());
        addresses = geo.getFromLocation (position.target.latitude, position.target.longitude, 1);
      } catch (Exception e) {
        e.printStackTrace (); // getFromLocation() may sometimes fail
      }
      return null;
    }

    @Override
    protected void onPostExecute (Void aVoid) {
      super.onPostExecute (aVoid);
      try {
        if (addresses.isEmpty ()) {
          location.setText (WAITING_FOR_LOCATION);
        } else {
          if (addresses.size () > 0) {
            location.setText (addresses.get (0).getFeatureName () + ", " +
                    addresses.get (0).getAdminArea ()
                    + ", " + addresses.get (0).getCountryName ());
            if (visible && isOnline () && !(location.getText ().toString ().equalsIgnoreCase (WAITING_FOR_LOCATION))) {
              goToLocation.setText ("Find Chefs in " + addresses.get (0).getFeatureName () + ", " + addresses.get (0).getAdminArea ());
              enterLocation.setImageResource (R.drawable.getcheflocationactive);
            } else {
              goToLocation.setText ("Tap map to activate");
              enterLocation.setImageResource (R.drawable.gotocheflocation);
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace ();
      }
    }
  }

  public void initAnimation () {
    hide = AnimationUtils.loadAnimation (GetPeopleAround.this, R.anim.fadeout);
    show = AnimationUtils.loadAnimation (GetPeopleAround.this, R.anim.fadein);
  }

  public void showToast (String message) {
    Toast.makeText (getApplicationContext (), message, Toast.LENGTH_LONG).show ();
  }

  private void displayLocation (GoogleMap map) {

    mLastLocation = LocationServices.FusedLocationApi
            .getLastLocation (mGoogleApiClient);

    if (mLastLocation != null) {
      double latitude = mLastLocation.getLatitude ();
      double longitude = mLastLocation.getLongitude ();
      LatLng loc = new LatLng (latitude, longitude);
      if (mMap != null) {
        mMap.animateCamera (CameraUpdateFactory.newLatLngZoom (loc, 16.0f));
      }
      //lblLocation.setText(latitude + ", " + longitude);

    } else {
      Toast.makeText (getApplicationContext (), "Map getting ready...", Toast.LENGTH_SHORT).show ();
    }
  }

  @Override
  protected void onStart () {
    super.onStart ();
    if (mGoogleApiClient != null) {
      mGoogleApiClient.connect ();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable ("BitmapImage", myRoundedImage);
  }

  @Override
  protected void onResume () {
    super.onResume ();
    PicassoImageLoader loader = new PicassoImageLoader (GetPeopleAround.this);
    loader.loadImage (userImage, User.imageUrl );
    Toast.makeText (getApplicationContext (), User.imageUrl, Toast.LENGTH_SHORT).show ();
    checkPlayServices ();
  }

  @Override
  public void onActivityReenter (int resultCode, Intent data) {
    super.onActivityReenter (resultCode, data);
    PicassoImageLoader loader = new PicassoImageLoader (GetPeopleAround.this);
    loader.loadImage (userImage, User.imageUrl);
  }

  public class setUpListView extends AsyncTask<Void, Void, Void> {
    Context context;
    String url;

    public setUpListView (Context context, String url) {
      this.context = context;
      this.url = url;
    }

    @Override
    protected Void doInBackground (Void... params) {
      RequestQueue que = Volley.newRequestQueue (GetPeopleAround.this);
      final StringRequest request = new StringRequest (Request.Method.GET, url, new Response.Listener<String> () {
        @Override
        public void onResponse (String response) {
          DecimalFormat decimalFormat = new DecimalFormat ("#");
          decimalFormat.setMaximumFractionDigits (0);
          android.util.Log.e ("", response);
          try {
            JSONArray chefArray = new JSONArray (response);
            for (int i = 0; i < chefArray.length (); i++) {
              JSONObject currentChef = chefArray.getJSONObject (i);
              Chef chef = new Chef ();
              chef.address = currentChef.getString ("address");
              chef.firstname = currentChef.getString ("first_name");
              chef.lastname = currentChef.getString ("last_name");
              chef.nickName = currentChef.getString ("username");
              chef.phoneNumber = Long.parseLong ((decimalFormat.format (Double.parseDouble (currentChef.getString ("phone_number")))));
              JSONObject coord = currentChef.getJSONObject ("coords");
              chef.longitude = Double.parseDouble (coord.getString ("lng"));
              chef.latitude = Double.parseDouble (coord.getString ("lat"));
              if (currentChef.getString ("profile_photo") != null) {
                chef.profilePhoto = currentChef.getString ("profile_photo");
              }
//                User.myChef.add (chef);
            }
          } catch (JSONException e) {
            e.printStackTrace ();
          }

        }
      }, new Response.ErrorListener () {
        @Override
        public void onErrorResponse (VolleyError error) {
          android.util.Log.e ("", error.toString ());
        }
      });
      int socketTimeout = 30000;//30 seconds - change to what you want
      RetryPolicy policy = new DefaultRetryPolicy (socketTimeout, DefaultRetryPolicy
              .DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
      request.setRetryPolicy (policy);
      request.setShouldCache (true);

      que.add (request);


      return null;
    }

    @Override
    protected void onPostExecute (Void aVoid) {

      super.onPostExecute (aVoid);

    }
  }

  public void getMyLocation () {
    LocationManager locationManager = (LocationManager)
            getSystemService (Context.LOCATION_SERVICE);
    Criteria criteria = new Criteria ();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (checkSelfPermission (Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission (Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    Location location = locationManager.getLastKnownLocation (locationManager
            .getBestProvider (criteria, false));
    latitud = location.getLatitude ();
    longitud = location.getLongitude ();
  }

  public void turnGPSOn () {
    String provider = Settings.Secure.getString (getContentResolver (), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

    if (!provider.contains ("gps")) { //if gps is disabled
      final Intent poke = new Intent ();
      poke.setClassName ("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
      poke.addCategory (Intent.CATEGORY_ALTERNATIVE);
      poke.setData (Uri.parse ("3"));
      sendBroadcast (poke);
    }
  }

  public boolean isGPSEnabled (Context mContext){
    LocationManager locationManager = (LocationManager)
            mContext.getSystemService(Context.LOCATION_SERVICE);
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }


  private void showGPSDisabledAlertToUser(){
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
            .setCancelable(false)
            .setPositiveButton("Enable GPS in location",
                    new DialogInterface.OnClickListener(){
                      public void onClick(DialogInterface dialog, int id){
                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                      }
                    });
    alertDialogBuilder.setNegativeButton("Cancel",
            new DialogInterface.OnClickListener(){
              public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
              }
            });
    AlertDialog alert = alertDialogBuilder.create();
    alert.show();
  }
  public void showGpsPlease(){
    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
      Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
    }else{
      showGPSDisabledAlertToUser();
    }
  }
}
