package com.example.bookac.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookac.R;
import com.example.bookac.singletons.Chef;
import com.example.bookac.singletons.User;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* By Aliyu Olalekan */
/*
* User Homepage Activity has a map that shows the user's location and list of all chefs around
* the location.
* */

public class UserHomePage extends AppCompatActivity implements OnMapReadyCallback{

  static final CameraPosition TOKYO = CameraPosition.builder ().target (new LatLng (35.6895, 139.6917))
          .tilt (45).zoom (17).bearing (0).build ();

  ArrayList<Chef> chefs = new ArrayList<Chef> ();
  boolean mapReady = false;
  String address;
  int count = 0;
  OnDataRecieved onDataRecieved;
  ImageView expand;
  protected  GoogleApiClient googleApiClient;
  protected Location location;
  protected static double lng;
  protected static double lat;
  ListView chefsList;
  public static final String PREFS_NAME = "ListFileOne";

  LocationManager locationManager;
  protected LocationRequest locationRequest;
  protected static final String TAG = "Location: ";
  Toolbar toolbar;
  MapFragment mapFragment;
  ArrayList<String> addresses = new ArrayList<String> ();
  ArrayList<Float> longitudeArrayList = new ArrayList<Float> ();
  ArrayList<Float> latitudeArrayList = new ArrayList<Float> ();
  GoogleMap map;
  PolylineOptions options;
  MarkerOptions newYorkCity;
  ImageView getMyLocation;

  /*
   * the onCreate method starts the activity and renders the view
    * */

  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_user_home_page);
    toolbar = (Toolbar)findViewById (R.id.toolbar);
    setSupportActionBar (toolbar);
    getMyLocation = (ImageView)findViewById (R.id.sendMeHome);
    getUserData ();
    chefsList = (ListView)findViewById (R.id.listView);
    com.pkmmte.view.CircularImageView userImage= (com.pkmmte.view.CircularImageView)
            findViewById (R.id.myAvartar);

    //Picassso Library is used to load image into imageview

    try {
      Picasso.with (UserHomePage.this).load (User.imageUrl)
              .error (R.drawable.logo).placeholder (R.drawable.logo)
              .into (userImage);
    }catch (Exception e){
      e.printStackTrace ();
    }
    //get All the chefs around
    final FrameLayout frame = (FrameLayout)findViewById (R.id.frameForMap);


    expand = (ImageView)findViewById (R.id.pullMap);

    //expand the view for better access

    expand.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        ViewGroup.LayoutParams params = frame.getLayoutParams ();

        if(count == 0){
          expand.setImageResource (R.drawable.collapse);
          params.height = 800;
          count += 1;
        }
        else {
          expand.setImageResource (R.drawable.expand);
          count -=1;
          params.height = 460;
        }


      }
    });

    // the getAllCHeffsArround method is used to get all the chefs around a particular location

    getAllCheffsArround ("http://mybukka.herokuapp.com/api/v1/bukka/chefs/lat/long");
    if(mapReady){
      map.setMapType (GoogleMap.MAP_TYPE_NORMAL);
    }
    options = new PolylineOptions ().geodesic (true).add (new LatLng (User.getDouble (UserHomePage.this, "latitude", (float) 0.0), User.getDouble (UserHomePage.this, "longitude", (float) 0.0)));
    newYorkCity = new MarkerOptions ().position (new LatLng (User.getDouble (UserHomePage.this, "latitude", (float) 0.0), User.getDouble (UserHomePage.this, "longitude", (float) 0.0)))
            .icon (BitmapDescriptorFactory.fromResource (R.drawable.currentlocation))
            .title (User.getString (UserHomePage.this, "address", ""));
    mapFragment = (MapFragment)getFragmentManager ().findFragmentById (R.id.map);
    mapFragment.getMapAsync (this);
    getMyLocation.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        CameraPosition MYLOCATION = CameraPosition.builder ()
                .target (new LatLng (User.getDouble (UserHomePage.this, "latitude", (float) 0.0)
                        , User.getDouble (UserHomePage.this, "longitude", (float) 0.0))).zoom (14).
                        build ();
                flyTo (MYLOCATION);
      }
    });

  }


//create context menu

  @Override
  public boolean onCreateOptionsMenu (Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater ().inflate (R.menu.menu_user_home_page, menu);
    return true;
  }

  //getAllcheffsAround a location using Url

  public void getAllCheffsArround(final String chefUrl){
    RequestQueue que = Volley.newRequestQueue (UserHomePage.this);
    final StringRequest request = new StringRequest (Request.Method.GET, chefUrl, new Response.Listener<String> () {
      @Override
      public void onResponse (String response) {
        try {
          JSONArray chefArray = new JSONArray (response);
          for (int i = 0; i< chefArray.length (); i++){
            JSONObject currentChef = chefArray.getJSONObject (i);
            Chef chef = new Chef ();
            chef.address = currentChef.getString ("address");
            chef.firstname = currentChef.getString ("first_name");
            chef.lastname = currentChef.getString ("last_name");
            JSONObject coord = currentChef.getJSONObject ("coords");
            chef.longitude = Double.parseDouble (coord.getString ("lng"));
            chef.latitude = Double.parseDouble (coord.getString ("lat"));
//            if(currentChef.getString ("profile_photo")!= null){
//              chef.profilePhoto = currentChef.getString ("profile_photo");
//            }
            chefs.add (chef);
            addresses.add (currentChef.getString ("address"));
            longitudeArrayList.add (Float.parseFloat (coord.getString ("lng")));
            latitudeArrayList.add (Float.parseFloat (coord.getString ("lat")));
           /***** error here ****** pausing ******/
            myAdapter adapter = new myAdapter (UserHomePage.this, chefs);
            chefsList.setAdapter (adapter);


          }
        } catch (JSONException e) {
          e.printStackTrace ();
        }
        saveToInternalStorage (UserHomePage.this, chefs);



      }
    }, new Response.ErrorListener () {
      @Override
      public void onErrorResponse (VolleyError error) {
        Log.e ("", error.toString ());
      }
    });

    que.add (request);

  }

  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId ();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected (item);
  }

  //onMapReady is called when map is ready

  @Override
  public void onMapReady (GoogleMap googleMap) {
    getUserData ();
    mapReady = true;
    map = googleMap;
    map.addMarker (newYorkCity);
    ArrayList<Marker> markers = new ArrayList<Marker> ();
    for(Chef chef : readFromInternalStorage (UserHomePage.this)){
      double lat = chef.latitude;
      double longit = chef.longitude;
      PolylineOptions rectOptions = new PolylineOptions()
              .add (new LatLng (chef.latitude, chef.longitude));
      map.addPolyline (rectOptions);

      map.addMarker (new MarkerOptions ().position (new LatLng (lat,longit))
              .title (chef.address).draggable (true)
              .icon (BitmapDescriptorFactory.fromResource (R.drawable.icon_marker)));
    }

    LatLng newYork = new LatLng (User.getDouble (UserHomePage.this, "latitude", (float) 0.0)  , User.getDouble (UserHomePage.this, "longitude", (float) 0.0));
    CameraPosition cameraPosition = CameraPosition.builder ().target (newYork).zoom (14).build ();
    map.moveCamera (CameraUpdateFactory.newCameraPosition (cameraPosition));
  }

  private void flyTo(CameraPosition target) {
    map.animateCamera (CameraUpdateFactory.newCameraPosition (target), 2000, null);
  }

  //getUserData obtains the current user's data, including location and image url
  public void getUserData(){
    RequestQueue queue = Volley.newRequestQueue ( UserHomePage.this);
    StringRequest request = new StringRequest (Request.Method.POST, "http://mybukka.herokuapp.com/api/v1/bukka/auth/login", new Response.Listener<String> () {
      @Override
      public void onResponse (String response) {
        String userImageUrl = null;
        try {
          JSONObject responseObject = new JSONObject (response);
          Log (response);
          JSONObject authObject = responseObject.getJSONObject ("auth");
          String token = authObject.getString ("token");
          JSONObject imageUrlObject = authObject.getJSONObject ("password");
          userImageUrl = imageUrlObject.getString ("profileImageURL");
          User.imageUrl = userImageUrl;
          JSONObject userObject = responseObject.getJSONObject ("userObj");
          address = userObject.getString ("address");
          JSONObject coordnates = userObject.getJSONObject ("coords");
          User.saveDouble ("longitude", Float.parseFloat (coordnates.getString ("lng")), UserHomePage.this);
          User.saveDouble ("latitude", Float.parseFloat (coordnates.getString ("lat")), UserHomePage.this);
          User.saveString ("address", address, UserHomePage.this);
        } catch (JSONException e) {
          e.printStackTrace ();
        }
      }
    }, new Response.ErrorListener () {
      @Override
      public void onErrorResponse (VolleyError error) {
        Toast.makeText (getApplicationContext (), error+ "", Toast.LENGTH_SHORT).show ();
        Log.e ("Error:", error+"");
      }
    }){
      @Override
      protected Map<String, String> getParams () throws AuthFailureError {
        super.getParams ();
        Map<String,String> params = new HashMap<String, String> ();
        params.put ("email", User.getDB (UserHomePage.this, "username", ""));
        params.put ("password", User.getDB (UserHomePage.this, "password", ""));
        return params;
      }

      @Override
      public Map<String, String> getHeaders () throws AuthFailureError {
        super.getHeaders ();
        Map<String,String> params = new HashMap<String, String>();
        params.put("Content-Type","application/x-www-form-urlencoded");
        return params;
      }
    };
    queue.add (request);
  }

  //a method for toasting messages
  public void Toast(String message){
    Toast.makeText (getApplicationContext (), message, Toast.LENGTH_SHORT).show ();
  }

  //a method for loging messages
  public void Log(String message){
    Log.e("",message);
  }


  public interface OnDataRecieved{
    void onDataRecieved(ArrayList<Chef> chefArrayList);
  }

//saveToInternalStorage method saves data to internal storage
  public void saveToInternalStorage(Context ctx, ArrayList<Chef> aList) {
    try {
      FileOutputStream fos = ctx.openFileOutput("myfile", Context.MODE_PRIVATE);
      ObjectOutputStream of = new ObjectOutputStream(fos);
      of.writeObject(aList);
      of.flush();
      of.close();
      fos.close();
    }
    catch (Exception e) {
      Log.e("InternalStorage", e.getMessage());
    }
  }

  //readFromInternalStorage method reads saved arraylist from internal storage
  public ArrayList<Chef> readFromInternalStorage(Context ctx) {
    ArrayList<Chef> toReturn = null;
    FileInputStream fis;
    try {
      fis = ctx.openFileInput("myfile");
      ObjectInputStream oi = new ObjectInputStream(fis);
      try {
        toReturn = (ArrayList<Chef>) oi.readObject();
      } catch (ClassNotFoundException e) {
        e.printStackTrace ();
      }
      oi.close();
    } catch (FileNotFoundException e) {
      Log.e("InternalStorage", e.getMessage());
    } catch (IOException e) {
      Log.e("InternalStorage", e.getMessage());
    }
    return toReturn;
  }

  //get user data when app resumes
  @Override
  protected void onResume () {
    super.onResume ();
    getUserData ();
    Toast(User.imageUrl);
  }

  //listview adapter for the list items
  public class myAdapter extends BaseAdapter{
    Context context;
    ArrayList<Chef> chefs;
    public myAdapter(Context context, ArrayList<Chef> chefs){
      this.context = context;
      this.chefs = chefs;
    }
    @Override
    public int getCount () {
      if(chefs != null){

        return chefs.size ();
      }
      return 0;
    }

    @Override
    public Object getItem (int position) {
      return chefs.get (position);
    }

    @Override
    public long getItemId (int position) {
      return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
      View row = null;
      Chef chef = chefs.get (position);

      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.adapter_for_listview, parent, false);
      } else {
        row = convertView;
      }
      com.pkmmte.view.CircularImageView image = (com.pkmmte.view.CircularImageView)row
              .findViewById (R.id.avatar);
      TextView chefname = (TextView)row.findViewById (R.id.chefname);
      TextView chefaddress = (TextView)row.findViewById (R.id.chefaddress);
      TextView chefdistance = (TextView)row.findViewById (R.id.chefdistance);
      String text = chef.firstname + " "+chef.lastname;
      try{
        chefname.setText (text);
        chefaddress.setText (chef.address);
        if(chef.profilePhoto != null){
          Picasso.with (UserHomePage.this).load (chef.profilePhoto)
                  .error (R.drawable.logo).placeholder (R.drawable.logo)
                  .into (image);
        }
      }catch (NullPointerException e){
        e.printStackTrace ();
      }

      return row;
    }
  }
}