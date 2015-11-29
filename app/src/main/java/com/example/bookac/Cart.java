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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookac.activities.UserHomePage;
import com.example.bookac.singletons.Chef;
import com.example.bookac.singletons.ItemCart;
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
    myGridAdapter adapter = new myGridAdapter (getApplicationContext ());
    gridView.setAdapter (adapter);
    FloatingActionButton fab = (FloatingActionButton) findViewById (R.id.fab);
    fab.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View view) {

      }
    });
  }


  public class myGridAdapter extends BaseAdapter{

    Context context;
    public myGridAdapter(Context context){
      this.context = context;

    }
    @Override
    public int getCount () {
      return ItemCart.INSTANCE.getSize ();
    }

    @Override
    public Object getItem (int position) {
      return ItemCart.INSTANCE.getAllItems ().get (position);
    }

    @Override
    public long getItemId (int position) {
      return position;
    }

    public View getView (int position, View convertView, ViewGroup parent) {
      View row = null;
      MenuItem item = ItemCart.INSTANCE.getAllItems ().get (position);

      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.adapter_for_gridview, parent, false);
      } else {
        row = convertView;
      }
      ImageView mealImage= (ImageView)row.findViewById (R.id.imagegrid);
      TextView mealTitle  = (TextView)row.findViewById (R.id.titleofmeal);
      TextView mealprice = (TextView)row.findViewById (R.id.mealprice);
      mealprice.setText ("$" + item.price + "");
      mealTitle.setText (item.name);
      try {
        Picasso.with (Cart.this).load (item.imageUrl.get (position))
                .error (R.drawable.logo).placeholder (R.drawable.logo)
                .into (mealImage);
      } catch (Exception e) {
        e.printStackTrace ();
      }
      return row;
    }
  }
}
