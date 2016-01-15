package com.example.bookac.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.bookac.R;
import com.example.bookac.constants.Constants;
import com.example.bookac.fragments.NavigationFragment;
import com.example.bookac.singletons.Chef;
import com.example.bookac.singletons.ItemCart;
import com.example.bookac.singletons.MenuItem;
import com.example.bookac.tools.PicassoImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import logger.Log;

public class UserMenu extends AppCompatActivity {
  public LinearLayout callImage;
  public RelativeLayout rateImage;
  public RelativeLayout reportImage;
  public TextView chefAddress;
  private String chefUid;
  private String chefFirstName;
  private String chefLastName;
  com.pkmmte.view.CircularImageView chefAvatar;
  private String chefNick;
  private String chefAddrss;
  private long phoneNumber;
  private double cheflongitude;
  private double chefLatitude;
  SwipeRefreshLayout mSwipeRefreshLayout;
  public TextView chefTitle;
  private DrawerLayout mdrawerLayout;
  NavigationFragment navigationFragment;
  final String TAG =  UserMenu.class.getName ();
  String uid;
  private String name;

  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_user_menu);
    getIntentContent ();
    setUpView ();
    PicassoImageLoader loader = new PicassoImageLoader (UserMenu.this);
    loader.loadImage (chefAvatar, getIntent ().getStringExtra ("chefProfilePhoto"));
    Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
    setSupportActionBar (toolbar);

    getSupportActionBar().setTitle (chefNick);

    mdrawerLayout = (DrawerLayout) findViewById (R.id.drawer_layout_menu);
    navigationFragment = (NavigationFragment)getSupportFragmentManager ().findFragmentById (R.id.navigation_fragment);
    navigationFragment.setUp (R.id.navigation_fragment, mdrawerLayout, toolbar);
    chefTitle.setText (chefFirstName + " " + chefLastName);
    CollapsingToolbarLayout collapsingToolbar =
            (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
    String[] addressLocale = chefAddrss.split (",");
    collapsingToolbar.setTitleEnabled (false);
    String addressName = chefAddrss.split (",")[addressLocale.length-2];
    String myAddress = addressLocale[addressLocale.length-1];

    try { chefAddress.setText (addressName + ", " + myAddress); } catch (NullPointerException e){e.printStackTrace ();}


    getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
    RecyclerView rv = (RecyclerView)findViewById (R.id.rvToDoList);
    setupRecyclerView (rv, Constants.MENU_URL);
    callImage.setOnClickListener (new View.OnClickListener () {
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
    mSwipeRefreshLayout.setOnRefreshListener (new SwipeRefreshLayout.OnRefreshListener () {
      @Override
      public void onRefresh () {
        refreshItems ();
      }
    });
  }



  void refreshItems() {
    // Load items
    // ...

    // Load complete
    onItemsLoadComplete ();
  }

  void onItemsLoadComplete() {
    // Update the adapter and notify data set changed
    // ...

    // Stop refresh animation
    mSwipeRefreshLayout.setRefreshing (false);
  }

  public void getChefMenu(String url){
  }


  private void setupRecyclerView(final RecyclerView recyclerView, String url) {

    RequestQueue queue = Volley.newRequestQueue (UserMenu.this);
    StringRequest request = new StringRequest (Request.Method.GET, url+ chefUid, new Response.Listener<String> () {
      @Override
      public void onResponse (String response) {
        android.util.Log.e ("response: ", response);
        try {
          Chef chef = new Chef ();

          JSONArray responseArray = new JSONArray (response);
          for (int i = 0; i < responseArray.length (); i++){
            MenuItem item = new MenuItem ();
            JSONObject responseObject = responseArray.getJSONObject (i);
            item.description = responseObject.getString ("desc");
            item.price = responseObject.getDouble ("price");
            item.quantity = responseObject.getInt ("quantity");
            item.doneTime = responseObject.getInt ("min");
            item.doneTimeInOur = responseObject.getInt ("hour");
            item.name = responseObject.getString ("menu");
            item.photo = responseObject.getString ("image");
            chef.menuItems.add (item);
          }
          for(MenuItem itemx: chef.menuItems){
            Toast.makeText (getApplicationContext (), itemx.name, Toast.LENGTH_SHORT).show ();
          }

          recyclerView.setLayoutManager (new LinearLayoutManager (recyclerView.getContext ()));
          if(chef.menuItems != null)
            recyclerView.setAdapter (new SimpleStringRecyclerViewAdapter (UserMenu.this,
                  chef));
          //  menuAdapter menuAdapter = new menuAdapter (Menu.this, chef);
          //  listView.setAdapter (menuAdapter);

        } catch (JSONException e) {
          e.printStackTrace ();
        }
      }
    }, new Response.ErrorListener () {
      @Override
      public void onErrorResponse (VolleyError error) {

      }
    });
    int socketTimeout = 30000;//30 seconds - change to what you want
    RetryPolicy policy = new DefaultRetryPolicy (socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    request.setRetryPolicy (policy);
    queue.add (request);

  }

  public class SimpleStringRecyclerViewAdapter
          extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    Chef chef;
    public class ViewHolder extends RecyclerView.ViewHolder {
      public LinearLayout checkout;
      public String mBoundString;
      public TextView nameOfMeal;
      public TextView price;
      public TextView cookingTime;
      public TextView mealType;
      public final View mView;
      public ImageView desertImage;
      public ImageView share;
      public ImageView addTocart;

      public TextView chefTitle;

      public ViewHolder(View view) {
        super(view);
        mView = view;
        nameOfMeal = (TextView)view.findViewById (R.id.nameOfMeal);
        mealType = (TextView)view.findViewById (R.id.mealTypeX);
        cookingTime = (TextView)view.findViewById (R.id.cookingTime);
        price = (TextView)view.findViewById (R.id.price);
        desertImage = (ImageView)view.findViewById (R.id.desert);
        addTocart = (ImageView)view.findViewById (R.id.addTocartImage);
        share = (ImageView)view.findViewById (R.id.sharemeal);
        checkout = (LinearLayout)view.findViewById (R.id.checkout);

      }

      @Override
      public String toString() {
        return super.toString() ;
      }
    }

    public MenuItem getValueAt(int position) {
      return chef.menuItems.get(position);
    }

    public SimpleStringRecyclerViewAdapter(Context context, Chef chef) {
      this.chef = chef;
      context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
      mBackground = mTypedValue.resourceId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from (parent.getContext ())
              .inflate(R.layout.list_item, parent, false);
      view.setBackgroundResource(mBackground);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
      final MenuItem item = chef.menuItems.get (position);
      holder.price.setText ("N" + item.price);
      holder.cookingTime.setText (item.doneTime+"");
      holder.nameOfMeal.setText (item.name);
      holder.share.setOnClickListener (new View.OnClickListener () {
        @Override
        public void onClick (View v) {
          shareIt ("Hello Yet", "how are you");
        }
      });

      holder.checkout.setOnClickListener (new View.OnClickListener () {
        @Override
        public void onClick (View v) {
          double price = item.price;
          String itemName = item.name;
          String photo = item.photo;
          Intent makePayment = new Intent (UserMenu.this, Payment.class);
          makePayment.putExtra ("price", price);
          makePayment.putExtra ("name", itemName);
          makePayment.putExtra ("photo", photo);
          makePayment.putExtra ("uid", chefUid);
          startActivity (makePayment);
        }
      });

      holder.addTocart.setOnClickListener (new View.OnClickListener () {
        @Override
        public void onClick (View v) {
          ItemCart.INSTANCE.addToItem (item);
          if(ItemCart.INSTANCE.getSize () > 0){
            ItemCart.INSTANCE.itemIsinCart = true;
            if(ItemCart.INSTANCE.getSize () == 1){
              Toast.makeText (getApplicationContext (), ItemCart.INSTANCE.getAllItems ().size () +  " Item Added to cart", Toast.LENGTH_SHORT).show ();
            }
            Toast.makeText (getApplicationContext (), ItemCart.INSTANCE.getAllItems ().size () +  " Items Added to cart", Toast.LENGTH_SHORT).show ();
          }
        }
      });
      PicassoImageLoader loader = new PicassoImageLoader (UserMenu.this);
      loader.loadImage (holder.desertImage, item.photo);

    }

    @Override
    public int getItemCount() {
      return chef.menuItems.size();
    }
  }

  /**
   *
   * @param header is the message header
   * @param shareBody is the message body
   */
  private void shareIt (String header, String shareBody) {
    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    sharingIntent.putExtra (android.content.Intent.EXTRA_SUBJECT, header);
    sharingIntent.putExtra (android.content.Intent.EXTRA_TEXT, shareBody);
    startActivity (Intent.createChooser (sharingIntent, "Share via"));
  }

  /**
   * Obtain contents from the item clicked
   */
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

  private void setUpView(){
    chefAvatar = (com.pkmmte.view.CircularImageView)findViewById (R.id.chefAvatarY);
    mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById (R.id.swipeRefreshLayout);
    callImage = (LinearLayout)findViewById (R.id.callImage);
    rateImage = (RelativeLayout)findViewById (R.id.rateImage);
    reportImage = (RelativeLayout)findViewById (R.id.reportImage);
    chefAddress = (TextView)findViewById (R.id.chefAddress);
    chefTitle = (TextView)findViewById (R.id.chefMenuName);
  }
}
