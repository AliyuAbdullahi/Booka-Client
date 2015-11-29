package com.example.bookac;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookac.activities.UserHomePage;
import com.example.bookac.singletons.Chef;
import com.example.bookac.singletons.ItemCart;
import com.example.bookac.singletons.MenuItem;
import com.example.bookac.singletons.User;
import com.example.bookac.singletons.UserCart;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class Cart extends AppCompatActivity {
  UserCart cart;
  private TextView noItemAvailble;
  GridView gridView;
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_cart);
    noItemAvailble = (TextView)findViewById (R.id.noItemAvailable);

    if(ItemCart.INSTANCE.itemIsinCart){
      noItemAvailble.setVisibility (View.INVISIBLE);
    }
    else {
      noItemAvailble.setVisibility (View.VISIBLE);
    }

    Toolbar toolbar = (Toolbar) findViewById (R.id.toolbarcart);
    setSupportActionBar (toolbar);
    com.pkmmte.view.CircularImageView userImage = (com.pkmmte.view.CircularImageView)
            findViewById (R.id.myAvartar);
    loadImage (Cart.this, userImage, User.imageUrl);

    gridView = (GridView)findViewById (R.id.grid_view);

    final myGridAdapter adapter = new myGridAdapter (getApplicationContext ());

    gridView.setAdapter (adapter);

    /**
     * The floating action button is used for deleting all items
     */
    FloatingActionButton fab = (FloatingActionButton) findViewById (R.id.fab);
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

      loadImage (Cart.this, mealImage, item.imageUrl.get (0));
      return row;
    }
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
}
