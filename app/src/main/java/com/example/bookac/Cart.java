package com.example.bookac;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookac.activities.Payment;
import com.example.bookac.activities.UserHomePage;
import com.example.bookac.activities.UserMenu;
import com.example.bookac.fragments.NavigationFragment;
import com.example.bookac.singletons.Chef;
import com.example.bookac.singletons.ItemCart;
import com.example.bookac.singletons.MenuItem;
import com.example.bookac.singletons.User;
import com.example.bookac.singletons.UserCart;
import com.example.bookac.tools.PicassoImageLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class Cart extends AppCompatActivity {
  UserCart cart;
  private TextView noItemAvailble;
  GridView gridView;
  Toolbar toolbar;
  NavigationFragment navigationFragment;
  DrawerLayout mdrawerLayout;

  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_cart);

    toolbar = (Toolbar) findViewById (R.id.toolbar_cart);
    toolbar.setTitle ("");
    mdrawerLayout = (DrawerLayout)findViewById (R.id.drawer_layout_cart);
    setSupportActionBar (toolbar);

    navigationFragment = (NavigationFragment)getSupportFragmentManager ().findFragmentById (R.id.navigation_fragment_cart);

    navigationFragment.setUp (R.id.navigation_fragment, mdrawerLayout, toolbar);
    gridView = (GridView)findViewById (R.id.grid_view);
    noItemAvailble = (TextView)findViewById (R.id.noItemAvailable);
    final MyOwnAdapter adapter = new MyOwnAdapter (Cart.this);
    gridView.setNumColumns (2);
    gridView.setAdapter (adapter);

    if(ItemCart.INSTANCE.itemIsinCart){
      noItemAvailble.setVisibility (View.INVISIBLE);
    }
    else {
      noItemAvailble.setVisibility (View.VISIBLE);
    }

    com.pkmmte.view.CircularImageView userImage = (com.pkmmte.view.CircularImageView)
            findViewById (R.id.myAvartar);
    loadImage (Cart.this, userImage, User.getContent (Cart.this, "photo", "photo"));

    /**
     * The floating action button is used for deleting all items
     */
    final FloatingActionButton fab = (FloatingActionButton) findViewById (R.id.fab);
    fab.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder (Cart.this);
        if(ItemCart.INSTANCE.getAllItems ().size () <= 0){
          builder.setTitle ("No Item in Cart");
          builder.setPositiveButton ("Ok", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
              dialog.cancel ();
            }
          });
          builder.show ();
        }
        else {
          builder.setTitle ("Delete All Items?");
          builder.setPositiveButton ("Yes", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
              boolean deleteAll = ItemCart.INSTANCE.deleteAll ();
              if (deleteAll) {
                ItemCart.INSTANCE.itemIsinCart = false;
                noItemAvailble.setVisibility (View.VISIBLE);
                adapter.notifyDataSetChanged ();
                Toast.makeText (Cart.this, "All Items deleted", Toast.LENGTH_SHORT).show ();
              }
            }
          });
          builder.setNegativeButton ("No", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
              dialog.cancel ();
            }
          });
          builder.show ();
        }
      }
    });
  }

  public void loadImage(final Context context, final ImageView view, final String url){

    try {
      Picasso.with (Cart.this).load (url)
              .error (R.drawable.logo).placeholder (R.drawable.logo)
              .into (view, new Callback () {
                @Override
                public void onSuccess () {

                }
                @Override
                public void onError () {
                  loadImage (context, view, url);
                }
              });
    } catch (Exception e) {
      e.printStackTrace ();
    }
  }

  private class MyOwnAdapter extends BaseAdapter{
    Context context;

    public MyOwnAdapter(Context context){
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

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
      final MenuItem menuItem = ItemCart.INSTANCE.getItems (position);
      View row = null;
      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.adapter_for_gridview, parent, false);
      } else {
        row = convertView;
      }
      LinearLayout checkout = (LinearLayout)row.findViewById (R.id.checkoutCart);
      String mBoundString;
      ImageView removeFromCart = (ImageView)row.findViewById (R.id.removeFromCartXC);
      TextView nameOfMeal = (TextView)row.findViewById (R.id.nameOfMealCartX);
      TextView price = (TextView)row.findViewById (R.id.priceCartMM);
      TextView cookingTime = (TextView)row.findViewById (R.id.cookingTimeCartXVB);
      TextView mealType = (TextView)row.findViewById (R.id.mealTypeXCartM);
      ImageView desertImage = (ImageView)row.findViewById (R.id.desertCartMM);
      TextView description = (TextView)row.findViewById (R.id.descriptionCart);
      nameOfMeal.setText (menuItem.name);
      price.setText ("N" + menuItem.price);
      try {
        cookingTime.setText (menuItem.doneTime + "mins");
      }catch (Exception e){e.printStackTrace ();}
      description.setText (menuItem.description);
      PicassoImageLoader loader = new PicassoImageLoader (Cart.this);
      loader.loadImage (desertImage, menuItem.photo);
      return row;
    }
  }
}
