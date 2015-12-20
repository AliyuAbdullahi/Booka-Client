package com.example.bookac.fragments;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.bookac.AutoComplete;
import com.example.bookac.GetPeopleAround;
import com.example.bookac.R;
import com.example.bookac.Search;
import com.example.bookac.constants.Constants;
import com.example.bookac.singletons.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aliyuolalekan on 9/18/15.
 */
public class UserLogin extends Fragment {
  EditText email;
  EditText password;
  Button logIn;
  @Nullable
  @Override
  public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView (inflater, container, savedInstanceState);
    View view = inflater.inflate (R.layout.fragment_login_layout, container, false);
    email = (EditText)view.findViewById (R.id.emailTextBox);
    password = (EditText)view.findViewById (R.id.passwordTextBox);

    Animation anim2 = AnimationUtils.loadAnimation (getActivity (), R.anim.x_left);
    email.startAnimation(anim2);

    Animation anim3 = AnimationUtils.loadAnimation (getActivity (), R.anim.x_right);
    password.startAnimation(anim3);

    logIn = (Button)view.findViewById (R.id.signIn);
    Animation anim = AnimationUtils.loadAnimation (getActivity (), R.anim.translator);
    logIn.startAnimation(anim);

    logIn.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        if(isOnline ()){
          if((email.getText ().toString ().contains ("@") && email.getText ().toString () != null)
                  && password.getText ()!= null){

            User.saveDB ("username", email.getText ().toString (), getActivity ());
            User.saveDB ("password", password.getText ().toString (), getActivity ());
            final ProgressDialog dialog = new ProgressDialog (getActivity ());
            dialog.setTitle ("loading content...");
            dialog.show ();
            RequestQueue queue = Volley.newRequestQueue (getActivity ());
            StringRequest request = new StringRequest (Request.Method.POST, Constants.LOGIN_URL, new Response.Listener<String> () {
              @Override
              public void onResponse (String response) {
                String userImageUrl = null;
                String firstName = null;
                String lastName = null;
                Log.e("Response: ", response);
                try {
                  JSONObject responseObject = new JSONObject (response);
                  JSONObject authObject = responseObject.getJSONObject ("auth");
                  String token = authObject.getString ("token");
                  JSONObject imageUrlObject = authObject.getJSONObject ("password");
                  userImageUrl = imageUrlObject.getString ("profileImageURL");
                  User.imageUrl = userImageUrl;
                  User.uid = authObject.getString ("uid");
                  User.token = token;
                  JSONObject userObject = responseObject.getJSONObject ("userObj");
                  firstName = userObject.getString ("first_name");
                  lastName = userObject.getString ("last_name");
                  User.firstName = firstName;
                  User.lastName = lastName;

                } catch (JSONException e) {
                  e.printStackTrace ();
                }
                if(userImageUrl != null){
                  dialog.hide ();
                  Intent i = new Intent (getActivity (), GetPeopleAround.class);
                  startActivity (i);
                  getActivity ().overridePendingTransition (R.anim.right_in, R.anim.left_out);
                }else{
                  dialog.hide ();
                  Toast.makeText (getActivity (),"Invalid User Input",Toast.LENGTH_SHORT).show ();
                }

              }
            }, new Response.ErrorListener () {
              @Override
              public void onErrorResponse (VolleyError error) {
                Toast.makeText (getActivity (), error+ "", Toast.LENGTH_SHORT).show ();
                Log.e ("Error:", error+"");
              }
            }){
              @Override
              protected Map<String, String> getParams () throws AuthFailureError {
                super.getParams ();
                Map<String,String> params = new HashMap<String, String> ();
                params.put ("email", email.getText ().toString ());
                params.put ("password", password.getText ().toString ());
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
          }else {
            Toast.makeText (getActivity (),"Enter valid email or password", Toast.LENGTH_SHORT).show ();
          }
        }
      }
    });

    return view;
  }
  public void makeVolleyRequest(final String email, final String password){

  }

  public boolean isOnline() {
    ConnectivityManager connectivityManager = (ConnectivityManager)getActivity (). getSystemService (Context.CONNECTIVITY_SERVICE);
    NetworkInfo info = connectivityManager.getActiveNetworkInfo();
    if (info != null && info.isConnectedOrConnecting ()) {
      return true;
    } else {
      Toast.makeText (getActivity (),"Check your internet connection", Toast.LENGTH_SHORT).show ();
      return false;
    }

  }

  public String getUserNameFromSharedPreference(){
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (getActivity ());
    return preferences.getString ("username", "");
  }

  //get the saved password in sharedpreference
  public String getPassWordFromSharedPreference(){
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (getActivity ());
    return preferences.getString ("password", "");
  }
}
