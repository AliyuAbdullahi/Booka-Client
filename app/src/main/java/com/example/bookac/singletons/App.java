package com.example.bookac.singletons;

import android.app.Application;
import android.content.Context;

/**
 * Created by aliyuolalekan on 11/12/2015.
 */
public class App extends Application {
  private static App instance;
  private static Context context;

  @Override
  public void onCreate () {
    super.onCreate ();
    instance = this;
    context = getApplicationContext ();
    setAppContext(context);

  }
  public static Context getAppContext(){
    return context;
  }
  private void setAppContext(Context context){
    context = context;
  }
}
