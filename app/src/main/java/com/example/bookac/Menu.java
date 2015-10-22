package com.example.bookac;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.bookac.singletons.Chef;
import com.example.bookac.singletons.MenuItem;

public class Menu extends AppCompatActivity {
  Chef chef = new Chef ();
  ListView listView;
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_menu);
    Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
    setSupportActionBar (toolbar);
    getSupportActionBar ().setTitle ("");
    getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
    final Drawable upArrow = getResources ().getDrawable (R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    upArrow.setColorFilter (getResources ().getColor (android.R.color.white), PorterDuff.Mode.SRC_ATOP);
    getSupportActionBar ().setHomeAsUpIndicator (upArrow);
    listView = (ListView)findViewById (R.id.menu_items);

  }

  public class menuAdapter extends BaseAdapter{
    Context context;
    Chef chef;
    menuAdapter(Context context, Chef chef){
      this.context = context;
      this.chef = chef;
    }

    @Override
    public int getCount () {
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
    MenuItem menuItem = new MenuItem ();
    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
      return null;
    }
  }

}
