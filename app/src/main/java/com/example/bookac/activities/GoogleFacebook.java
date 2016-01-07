package com.example.bookac.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.example.bookac.R;
import com.example.bookac.constants.Constants;
import com.example.bookac.singletons.GoogleApiInstance;
import com.example.bookac.singletons.User;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GoogleFacebook extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{
  private GoogleApiClient mGoogleApiClient;
  public static final int RC_GOOGLE_LOGIN = 1;
  private boolean mGoogleIntentInProgress;
  private boolean mGoogleLoginClicked;
  private ImageButton mGoogleLoginButton;
  private String TAG = "google login";
  private ProgressDialog mAuthProgressDialog;
  private ConnectionResult mGoogleConnectionResult;
  String photoUrl;
  String token = null;
  String Id;
  String personName;
  String email;
  Button googleLogin;
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_google_facebook);
    mAuthProgressDialog = new ProgressDialog (GoogleFacebook.this);
    mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks (this)
            .addOnConnectionFailedListener (this)
            .addApi (Plus.API)
            .addScope (Plus.SCOPE_PLUS_LOGIN)
            .build ();
    googleLogin = (Button)findViewById (R.id.loginGoogle);
    googleLogin.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        Toast.makeText (getApplicationContext (), "Clicked", Toast.LENGTH_SHORT).show ();
        if (!mGoogleApiClient.isConnecting())
          if(mGoogleConnectionResult != null)
            resolveSignInError ();
        else if (mGoogleApiClient.isConnected()) {
          final ProgressDialog dialog = ProgressDialog.show(GoogleFacebook.this, "", "Syncing...",
                  true);
          dialog.show ();
          android.os.Handler handler = new android.os.Handler ();
          handler.postDelayed (new Runnable () {
            public void run () {
              dialog.dismiss ();
              Toast.makeText (getApplicationContext (), "Google is Connected!", Toast.LENGTH_SHORT).show ();
              loginAndGetToken ();
            }
          }, 2000);

        } else {
          Toast.makeText (getApplicationContext (), "is it taking too long?\nRestart executer and sync your canlendar", Toast.LENGTH_LONG).show ();
          Log.d(TAG, "Trying to connect to Google API");
          mGoogleApiClient.connect();
        }
      }
    });
  }

  private void resolveSignInError() {
    if (mGoogleConnectionResult.hasResolution()) {
      try {
        mGoogleIntentInProgress = true;
        mGoogleConnectionResult.startResolutionForResult(this, RC_GOOGLE_LOGIN);
      } catch (IntentSender.SendIntentException e) {
        mGoogleIntentInProgress = false;
        mGoogleApiClient.connect();
      }
    }
  }
  @Override
  public void onConnected (Bundle bundle) {
    if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
      try {
      Person currentPerson = Plus.PeopleApi.getCurrentPerson (mGoogleApiClient);
      Id = currentPerson.getId ();
      personName = currentPerson.getDisplayName ();
      Person.Image personPhoto = currentPerson.getImage ();
      String personGooglePlusProfile = currentPerson.getUrl ();
      Log.e ("personPhoto", personPhoto+"");
      Log.e("PersonProfile", personGooglePlusProfile);
      email = Plus.AccountApi.getAccountName (mGoogleApiClient);

        JSONObject photo = new JSONObject (String.valueOf (personPhoto));
        photoUrl = photo.getString ("url");
        loginAndGetToken ();
        User.saveContent ("personNameDb", personName, GoogleFacebook.this, "personName");
        User.saveContent ("personIdDb", Id,GoogleFacebook.this, "personId");
        User.saveContent ("email", email, GoogleFacebook.this, "email");
        User.saveContent ("photo", photoUrl, GoogleFacebook.this, "photo");

        Toast.makeText (getApplicationContext (), personName + " " + Id, Toast.LENGTH_LONG ).show ();

      } catch (JSONException e) {
        e.printStackTrace ();
      }
    }
    //makeVolleyRequest (token, personName, email, Id, photoUrl);

    // GoogleApiInstance.mGoogleApiClient = this.mGoogleApiClient;
  }

  @Override
  public void onConnectionSuspended (int i) {
    //nothing is gonna happen
  }

  @Override
  public void onClick (View v) {
//    switch (v.getId ()) {
//      case R.id.loginGoogle:
//        Toast.makeText (getApplicationContext (), "CLicked", Toast.LENGTH_SHORT).show ();
//        resolveSignInError ();
//    }

  }

  @Override
  public void onConnectionFailed (ConnectionResult result) {
    mGoogleConnectionResult = result;

    if (mGoogleLoginClicked) {
      resolveSignInError();
    } else {
      Toast.makeText (getApplicationContext (), "failed", Toast.LENGTH_SHORT).show ();
      Log.e(TAG, result.toString());
    }
  }

  private void loginAndGetToken() {
    try{
      mAuthProgressDialog.show ();
    }
    catch (Exception e){
      e.printStackTrace ();
    }

    AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
      String errorMessage = null;
      String profileObjecUrl = "https://www.googleapis.com/plus/v1/people/{716723559238-uo4jah2kqd7e73438g6u12v5lk7u68eu.apps.googleusercontent.com}?key={AIzaSyD1A5VjvibIN7wEb8tNhMHrz7RM8xXlcjY}";
      @Override
      protected String doInBackground(Void... params) {
        String clientID = "716723559238-uo4jah2kqd7e73438g6u12v5lk7u68eu.apps.googleusercontent.com";
        String uglyScope = "oauth2:server:client_id:{"+clientID +"}.apps.googleusercontent.com"+
                ":api_scope:https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/plus.login";
        String mycoolscope = "oauth2:https://www.googleapis.com/auth/calendar https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile";
        try {
          String scope = mycoolscope;
          token = GoogleAuthUtil.getToken (GoogleFacebook.this, Plus.AccountApi.getAccountName (mGoogleApiClient), scope);
        } catch (IOException transientEx) {
          Log.e (TAG, "Error authenticating with Google: " + transientEx);
          errorMessage = "Network error: " + transientEx.getMessage();
        } catch (UserRecoverableAuthException e) {
          Log.w(TAG, "Recoverable Google OAuth error: " + e.toString());
                    /* We probably need to ask for permissions, so start the intent if there is none pending */
          if (!mGoogleIntentInProgress) {
            mGoogleIntentInProgress = true;
            Intent recover = e.getIntent();
            startActivityForResult(recover, RC_GOOGLE_LOGIN);
          }
        } catch (GoogleAuthException authEx) {
          Log.e(TAG, "Error authenticating with Google: " + authEx.getMessage(), authEx);
          errorMessage = "Error authenticating with Google: " + authEx.getMessage();
        }
        return token;
      }

      @Override
      protected void onPostExecute(final String token) {
        mGoogleLoginClicked = false;
        mAuthProgressDialog.hide();
        if (token != null) {
          Log.v ("token", token);
          User.saveContent ("token", token, GoogleFacebook.this, "token");
          Intent intent = new Intent (GoogleFacebook.this, LoginRedirect.class);
          startActivity (intent);
          Toast.makeText (getApplicationContext (),"Token" + token, Toast.LENGTH_SHORT).show ();

        } else if (errorMessage != null) {

          Toast.makeText (getApplicationContext (),errorMessage,Toast.LENGTH_SHORT).show ();
        }
      }
    };
    task.execute ();
  }

  @Override
  protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    if (resultCode != RESULT_OK) {
      mGoogleLoginClicked = false;
    }
    mGoogleIntentInProgress = false;
    if(data != null)
      System.out.println("data" + data);

    if (!mGoogleApiClient.isConnecting()) {
      mGoogleApiClient.connect();
    }
  }

  //sign user out...
  public void signOut(){
    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
    // Our sample has caches no user data from Google+, however we
    // would normally register a callback on revokeAccessAndDisconnect
    // to delete user data so that we comply with Google developer
    // policies.
    Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
    mGoogleApiClient.connect();
  }

  public void makeVolleyRequest(final String newToken, final String name, final String emailNew, final String id, final String personPhotoUrl ){

    RequestQueue queue = Volley.newRequestQueue (GoogleFacebook.this);
    StringRequest request = new StringRequest (Request.Method.POST, Constants.LOGIN_URL, new Response.Listener<String> () {
      @Override
      public void onResponse (String response) {
        Toast.makeText (getApplicationContext (), response, Toast.LENGTH_LONG).show ();
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
        params.put ("lastname", personName.substring (personName.indexOf (" "), personName.length ()));
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
    request.setRetryPolicy (policy);
    queue.add (request);
  }
}
