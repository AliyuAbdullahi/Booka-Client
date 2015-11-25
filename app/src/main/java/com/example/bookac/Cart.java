package com.example.bookac;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.bookac.activities.UserHomePage;
import com.example.bookac.singletons.Chef;
import com.example.bookac.singletons.MenuItem;
import com.example.bookac.singletons.UserCart;
import com.squareup.picasso.Picasso;

public class Cart extends AppCompatActivity {
  UserCart cart;
  GridView gridView;
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_cart);
    Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
    setSupportActionBar (toolbar);
    gridView = (GridView)findViewById (R.id.grid_view);

    FloatingActionButton fab = (FloatingActionButton) findViewById (R.id.fab);
    fab.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View view) {
        Snackbar.make (view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction ("Action", null).show ();
      }
    });
  }


  public class myGridAdapter extends BaseAdapter{
    UserCart items;
    Context context;
    public myGridAdapter(Context context, UserCart items){
      this.context = context;
      this.items = items;
    }
    @Override
    public int getCount () {
      return items.itemsToCart.size ();
    }

    @Override
    public Object getItem (int position) {
      return items.itemsToCart.get (position);
    }

    @Override
    public long getItemId (int position) {
      return position;
    }

    public View getView (int position, View convertView, ViewGroup parent) {
      View row = null;
      MenuItem item = items.itemsToCart.get (position);

      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.adapter_for_gridview, parent, false);
      } else {
        row = convertView;
      }
      com.pkmmte.view.CircularImageView image = (com.pkmmte.view.CircularImageView)row
              .findViewById (R.id.avatar);
      TextView chefname = (TextView)row.findViewById (R.id.chefname);
      TextView chefaddress = (TextView)row.findViewById (R.id.chefaddress);
      TextView chefdistance = (TextView)row.findViewById (R.id.chefdistance);
      return row;
    }
  }
}
