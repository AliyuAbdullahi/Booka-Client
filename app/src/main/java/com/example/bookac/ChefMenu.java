package com.example.bookac;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ChefMenu extends AppCompatActivity {

  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_chef_menu);
    Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
    setSupportActionBar (toolbar);
    getSupportActionBar ().setTitle ("");
    getSupportActionBar ().setDisplayHomeAsUpEnabled (true);

    final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    upArrow.setColorFilter (getResources ().getColor (android.R.color.white), PorterDuff.Mode.SRC_ATOP);
    getSupportActionBar().setHomeAsUpIndicator (upArrow);


  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater ().inflate (R.menu.chef_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    return super.onOptionsItemSelected (item);
  }
}
