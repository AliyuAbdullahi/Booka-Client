package com.example.bookac.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookac.GetPeopleAround;
import com.example.bookac.R;
import com.example.bookac.constants.Constants;
import com.example.bookac.singletons.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginRedirect extends AppCompatActivity {

  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_login_redirect);


    Toast.makeText (LoginRedirect.this, "Sleeping", Toast.LENGTH_SHORT).show ();
    try {
      Thread.sleep (5000);

    } catch (InterruptedException e) {
      e.printStackTrace ();
    }
    Toast.makeText (LoginRedirect.this, "Done!", Toast.LENGTH_SHORT).show ();
    String token = User.getContent (LoginRedirect.this, "token", "token");
    String personName = User.getContent (LoginRedirect.this, "personNameDb", "personName");
    String email = User.getContent (LoginRedirect.this, "email", "email");
    String uid = User.getContent (LoginRedirect.this, "personIdDb", "personId");
    String photo = User.getContent (LoginRedirect.this, "photo", "photo");
    makeVolleyRequest (token, personName, email, uid, photo);
  }

  public void makeVolleyRequest(final String newToken, final String name, final String emailNew, final String id, final String personPhotoUrl ){

    RequestQueue queue = Volley.newRequestQueue (LoginRedirect.this);
    StringRequest request = new StringRequest (Request.Method.POST, Constants.LOGIN_URL, new Response.Listener<String> () {
      @Override
      public void onResponse (String response) {
        Log.e ("Response: ", response + "");
        try{
          JSONObject responseObject = new JSONObject (response);
          JSONObject profile = responseObject.getJSONObject ("profile");
          User.saveContent ("photo", profile.getString ("photo").replace ("\\",""), LoginRedirect.this, "photo");
          User.saveContent ("firstname", profile.getString ("firstname"), LoginRedirect.this, "firstname" );
          User.saveContent ("lastname", profile.getString ("lastname"), LoginRedirect.this, "lastname" );
          User.saveContent ("token", profile.getString ("token"), LoginRedirect.this, "token" );
          User.saveContent ("uid", profile.getString ("uid"), LoginRedirect.this, "uid" );
        }catch (JSONException e){e.printStackTrace ();
        }
        Intent gotToGetPeopleAround = new Intent (LoginRedirect.this, GetPeopleAround.class);
        startActivity (gotToGetPeopleAround);
      }
    }, new Response.ErrorListener () {
      @Override
      public void onErrorResponse (VolleyError error) {
        Toast.makeText(getApplicationContext (), error+"", Toast.LENGTH_LONG).show ();

      }
    }){
      @Override
      protected Map<String, String> getParams () throws AuthFailureError {
        super.getParams ();
        Map<String,String> params = new HashMap<String, String> ();
        params.put ("token", newToken );
        params.put ("uid", id);
        params.put ("firstname", name.substring (0, name.indexOf (" ")));
        params.put ("lastname", name.substring (name.indexOf (" "), name.length ()));
        params.put ("email", emailNew);
        params.put ("provider", "google");
        String pattern = personPhotoUrl.replace ("\\","");
        params.put ("photo", pattern);
        return params;
      }

      @Override
      public Map<String, String> getHeaders () throws AuthFailureError {
        super.getHeaders ();
        Map<String,String> params = new HashMap<String, String>();
        params.put("Content-Type","application/x-www-form-urlencoded");
        return params;
      }
    };
    int socketTimeout = 30000;//30 seconds - change to what you want
    RetryPolicy policy = new DefaultRetryPolicy (socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    request.setRetryPolicy(policy);
    queue.add (request);
  }

}
