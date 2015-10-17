package com.example.bookac;

import android.content.Context;
import android.content.Intent;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookac.activities.UserHomePage;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import logger.Log;

public class GetPeopleAround extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, LocationListener,
        GoogleApiClient.OnConnectionFailedListener {
  private RelativeLayout goNow;
  TextView goToLocation;
  ProgressBar waitingForLocation;
  private Location mLastLocation;
  private String WAITING_FOR_LOCATION = "Waiting for Location";
  ImageView enterLocation;
  private LocationRequest mLocationRequest;
  private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
  private static int UPDATE_INTERVAL = 10000; // 10 sec
  private static int FATEST_INTERVAL = 5000; // 5 sec
  private static int DISPLACEMENT = 10; // 10 meters

  private GoogleApiClient mGoogleApiClient;
  private GoogleMap mMap;
  android.support.v7.widget.CardView cardView;
  TextView location;
  LinearLayout body;
  boolean visible = false;
  private Animation hide;
  private Animation show;
  int REQUEST_CODE = 1;
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_get_people_around);
    if (checkPlayServices()) {

      // Building the GoogleApi client
      buildGoogleApiClient ();
    }

    final ImageView clearText = (ImageView)findViewById (R.id.cancelImage);
    location = (TextView)findViewById (R.id.location);
    body = (LinearLayout)findViewById (R.id.body);
    enterLocation = (ImageView)findViewById (R.id.enter_one);
    goToLocation = (TextView)findViewById (R.id.go_to_location);
    goNow = (RelativeLayout)findViewById (R.id.go_now);
    enterLocation.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        if(visible && isOnline ()){
          Intent go = new Intent (GetPeopleAround.this, UserHomePage.class);
          startActivity (go);
        }
      }
    });
    waitingForLocation = (ProgressBar)findViewById (R.id.progress);
    location.setOnTouchListener (new View.OnTouchListener () {
      @Override
      public boolean onTouch (View v, MotionEvent event) {
        switch (event.getAction ()){
          case MotionEvent.ACTION_DOWN:
            if(isOnline ()){
              Intent getAddress = new Intent (GetPeopleAround.this, AutoComplete.class);
              startActivityForResult (getAddress, REQUEST_CODE);
            }
            else {
              Toast.makeText (getApplicationContext (), "Check your network connection",
                      Toast.LENGTH_SHORT).show ();
            }
        }
        return true;
      }
    });
    cardView = (CardView)findViewById (R.id.cardview);
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

  protected synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build();
  }

  private boolean checkPlayServices() {
    int resultCode = GooglePlayServicesUtil
            .isGooglePlayServicesAvailable (this);
    if (resultCode != ConnectionResult.SUCCESS) {
      if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
        GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                PLAY_SERVICES_RESOLUTION_REQUEST).show();
      } else {
        Toast.makeText(getApplicationContext(),
                "This device is not supported.", Toast.LENGTH_LONG)
                .show();
        finish();
      }
      return false;
    }
    return true;
  }

  @Override
  protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
      String result = data.getStringExtra ("result");
      double latitude = data.getDoubleExtra ("lat", 0);
      double longitude = data.getDoubleExtra ("lng",0);
      LatLng newLatLng = new LatLng (latitude, longitude);
      location.setText (result);
      Barcode.GeoPoint point =  getLocationFromAddress (result);
      LatLng latLng = new LatLng (point.lat/1000000,point.lng/1000000);
      showToast (point.lat / 1000000 + " " + point.lng / 1000000);
     // CameraPosition position = new CameraPosition (latLng, 8, 0, 360);
     // mMap.animateCamera (CameraUpdateFactory.newLatLng (latLng));
     // mMap.animateCamera (CameraUpdateFactory.newLatLng (newLatLng));
      CameraPosition newPosition = CameraPosition.builder ().target (newLatLng)
              .zoom (15).build ();
      mMap.animateCamera (CameraUpdateFactory.newCameraPosition (newPosition), 2000, null);
    }
  }

  public Barcode.GeoPoint getLocationFromAddress (String strAddress) {

    Geocoder coder = new Geocoder (this);
    List<Address> address;
    Barcode.GeoPoint p1 = null;

    try {
      address = coder.getFromLocationName (strAddress, 5);
      if (address == null) {
        return null;
      }
      Address location = address.get (0);
      location.getLatitude ();
      location.getLongitude ();

      p1 = new Barcode.GeoPoint (1, (int) (location.getLatitude () * 1E6),
              (int) (location.getLongitude () * 1E6));
    }

    catch (IOException e) {
      e.printStackTrace ();
    }

    return p1;

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
    displayLocation (mMap);
    mMap.setTrafficEnabled (true);
    mMap.setMyLocationEnabled (true);
    // Add a marker in Sydney and move the camera
    LatLng sydney = new LatLng (-34, 151);
    mMap.setOnCameraChangeListener (new GoogleMap.OnCameraChangeListener () {

      @Override
      public void onCameraChange (CameraPosition cameraPosition) {
        UpdateMap map = new UpdateMap (GetPeopleAround.this, cameraPosition);

        if(isOnline ()){
          map.execute ();
        }
        else {
          Toast.makeText (getApplicationContext (), "Network not available",
                  Toast.LENGTH_SHORT).show ();
        }
      }
    });

    mMap.setOnMapClickListener (new GoogleMap.OnMapClickListener () {
      @Override
      public void onMapClick (LatLng latLng) {
        if(!visible){
          cardView.setVisibility (View.VISIBLE);
          cardView.startAnimation (show);
          enterLocation.setImageResource (R.drawable.enter_two);
          waitingForLocation.setVisibility (View.INVISIBLE);
          goToLocation.setText ("Go to " + location.getText ().toString ());
          visible = true;
        }
        else {
          cardView.startAnimation (hide);
          cardView.setVisibility (View.GONE);
          goToLocation.setText ("");
          enterLocation.setImageResource (R.drawable.enter_one);
          waitingForLocation.setVisibility (View.VISIBLE);
          visible = false;
        }
      }

    });
  }

  @Override
  public void onConnected (Bundle bundle) {
    displayLocation(mMap);
  }

  @Override
  public void onConnectionSuspended (int i) {
    mGoogleApiClient.connect();
  }

  @Override
  public void onLocationChanged (Location location) {

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
          = new GoogleMap.OnMyLocationChangeListener() {
    @Override
    public void onMyLocationChange(Location location) {
      LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
      if(mMap != null){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
      }
    }
  };

  public boolean isOnline() {
    ConnectivityManager connectivityManager = (ConnectivityManager)
            getSystemService (Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
    if (info != null && info.isConnectedOrConnecting ()) {
      return true;
    } else {
      Toast.makeText (getApplicationContext (),"Check your internet connection",
              Toast.LENGTH_SHORT).show ();
      return false;
    }

  }

  @Override
  public void onConnectionFailed (ConnectionResult connectionResult) {
    Log.i("ERROR_TYPE=", "Connection failed: ConnectionResult.getErrorCode() = "
            + connectionResult.getErrorCode());

  }

  public class UpdateMap extends AsyncTask<Void, Void, Void>{
    Context context;
    CameraPosition position;
    List<Address> addresses;

    public UpdateMap(Context context, CameraPosition position){
      this.context = context;
      this.position = position;
    }

    @Override
    protected Void doInBackground (Void... params) {
      try {
        Geocoder geo = new Geocoder(GetPeopleAround.this.getApplicationContext(),
                Locale.getDefault ());
        addresses = geo.getFromLocation(position.target.latitude, position.target.longitude, 1);
      }
      catch (Exception e) {
        e.printStackTrace(); // getFromLocation() may sometimes fail
      }
      return null;
    }

    @Override
    protected void onPostExecute (Void aVoid) {
      super.onPostExecute (aVoid);
      try{
        if (addresses.isEmpty()) {
          location.setText(WAITING_FOR_LOCATION);
        }
        else {
          if (addresses.size() > 0) {
            location.setText(addresses.get(0).getFeatureName () + ", " +
                    addresses.get(0).getAdminArea()
                    + ", " + addresses.get(0).getCountryName());
            if(visible && isOnline () && !(location.getText ().toString ().contains (WAITING_FOR_LOCATION)) ){
              goToLocation.setText ("Go to "+addresses.get (0).getFeatureName ()+", "+addresses.get (0).getAdminArea ());
              waitingForLocation.setVisibility (View.INVISIBLE);
              enterLocation.setImageResource (R.drawable.enter_two);
            }
            else {
              goToLocation.setText ("");
              waitingForLocation.setVisibility (View.VISIBLE);
              enterLocation.setImageResource (R.drawable.enter_one);
            }
          }
        }
      }catch (Exception e){
        e.printStackTrace ();
      }
    }
  }
  public void initAnimation(){
    hide = AnimationUtils.loadAnimation (GetPeopleAround.this, R.anim.fadeout);
    show = AnimationUtils.loadAnimation (GetPeopleAround.this, R.anim.fadein);
  }
  public void showToast(String message){
    Toast.makeText (getApplicationContext (), message, Toast.LENGTH_LONG).show ();
  }

  private void displayLocation(GoogleMap map) {

    mLastLocation = LocationServices.FusedLocationApi
            .getLastLocation(mGoogleApiClient);

    if (mLastLocation != null) {
      double latitude = mLastLocation.getLatitude();
      double longitude = mLastLocation.getLongitude ();
      LatLng loc = new LatLng(latitude, longitude);
      if(mMap != null){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
      }
      //lblLocation.setText(latitude + ", " + longitude);

    } else {
      Toast.makeText (getApplicationContext (),"Map getting ready...", Toast.LENGTH_SHORT).show ();
    }
  }
  @Override
  protected void onStart() {
    super.onStart();
    if (mGoogleApiClient != null) {
      mGoogleApiClient.connect();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    checkPlayServices();
  }

}
