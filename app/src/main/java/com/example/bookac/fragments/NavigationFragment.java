package com.example.bookac.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bookac.Cart;
import com.example.bookac.GetPeopleAround;
import com.example.bookac.R;
import com.example.bookac.activities.About;
import com.example.bookac.activities.Help;
import com.example.bookac.activities.Settings;
import com.example.bookac.activities.UserHomePage;
import com.example.bookac.singletons.Chef;
import com.example.bookac.singletons.User;
import com.example.bookac.tools.PicassoImageLoader;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * Created by aliyuolalekan on 25/11/2015.
 */
public class NavigationFragment extends Fragment {
  View view;
  ListView listView;
  private TextView usernameOfsideNav;
  private TextView lastNameOfSideNav;
  private ImageView userImage;
  private ActionBarDrawerToggle toggle;
  private DrawerLayout mdrawerLayout;

  @Nullable
  @Override
  public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate (R.layout.fragment_navigation_drawer, container, false);
    listView = (ListView)view.findViewById (R.id.sideNavList);

    userImage = (ImageView)view.findViewById (R.id.sideNavAvatar);
    PicassoImageLoader loader = new PicassoImageLoader (getActivity ());
    try{
      loader.loadImage (userImage, User.getContent(getActivity (), "photo", "photo"));
      usernameOfsideNav = (TextView)view.findViewById (R.id.userfirstnameforsidenav);
      lastNameOfSideNav = (TextView)view.findViewById (R.id.userlastnameforsidenav);
      usernameOfsideNav.setText (User.getContent (getActivity (), "firstname", "firstname"));
      lastNameOfSideNav.setText (User.getContent (getActivity (), "lastname", "lastname"));
    }catch (NullPointerException e){e.printStackTrace ();}

    SideNavListAdapter adapter = new SideNavListAdapter (getContext ());
    listView.setAdapter (adapter);
    listView.setOnItemClickListener (new AdapterView.OnItemClickListener () {
      @Override
      public void onItemClick (AdapterView<?> parent, View view, int position, long id) {

        switch (position){
          case 0:
            Intent cart = new Intent (getActivity (), Cart.class);
            startActivity (cart);
            break;
          case 1:
            Intent settings = new Intent (getActivity (), Settings.class);
            startActivity (settings);
            break;
          case 2:
            Intent about = new Intent (getActivity (), About.class);
            startActivity (about);
            break;
          case 3:
            Intent help = new Intent (getActivity (), Help.class);
            startActivity (help);
            break;
        }
      }
    });
    return view;
  }

  public  class ViewAdapter extends BaseAdapter{

    @Override
    public int getCount () {
      return 0;
    }

    @Override
    public Object getItem (int position) {
      return null;
    }

    @Override
    public long getItemId (int position) {
      return 0;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
      return null;
    }
  }

  public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar){
    view = getActivity ().findViewById (fragmentId);
    mdrawerLayout = drawerLayout;
    toggle = new ActionBarDrawerToggle (getActivity (), mdrawerLayout, toolbar,R.string.drawer_opened,R.string.drawer_closed){
      @Override
      public void onDrawerOpened (View drawerView) {
        super.onDrawerOpened (drawerView);
        getActivity ().invalidateOptionsMenu ();
        mdrawerLayout.bringToFront ();
      }

      @Override
      public void onDrawerClosed (View drawerView) {
        super.onDrawerClosed (drawerView);
        getActivity ().invalidateOptionsMenu ();
      }

      @Override
      public void syncState () {
        super.syncState ();

      }

      @Override
      public void onDrawerSlide (View drawerView, float slideOffset) {
        super.onDrawerSlide (drawerView, slideOffset);
        if(slideOffset < 0.6)
          toolbar.setAlpha (1-slideOffset);
      }
    };

    mdrawerLayout.setDrawerListener (toggle);
    mdrawerLayout.post (new Runnable () {
      @Override
      public void run () {
        toggle.syncState ();
      }
    });
  }

  public class SideNavListAdapter extends BaseAdapter{
    int[] images = new int[]{R.mipmap.ic_shopping_cart_black_48dp, R.mipmap.ic_settings_black_48dp, R.mipmap.ic_info_outline_black_48dp, R.mipmap.ic_help_black_48dp };
    String[] titles = new String []{"Cart", "Settings", "About", "Help"};
    Context context;
    public SideNavListAdapter(Context context){
      this.context = context;
    }
    @Override
    public int getCount () {
      return images.length;
    }

    @Override
    public Object getItem (int position) {
      return images[position];
    }

    @Override
    public long getItemId (int position) {
      return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
      View row = null;
      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.sidenavlist, parent, false);
      } else {
        row = convertView;
      }

      TextView title = (TextView)row.findViewById (R.id.sideNavItemTitle);
      title.setText (titles[position]);
      ImageView sideNavImage = (ImageView)row.findViewById (R.id.sideNavItemImage);
      int color = Color.parseColor ("#AE6118"); //The color u want
      sideNavImage.setColorFilter(color);
      sideNavImage.setImageResource (images[position]);
      return row;
    }
  }
}
