package com.example.bookac.singletons;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.bookac.Menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aliyuolalekan on 9/26/15.
 */
public class User {
  public static ArrayList<Chef> myChef;
  public static String whatever = "";
  public static  String imageUrl = null;
  public static String name = "";
  public static String firstName = "";
  public static String lastName = "";
  public static String token = null;
  public static  double longitude = 0;
  public static  double latitude = 0;
  public static String uid = "";
  private static String myPrefs = "trvYJ001";
  private static String myOtherpref = "yogaOne";
  public static UserCart cart ;

  public static void saveDB(String id, String value, Context context) {
    try {
      context.getSharedPreferences(myPrefs, Activity.MODE_PRIVATE).edit().putString(id, value).apply();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String getDB(Context context, String id, String init) {
    return context.getSharedPreferences(myPrefs, Activity.MODE_PRIVATE).getString(id, init);
  }

  public static void saveDouble(String id, float value, Context context){
    try {
      context.getSharedPreferences(myPrefs, Activity.MODE_PRIVATE).edit().putFloat (id, value) .apply();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void saveString(String id, String value, Context context){
    try {
      context.getSharedPreferences(myPrefs, Activity.MODE_PRIVATE).edit().putString (id, value) .apply();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static float getDouble(Context context, String id, float init){
    return context.getSharedPreferences(myPrefs, Activity.MODE_PRIVATE).getFloat (id, init);
  }

  public static String getString(Context context, String id, String init){
    return context.getSharedPreferences(myPrefs, Activity.MODE_PRIVATE).getString (id, init);
  }
}
