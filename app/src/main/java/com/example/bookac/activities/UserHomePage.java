package com.example.bookac.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookac.Adapters.SearchAutoCompleteAdapter;
import com.example.bookac.R;
import com.example.bookac.fragments.NavigationFragment;
import com.example.bookac.singletons.Chef;
import com.example.bookac.singletons.User;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
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
import com.squareup.picasso.NetworkPolicy;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import logger.Toaster;

/* By Aliyu Olalekan */
/*
* User Homepage Activity has a map that shows the user's location and list of all chefs around
* the location.
* */

public class UserHomePage extends AppCompatActivity implements OnMapReadyCallback {

  ArrayList<Chef> chefs = new ArrayList<Chef> ();
  static ArrayList<Chef> myChefs;
  boolean mapReady = false;
  CardView searchCard;
  SearchAutoCompleteAdapter searchAdapter;
  String address;
  int count = 0;
  public Boolean notGotten = true;
  AutoCompleteTextView autoCompleteTextView;
  ImageView expand;
  Toaster toaster = new Toaster();
  protected Location location;
  protected static double lng;
  protected static double lat;
  com.pkmmte.view.CircularImageView userImage;
  ListView chefsList;
  ProgressBar listItemAvailableProgressBar;
  public static final String PREFS_NAME = "ListFileOne";
  TextView noChefFound;
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
  myAdapter adapter;
  private boolean isResume;
  DrawerLayout layout;
  NavigationFragment navigationFragment;
  /*
   * the onCreate method starts the activity and renders the view
    * */

  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_user_home_page);
    toolbar = (Toolbar) findViewById (R.id.toolbar);
    setSupportActionBar (toolbar);
    layout = (DrawerLayout)findViewById (R.id.drawerLayoutuserhomepage);
    setSupportActionBar (toolbar);




    navigationFragment = (NavigationFragment)getSupportFragmentManager ().findFragmentById (R.id.navigation_fragment);
    navigationFragment.setUp (R.id.navigation_fragment, layout, toolbar);
    listItemAvailableProgressBar = (ProgressBar) findViewById (R.id.progressBar);
    autoCompleteTextView = (AutoCompleteTextView)findViewById (R.id.autocomplete_place_text_view);
    searchCard = (CardView)findViewById (R.id.searchcard);
    searchCard.setVisibility (View.INVISIBLE);
    getUserData ();
    chefsList = (ListView) findViewById (R.id.listView);
    userImage = (com.pkmmte.view.CircularImageView)
            findViewById (R.id.myAvartar);

    //Picassso Library is used to load image into imageview

    try {
      Picasso.with (UserHomePage.this).load (User.imageUrl)
              .error (R.drawable.logo).placeholder (R.drawable.logo)
              .into (userImage);
    } catch (Exception e) {
      e.printStackTrace ();
    }
    //get All the chefs around
    noChefFound  = (TextView)findViewById (R.id.not_found);
    noChefFound.setVisibility (View.INVISIBLE);
    final FrameLayout frame = (FrameLayout) findViewById (R.id.frameForMap);


    expand = (ImageView) findViewById (R.id.pullMap);

    //expand the view for better access

    expand.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        ViewGroup.LayoutParams params = frame.getLayoutParams ();

        if (count == 0) {
          expand.setImageResource (R.drawable.collapse);
          params.height = 800;
          count += 1;
        } else {
          expand.setImageResource (R.drawable.expand);
          count -= 1;
          params.height = 460;
        }
      }
    });

     //the getAllCHeffsArround method is used to get all the chefs around a particular locatio
    getCoordinates ();
    getAllCheffsArround ("http://mybukka.herokuapp.com/api/v1/bukka/chefs/" + lat + "/" + lng);
    try{

    }catch (Exception e){
      e.printStackTrace ();
    }
    if (mapReady) {
      map.setMapType (GoogleMap.MAP_TYPE_NORMAL);
    }
    options = new PolylineOptions ().geodesic (true).add (new LatLng (User.getDouble (UserHomePage.this, "latitude", (float) 0.0), User.getDouble (UserHomePage.this, "longitude", (float) 0.0)));
//    newYorkCity = new MarkerOptions ().position (new LatLng (User.getDouble (UserHomePage.this, "latitude", (float) 0.0), User.getDouble (UserHomePage.this, "longitude", (float) 0.0)))
//            .icon (BitmapDescriptorFactory.fromResource (R.drawable.currentlocation))
//            .title (User.getString (UserHomePage.this, "address", ""));
    mapFragment = (MapFragment) getFragmentManager ().findFragmentById (R.id.map);
    mapFragment.getMapAsync (this);

  }

//create context menu

  @Override
  public boolean onCreateOptionsMenu (Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater ().inflate (R.menu.menu_user_home_page, menu);
    return true;
  }

  //getAllcheffsAround a location using Url

  //cleanup toolbar
  public int getStatusBarHeight() {
    int result = 0;
    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (resourceId > 0) {
      result = getResources().getDimensionPixelSize(resourceId);
    }
    return result;
  }


  public void getAllCheffsArround (final String chefUrl) {
    RequestQueue que = Volley.newRequestQueue (UserHomePage.this);
    final StringRequest request = new StringRequest (Request.Method.GET, chefUrl, new Response.Listener<String> () {
      @Override
      public void onResponse (String response) {
        Log.e (" ", response);
        DecimalFormat decimalFormat = new DecimalFormat ("#");
        decimalFormat.setMaximumFractionDigits (0);
        try {
          JSONArray chefArray = new JSONArray (response);
          for (int i = 0; i < chefArray.length (); i++) {
            JSONObject currentChef = chefArray.getJSONObject (i);
            final Chef chef = new Chef ();
            chef.address = currentChef.getString ("address");
            chef.firstname = currentChef.getString ("first_name");
            chef.lastname = currentChef.getString ("last_name");
            chef.nickName = currentChef.getString ("username");
            chef.uid = currentChef.getString ("uid");
            chef.phoneNumber = Long.parseLong ((decimalFormat.format (Double.parseDouble (currentChef.getString ("phone_number")))));
            JSONObject coord = currentChef.getJSONObject ("coords");
            chef.longitude = Double.parseDouble (coord.getString ("lng"));
            chef.latitude = Double.parseDouble (coord.getString ("lat"));
            if (currentChef.getString ("profile_photo") != null) {
              chef.profilePhoto = currentChef.getString ("profile_photo");
            }
            chefs.add (chef);
            if(chef != null){
              searchCard.setVisibility (View.VISIBLE);
            }
            if(chef.address == null)
            {
              Toast.makeText (getApplicationContext (), "Please search for another location", Toast.LENGTH_SHORT).show ();
              noChefFound.setVisibility (View.VISIBLE);
              chefsList.setVisibility (View.INVISIBLE);
              searchCard.setVisibility (View.INVISIBLE);
            }


            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            double allLatitudes = 0;
            double allLongitudes = 0;
            try{
              for(Chef nchef : chefs){

                double lat = nchef.latitude;
                double longit = nchef.longitude;
                allLatitudes += nchef.latitude;
                allLongitudes += nchef.longitude;
                builder.include (new LatLng (lat, longit));
                LatLngBounds bounds = builder.build();
                PolylineOptions rectOptions = new PolylineOptions()
                        .add (new LatLng (nchef.latitude, nchef.longitude));
                map.addPolyline (rectOptions);

                map.addMarker (new MarkerOptions ().position (new LatLng (lat, longit))
                        .title (nchef.address).draggable (true)
                        .icon (BitmapDescriptorFactory.fromResource (R.drawable.icon_marker)));
              }
              double averageLatitude = allLatitudes / chefs.size ();
              double averageLongitude = allLongitudes / chefs.size ();
              CameraPosition position = CameraPosition.builder ()
                      .target (new LatLng (averageLatitude, averageLongitude)).zoom (15).build ();
              map.animateCamera (CameraUpdateFactory.newCameraPosition (position));
            }catch (Exception e){
              e.printStackTrace ();
            }
//            User.myChef.add (chef);
//            User.myChef.add (chef);
            saveToInternalStorage (UserHomePage.this, chefs);
            addresses.add (currentChef.getString ("address"));
            longitudeArrayList.add (Float.parseFloat (coord.getString ("lng")));
            latitudeArrayList.add (Float.parseFloat (coord.getString ("lat")));
            adapter = new myAdapter (UserHomePage.this, chefs);
            chefsList.setAdapter (adapter);
//            if(autoCompleteTextView.getText () == null){
//              myAdapter adapter1 = new myAdapter (UserHomePage.this, chefs);
//              chefsList.setAdapter (adapter1);
//              adapter1.notifyDataSetChanged ();
//            }
            searchAdapter = new SearchAutoCompleteAdapter (UserHomePage.this, R.layout.layout_for_autocomplete, chefs);
            autoCompleteTextView.setAdapter (searchAdapter);
            autoCompleteTextView.setThreshold (1);
            autoCompleteTextView.addTextChangedListener (new MyTextWatcher (searchAdapter));
            if(chefsList.getCount () != 0){
              listItemAvailableProgressBar.setVisibility (View.INVISIBLE);
            }
            else {
              Toast.makeText (getApplicationContext (), "No Item in List", Toast.LENGTH_LONG).show ();
            }
              final Handler h = new Handler() {
                @Override
                public void handleMessage(Message message) {
                  listItemAvailableProgressBar.setVisibility (View.INVISIBLE);
                }
              };
            h.sendMessageDelayed (new Message (), 10000);

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
    int socketTimeout = 30000;//30 seconds - change to what you want
    RetryPolicy policy = new DefaultRetryPolicy (socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    request.setRetryPolicy (policy);
    request.setShouldCache (true);

    que.add (request);

  }
  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId ();

    //noinspection SimplifiableIfStatement


    return super.onOptionsItemSelected (item);
  }

  //onMapReady is called when map is ready

  @Override
  public void onMapReady (GoogleMap googleMap) {
    getUserData ();
    mapReady = true;
    map = googleMap;

//    map.addMarker (newYorkCity);
    map.setMyLocationEnabled (true);

    if(isResume == true) {
      Log.e("","Resume is called");
      CameraUpdate update = CameraUpdateFactory.newLatLngZoom (new LatLng (6.33354, 3.34534), 15);
      map.moveCamera (update);
      map.animateCamera (update);
    }

    ArrayList<Marker> markers = new ArrayList<Marker> ();


    chefsList.setOnItemClickListener (new AdapterView.OnItemClickListener () {
      @Override
      public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
        Intent toChefpage = new Intent (UserHomePage.this, ChefMenu.class);
        toChefpage.putExtra ("phoneNumber", chefs.get (position).phoneNumber);
        toChefpage.putExtra ("longitude", chefs.get (position).longitude);
        toChefpage.putExtra ("latitude", chefs.get (position).latitude);
        toChefpage.putExtra ("firstname", chefs.get (position).firstname);
        toChefpage.putExtra ("lastname", chefs.get (position).lastname);
        toChefpage.putExtra ("nickname", chefs.get (position).nickName);
        toChefpage.putExtra ("address", chefs.get (position).address);
        toChefpage.putExtra ("uid", chefs.get (position).uid);
        startActivity (toChefpage);
        overridePendingTransition (R.anim.right_in, R.anim.left_out);
      }
    });

    getCoordinates ();

    LatLng position = new LatLng (lat, lng);

    CameraPosition cameraPosition = CameraPosition.builder ().target (position).zoom (15).build ();
    map.moveCamera (CameraUpdateFactory.newCameraPosition (cameraPosition));
  }

  private void flyTo (CameraPosition target) {
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

    int socketTimeout = 30000;//30 seconds - change to what you want
    RetryPolicy policy = new DefaultRetryPolicy (socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    request.setRetryPolicy(policy);
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
      clearFile ("myfile");
      File file = new File("myfile");
      if(file.exists())
        file.delete ();

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
      oi.close ();
    } catch (FileNotFoundException e) {
      Log.e("InternalStorage", e.getMessage());
    } catch (IOException e) {
      Log.e ("InternalStorage", e.getMessage ());
    }
    return toReturn;
  }

  //get user data when app resumes
  @Override
  protected void onResume () {
    super.onResume ();
    isResume = true;
    getUserData ();
    userImage = (com.pkmmte.view.CircularImageView)
            findViewById (R.id.myAvartar);
    try {
      Picasso.with (UserHomePage.this).load (User.imageUrl)
              .error (R.drawable.logo).placeholder (R.drawable.logo)
              .into (userImage);
    }catch (Exception e){
      e.printStackTrace ();
    }
    CameraPosition position = CameraPosition.fromLatLngZoom (new LatLng (6,4),15);
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
//      TextView chefdistance = (TextView)row.findViewById (R.id.chefdistance);
      String text = chef.nickName;
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

  public void getCoordinates(){
    Intent getCoordinates = getIntent ();
    lat = getCoordinates.getDoubleExtra ("latitude", 0);
    lng = getCoordinates.getDoubleExtra ("longitude",0);
  }

  public void clearFile(String filenName){
    File dir = getFilesDir();
    File file = new File(dir, filenName);
    file.delete();
  }
  public boolean isOnline() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
    if (info != null && info.isConnectedOrConnecting ()) {
      return true;
    } else {
      Toast.makeText (UserHomePage.this,"Check your internet connection", Toast.LENGTH_SHORT).show ();
      return false;
    }
  }

  public class MyTextWatcher implements TextWatcher {
    private SearchAutoCompleteAdapter lAdapter;

    public MyTextWatcher(SearchAutoCompleteAdapter lAdapter) {
      this.lAdapter = lAdapter;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      lAdapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {


    }
  }

}
