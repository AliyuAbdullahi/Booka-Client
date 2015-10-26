package logger;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by aliyuolalekan on 25/10/2015.
 */
public class Toaster extends Application {
  public Toaster(){

  }
  public void longToast(String message){
    Toast.makeText (getApplicationContext (), message, Toast.LENGTH_LONG).show ();
  }
  public void shortToast(String message){
    Toast.makeText (getApplicationContext (), message, Toast.LENGTH_SHORT).show ();
  }
}
