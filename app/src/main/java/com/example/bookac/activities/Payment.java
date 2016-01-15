package com.example.bookac.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.bookac.GetPeopleAround;
import com.example.bookac.R;
import com.example.bookac.constants.Constants;
import com.example.bookac.singletons.User;
import com.example.bookac.tools.PicassoImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Payment extends AppCompatActivity {
  ImageView foodPhoto;

  TextView price;
  TextView itemName;

  String foodText;
  double priceText;
  String foodPhotoText;
  Random random = new Random ();
  int paymentId;
  String orderId;

  Button makePayment;

  EditText moreInfo;
  String moreInfoText;
  String uid;

  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_payment);
    setUpView ();
    getIntentContent ();
    generateRandomNumber ();
    passDataToview ();
    makePayment.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        makePayment();
      }
    });
  }

  private String generateRandomNumber(){
    paymentId = random.nextInt ((int) ((System.currentTimeMillis () / 1000000 + 1000000000 - 199) + 1));
    orderId = String.valueOf (paymentId + (char)(random.nextInt (100-35)) + (char)(random.nextInt (200-35)+3));
    return orderId;
  }

  //setup the view containing widgets
  private void setUpView () {
    foodPhoto = (ImageView)findViewById (R.id.foodImageCheckout);
    price = (TextView)findViewById (R.id.priceCheckOut);
    itemName = (TextView)findViewById (R.id.foodNameCheckout);
    makePayment = (Button)findViewById (R.id.makePaymentCheckout);
    moreInfo = (EditText)findViewById (R.id.moreInfo);
  }

  private void passDataToview(){
    PicassoImageLoader loader = new PicassoImageLoader (Payment.this);
    try{
      loader.loadImage (foodPhoto, foodPhotoText);
      price.setText ("N"+priceText + "");
      itemName.setText (foodText);
    }catch (NullPointerException e){e.printStackTrace ();}
  }

  private void getIntentContent(){
    Intent recievedIntent = getIntent ();
    foodText = recievedIntent.getStringExtra ("name");
    priceText = recievedIntent.getDoubleExtra ("price", 0);
    foodPhotoText = recievedIntent.getStringExtra ("photo");
    moreInfoText = moreInfo.getText ().toString ();
    uid = recievedIntent.getStringExtra ("uid");
  }

  private void makePayment(){
    final String UID = User.getContent (Payment.this, "personIdDb", "personId");
    RequestQueue queue = Volley.newRequestQueue (Payment.this);
    StringRequest request = new StringRequest (Request.Method.POST, Constants.MAKE_PAYMENT_URL + uid, new Response.Listener<String> () {
      @Override
      public void onResponse (String response) {
        Toast.makeText (getApplicationContext (), "Transaction Successful", Toast.LENGTH_LONG).show ();
      }
    }, new Response.ErrorListener () {
      @Override
      public void onErrorResponse (VolleyError error) {
        Toast.makeText(getApplicationContext (), "Something went wrong, please retry!", Toast.LENGTH_LONG).show ();

      }
    }){
      @Override
      protected Map<String, String> getParams () throws AuthFailureError {
        super.getParams ();
        Map<String,String> params = new HashMap<String, String> ();
        params.put ("customerUid", UID);
        params.put ("originalAmt", priceText+"");
        params.put ("item", foodText+"");
        params.put ("customerName", User.getContent (Payment.this, "personNameDb", "personName"));

        params.put ("description", foodText+"");

        params.put ("quantity", 1+"");
        params.put ("orderId",orderId);

        params.put ("customerImage", User.getContent (Payment.this, "photo", "photo"));
        if(!moreInfoText.equals (""))
          params.put ("additionalInfo", moreInfoText);
        else
          params.put ("additionalInfo", "");
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
