package com.example.bookac;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.example.bookac.singletons.Chef;
import com.example.bookac.singletons.MenuItem;
import com.example.bookac.singletons.User;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Menu extends AppCompatActivity {
  Chef chef = new Chef ();
  ListView listView;
  private final String CHEF_MENU_URL = "http://mybukka.herokuapp.com/api/v1/bukka/chef/menu/";
  String uid;
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_menu);
    getIntentFromChefMenuActiviyt ();
    Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
    setSupportActionBar (toolbar);
    getSupportActionBar ().setTitle ("");

    getChefMenu (CHEF_MENU_URL);

    Toast.makeText (getApplicationContext (), "Uid: " + uid, Toast.LENGTH_SHORT).show ();
    getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
    final Drawable upArrow = getResources ().getDrawable (R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    upArrow.setColorFilter (getResources ().getColor (android.R.color.white), PorterDuff.Mode.SRC_ATOP);
    getSupportActionBar ().setHomeAsUpIndicator (upArrow);
    listView = (ListView)findViewById (R.id.menu_items);

  }



  public void getIntentFromChefMenuActiviyt(){
    Intent getIntent = getIntent ();
    uid = getIntent.getStringExtra ("uid");
  }

  public void getChefMenu(String url){
    RequestQueue queue = Volley.newRequestQueue (Menu.this);
    StringRequest request = new StringRequest (Request.Method.GET, url+ uid, new Response.Listener<String> () {
      @Override
      public void onResponse (String response) {

        try {
          Chef chef = new Chef ();
          JSONObject responseObject = new JSONObject (response);
          JSONObject fuckedResonse = responseObject.getJSONObject ("menufxone");
          String desc = fuckedResonse.getString ("desc");
          MenuItem item = new MenuItem ();
          item.description = desc;
          item.price = fuckedResonse.getLong ("price");
          item.quantity = fuckedResonse.getInt ("quantity");
          item.doneTime = fuckedResonse.getInt ("min");
          item.name = fuckedResonse.getString ("menu");
          JSONArray imageUrls = fuckedResonse.getJSONArray ("images");
          for(int i = 0; i < imageUrls.length (); i++){
            item.imageUrl.add ((String) imageUrls.get(1));
          }
          chef.menuItems.add (item);
          menuAdapter menuAdapter = new menuAdapter (Menu.this, chef);
          listView.setAdapter (menuAdapter);
          for(MenuItem itemx: chef.menuItems){
            for(String x: itemx.imageUrl){
              System.out.println("*****  " + x);
            }
          }
        } catch (JSONException e) {
          e.printStackTrace ();
        }
        Toast.makeText (getApplicationContext (), response, Toast.LENGTH_SHORT).show ();
      }
    }, new Response.ErrorListener () {
      @Override
      public void onErrorResponse (VolleyError error) {

      }
    });
    int socketTimeout = 30000;//30 seconds - change to what you want
    RetryPolicy policy = new DefaultRetryPolicy (socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    request.setRetryPolicy(policy);
    queue.add (request);
  }

  public class menuAdapter extends BaseAdapter{
    Context context;
    Chef chef;
    menuAdapter(Context context, Chef chef){
      this.chef = chef;
      this.context = context;
    }

    @Override
    public int getCount () {
      //return chef.menuItems.size ();
      return chef.menuItems.size ();
    }

    @Override
    public Object getItem (int position) {
      return chef.menuItems.get (position);
    }

    @Override
    public long getItemId (int position) {
      return position;
    }

    public View getView (int position, View convertView, ViewGroup parent) {
      MenuItem menuItem = chef.menuItems.get (position);
      View row = null;
      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.adapter_menu_layout, parent, false);
      } else {
        row = convertView;
      }
      com.pkmmte.view.CircularImageView image = (com.pkmmte.view.CircularImageView)row
              .findViewById (R.id.avatar);
      TextView desertName = (TextView)row.findViewById (R.id.desertName);
      TextView desertProducer = (TextView)row.findViewById (R.id.desertProducer);
      TextView desertPrice = (TextView)row.findViewById (R.id.desertPrice);
      ImageView desertImage = (ImageView)row.findViewById (R.id.desertImage);

      try{
        desertName.setText (menuItem.name);
        desertProducer.setText ("By " + menuItem.description);
        desertPrice.setText ("$" + menuItem.price);
//        desertImage.setImageResource (resources[position]);

      }catch (NullPointerException e){
        e.printStackTrace ();
      }
      try {
        Picasso.with (Menu.this).load (menuItem.imageUrl.get (position))
                .error (R.drawable.logo).placeholder (R.drawable.logo)
                .into (desertImage);
      } catch (Exception e) {
        e.printStackTrace ();
      }
      return row;
    }
  }
}