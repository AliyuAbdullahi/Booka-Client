package com.example.bookac.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.example.bookac.singletons.FaceBookToken;
import com.example.bookac.singletons.GoogleApiInstance;
import com.example.bookac.singletons.User;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.Arrays;
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


  String idFacebook;
  String nameFacebook;
  String picturefacebook;


  private TextView info;

  CallbackManager callbackManager;

  AccessTokenTracker accessTokenTracker;

  AccessToken accessToken;

  String Id;
  String personName;
  String email;
  Button googleLogin;
  LoginButton loginButton;
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    FacebookSdk.sdkInitialize (getApplicationContext ());

    setContentView (R.layout.activity_google_facebook);

    if(!User.getContent (GoogleFacebook.this, "token", "token").equals ("")){
      Intent intent = new Intent (GoogleFacebook.this, LoginRedirect.class);
      startActivity (intent);
    }

    info = (TextView)findViewById(R.id.info);
    final LoginButton loginButton = (LoginButton)findViewById (R.id.login_button);
    callbackManager = CallbackManager.Factory.create();
    accessTokenTracker = new AccessTokenTracker () {
      @Override
      protected void onCurrentAccessTokenChanged(
              AccessToken oldAccessToken,
              AccessToken currentAccessToken) {
        // Set the access token using
        // currentAccessToken when it's loaded or set.
      }
    };

    // If the access token is available already assign it.
    accessToken = AccessToken.getCurrentAccessToken ();
    loginButton.setReadPermissions("public_profile, user_friends");
    loginButton.registerCallback (callbackManager, new FacebookCallback<LoginResult> () {
      @Override
      public void onSuccess (final LoginResult loginResult) {
        info.setText (
                "User ID: "
                        + loginResult.getAccessToken ().getUserId ()
                        + "\n" +
                        "Auth Token: "
                        + loginResult.getAccessToken ().getToken ());
        User.saveContent ("facebookToken", loginResult.getAccessToken ().getToken (), GoogleFacebook.this, "facebookToken");
        FaceBookToken.INSTANCE.setToken (loginResult.getAccessToken ().getToken () + "");
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                  @Override
                  public void onCompleted(JSONObject object,GraphResponse response) {
                    if(response != null){
                      try {
                        JSONObject data = response.getJSONObject ();
                        Log.e ("data: ", data + "");
                        idFacebook = data.getString ("id");
                        logger.Log.e ("id", idFacebook);
                        nameFacebook = data.getString ("name");
                        Log.e ("name", nameFacebook);
                        JSONObject picutre = data.getJSONObject ("picture");
                        JSONObject innerData = picutre.getJSONObject ("data");
                        picturefacebook = innerData.getString ("url");
                        Log.e ("All:", "Name: " + nameFacebook + "\nId " + idFacebook + "\npicture: " + picturefacebook + "\n"
                                + "Token: " + User.getContent (GoogleFacebook.this, "facebookToken", "facebookToken"));
                        String fbToken = User.getContent (GoogleFacebook.this, "facebookToken", "facebookToken");
                        makeVolleyRequest (fbToken, nameFacebook, "email@email.com", idFacebook, picturefacebook);
//                        if(data.has ("picture")){
//                          Log.e("ImageUrl", data.getString ("picture").replace ("\\", ""));
//                          Toast.makeText (getApplicationContext (), data.getString ("picture"), Toast.LENGTH_LONG).show ();
//                        }
//                        Log.e ("data: ", response+"");
                      } catch(Exception ex) {
                        ex.printStackTrace();

                      }
                    }

                  }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,id,email,gender,picture,birthday");
        request.setParameters (parameters);
        request.executeAsync();

      }

      @Override
      public void onCancel () {

      }

      @Override
      public void onError (FacebookException error) {

      }
    });


    //google login
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
          if(mGoogleConnectionResult != null){
            resolveSignInError ();

          }
        else if (mGoogleApiClient.isConnected()) {
            loginAndGetToken ();
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
        mGoogleApiClient.connect ();
      }
    }
  }

  @Override
  public void onConnected (Bundle bundle) {
    try {
      loginAndGetToken ();
    }catch (Exception e){
      e.printStackTrace ();
    }
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
        User.saveContent ("personNameDb", personName, GoogleFacebook.this, "personName");
        User.saveContent ("personIdDb", Id,GoogleFacebook.this, "personId");
        User.saveContent ("email", email, GoogleFacebook.this, "email");
        User.saveContent ("photo", photoUrl, GoogleFacebook.this, "photo");

        loginAndGetToken ();
      } catch (JSONException e) {
        e.printStackTrace ();
      }
    }
//    makeVolleyRequest (token, personName, email, Id, photoUrl);

     GoogleApiInstance.mGoogleApiClient = this.mGoogleApiClient;
  }

  @Override
  public void onConnectionSuspended (int i) {
    //nothing is gonna happen
    mGoogleApiClient.connect();
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
          if(mGoogleApiClient.isConnected ())
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


        } else if (errorMessage != null) {

          Toast.makeText (getApplicationContext (),errorMessage,Toast.LENGTH_SHORT).show ();
        }
      }
    };
    task.execute ();
  }

  @Override
  protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    callbackManager.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK){
      Intent intent = new Intent (GoogleFacebook.this, LoginRedirect.class);
      startActivity (intent);
    }
    if (resultCode != RESULT_OK) {
      mGoogleLoginClicked = false;
    }
    mGoogleIntentInProgress = false;
    if(data != null)
      System.out.println ("data" + data);

    if (!mGoogleApiClient.isConnecting()) {
      mGoogleApiClient.connect();
    }
    if(mGoogleApiClient.isConnected ()){

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
        Toast.makeText (getApplicationContext (), "Success!", Toast.LENGTH_LONG).show ();
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
        params.put ("firstname", name.split (" ")[1]);
        params.put ("lastname", name.split (" ")[0]);
        params.put ("email", emailNew);
        params.put ("provider", "facebook");
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

  @Override
  public void onDestroy() {
    super.onDestroy ();
//    accessTokenTracker.stopTracking();
  }
//  @Override
//  protected void onResume() {
//    super.onResume();
//
//    // Logs 'install' and 'app activate' App Events.
//    AppEventsLogger.activateApp (this);
//  }
//
//  @Override
//  protected void onPause() {
//    super.onPause();
//
//    // Logs 'app deactivate' App Event.
//    AppEventsLogger.deactivateApp(this);
//  }

  @Override
  protected void onStart() {
    super.onStart();
    mGoogleApiClient.connect();
  }
  @Override
  protected void onStop() {
    super.onStop();

    if (mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }
  }
}
