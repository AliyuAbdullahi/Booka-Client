package com.example.bookac.activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bookac.R;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.nearby.messages.internal.RegisterStatusCallbackRequest;
import com.google.android.gms.plus.Plus;

import java.io.IOException;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

public class GoogleFacebookLogin extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

  private static final String TAG = "signin1";
  static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
  static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
  private static final int RC_SIGN_IN = 0;
  private boolean mIntentInProgress;
  private PendingIntent mSignInIntent;
  public static final Parcelable.Creator<GoogleSignInAccount> CREATOR = null;
  String mEmail; // Received from newChooseAccountIntent(); passed to getToken()
  String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
  private final static String BOOKS_API_SCOPE
          = "https://www.googleapis.com/auth/books";
  private final static String GPLUS_SCOPE
          = "https://www.googleapis.com/auth/plus.login";
  private final static String mScopes
          = "oauth2:" + BOOKS_API_SCOPE + " " + GPLUS_SCOPE;

  private boolean signedInUser;
  private static final int SIGNED_IN = 0;
  private static final int STATE_IN_PROGRESS = 2;

  private static final int STATE_SIGNING_IN = 1;

  Button googleLogin;
  private int mSignInProgress;

  private static final int DIALOG_PLAY_SERVICES_ERROR = 0;

  GoogleApiClient mGoogleApiClient;

  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_google_facebook_login);
    mGoogleApiClient = buildApiClient ();
    googleLogin = (Button)findViewById (R.id.googleLogin);
    googleLogin.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        pickUserAccount ();
      }
    });
  }

  private void googlePlusLogin () {

    if (!mGoogleApiClient.isConnecting ()) {

      signedInUser = true;

      resolveSignInError ();

    }
  }


  private void resolveSignInError () {
    if (mSignInIntent != null) {
      // We have an intent which will allow our user to sign in or
      // resolve an error.  For example if the user needs to
      // select an account to sign in with, or if they need to consent
      // to the permissions your app is requesting.

      try {
        // Send the pending intent that we stored on the most recent
        // OnConnectionFailed callback.  This will allow the user to
        // resolve the error currently preventing our connection to
        // Google Play services.
        mSignInProgress = STATE_IN_PROGRESS;
        startIntentSenderForResult (mSignInIntent.getIntentSender (),
                RC_SIGN_IN, null, 0, 0, 0);
      } catch (IntentSender.SendIntentException e) {
        Log.i (TAG, "Sign in intent could not be sent: "
                + e.getLocalizedMessage ());
        // The intent was canceled before it was sent.  Attempt to connect to
        // get an updated ConnectionResult.
        mSignInProgress = STATE_SIGNING_IN;
        mGoogleApiClient.connect ();
      }
    } else {
      // Google Play services wasn't able to provide an intent for some
      // error types, so we show the default Google Play services error
      // dialog which may still start an intent on our behalf if the
      // user can resolve the issue.
      showDialog (DIALOG_PLAY_SERVICES_ERROR);
    }
  }

  private void googlePlusLogout () {

    if (mGoogleApiClient.isConnected ()) {

      Plus.AccountApi.clearDefaultAccount (mGoogleApiClient);

      mGoogleApiClient.disconnect ();

      mGoogleApiClient.connect ();

      // updateProfile(false);

    }

  }



  @Override
  protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    super.onActivityResult (requestCode, resultCode, data);

    switch (requestCode) {
//      case RC_SIGN_IN:
//        if (resultCode == RESULT_OK) {
//
////          Toast.makeText (getApplicationContext (), "Signed In", Toast.LENGTH_SHORT).show ();
//          signedInUser = false;
//        }
//        mIntentInProgress = false;
//
//        if (!mGoogleApiClient.isConnecting ()) {
//          mGoogleApiClient.connect ();
//        }
//        break;
      case REQUEST_CODE_PICK_ACCOUNT:
        if (resultCode == RESULT_OK) {
          mEmail = data.getStringExtra (AccountManager.KEY_ACCOUNT_NAME);
          Toast.makeText (getApplicationContext (), mEmail, Toast.LENGTH_SHORT);
          // With the account name acquired, go get the auth token
          //getUsername ();
        } else if (resultCode == RESULT_CANCELED) {
          // The account picker dialog closed without selecting an account.
          // Notify users that they must pick an account to proceed.
          //Toast.makeText(this, R.string.pick_account, Toast.LENGTH_SHORT).show();
        }
        break;
    }

  }

  @Override
  protected void onStart () {
    super.onStart ();
    mGoogleApiClient.connect ();
  }
//build the google api...
  public GoogleApiClient buildApiClient () {
    return new GoogleApiClient.Builder (this)
            .addConnectionCallbacks (this)
            .addOnConnectionFailedListener (this)
            .addApi (Plus.API, Plus.PlusOptions.builder ().build ())
            .addScope (new Scope ("email"))
            .build ();
  }

  //..pick user account
  private void pickUserAccount () {
    String[] accountTypes = new String[]{"com.google"};
    Intent intent = AccountPicker.newChooseAccountIntent (null, null,
            accountTypes, false, null, null, null, null);
    startActivityForResult (intent, REQUEST_CODE_PICK_ACCOUNT);
  }

  @Override
  protected void onStop () {
    super.onStop ();

    if (mGoogleApiClient.isConnected ()) {
      mGoogleApiClient.disconnect ();
    }
  }

  @Override
  public void onConnected (Bundle bundle) {
    Log.i (TAG, "onConnected");
    Toast.makeText (getApplicationContext (), "Logged In", Toast.LENGTH_SHORT).show ();
    mSignInProgress = SIGNED_IN;

    // We are signed in!
    // Retrieve some profile information to personalize our app for the user.
    try {
      ////Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
      ////mStatus.setText(String.format("Signed In to G+ as %s", currentUser.getDisplayName()));
      String emailAddress = Plus.AccountApi.getAccountName (mGoogleApiClient);
//      mStatus.setText(String.format("Signed In to My App as %s", emailAddress));
    } catch (Exception ex) {
      String exception = ex.getLocalizedMessage ();
      String exceptionString = ex.toString ();
    }
  }

  private void onSignedOut () {
    // Update the UI to reflect that the user is signed out.
    //  mSignInButton.setEnabled (true);

//    mStatus.setText("Signed out");

  }

  /**
   * Attempts to retrieve the username.
   * If the account is not yet known, invoke the picker. Once the account is known,
   * start an instance of the AsyncTask to get the auth token and do work with it.
   */
  private void getUsername() {
    if (mEmail == null) {
      pickUserAccount();
    } else {
      if (isDeviceOnline()) {
        new GetUsernameTask(GoogleFacebookLogin.this, mEmail, SCOPE).execute();
      } else {
        Toast.makeText(this, "Online", Toast.LENGTH_LONG).show();
      }
    }
  }

  @Override
  public void onConnectionSuspended (int i) {

  }


  @Override
  public void onConnectionFailed (ConnectionResult connectionResult) {

  }

  public class GetUsernameTask extends AsyncTask<Void, Void, Void> {
    Activity mActivity;
    String mScope;
    String mEmail;

    GetUsernameTask (Activity activity, String name, String scope) {
      this.mActivity = activity;
      this.mScope = scope;
      this.mEmail = name;
    }

    /**
     * Executes the asynchronous job. This runs when you call execute()
     * on the AsyncTask instance.
     */
    @Override
    protected Void doInBackground (Void... params) {
      try {
        String token = fetchToken ();
        if (token != null) {
          Toast.makeText (getApplicationContext (),"Token" + token +"", Toast.LENGTH_SHORT).show ();
          // **Insert the good stuff here.**
          // Use the token to access the user's Google data.
        }
      } catch (IOException e) {
        // The fetchToken() method handles Google-specific exceptions,
        // so this indicates something went wrong at a higher level.
        // TIP: Check for network connectivity before starting the AsyncTask.

      }
      return null;
    }

    /**
     * Gets an authentication token from Google and handles any
     * GoogleAuthException that may occur.
     */
    protected String fetchToken () throws IOException {
      try {
        return GoogleAuthUtil.getToken (mActivity, mEmail, mScope);
      } catch (UserRecoverableAuthException userRecoverableException) {
        // GooglePlayServices.apk is either old, disabled, or not present
        // so we need to show the user some UI in the activity to recover.
//        mActivity.handleException (userRecoverableException);
      } catch (GoogleAuthException fatalException) {
        // Some other type of unrecoverable exception has occurred.
        // Report and log the error as appropriate for your app.
      }
      return null;
    }


  }
  public boolean isDeviceOnline(){
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
    if (info != null && info.isConnectedOrConnecting ()) {
      return true;
    } else {
      Toast.makeText (getApplicationContext (),"Check your internet connection", Toast.LENGTH_SHORT).show ();
      return false;
    }
  }

  public void handleException(final Exception e) {
    // Because this call comes from the AsyncTask, we must ensure that the following
    // code instead executes on the UI thread.
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (e instanceof GooglePlayServicesAvailabilityException) {
          // The Google Play services APK is old, disabled, or not present.
          // Show a dialog created by Google Play services that allows
          // the user to update the APK
          int statusCode = ((GooglePlayServicesAvailabilityException)e)
                  .getConnectionStatusCode();
          Dialog dialog = GooglePlayServicesUtil.getErrorDialog (statusCode,
                  GoogleFacebookLogin.this,
                  REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
          dialog.show();
        } else if (e instanceof UserRecoverableAuthException) {
          // Unable to authenticate, such as when the user has not yet granted
          // the app access to the account, but the user can fix this.
          // Forward the user to an activity in Google Play services.
          Intent intent = ((UserRecoverableAuthException)e).getIntent();
          startActivityForResult(intent,
                  REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
        }
      }
    });
  }
}
