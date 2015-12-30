package com.example.bookac.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bookac.R;
import com.example.bookac.fragments.PaymentDialog;

public class MakePayment extends AppCompatActivity {
  Button makePaymentNow;
  double price;
  TextView priceForCheckout;
  Bundle args = new Bundle ();
  @Override
  protected void onCreate (Bundle savedInstanceState) {

    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_make_payment);
    getIntentContent (args);
    Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
    setSupportActionBar (toolbar);
    makePaymentNow = (Button)findViewById (R.id.make_payment_now);
    priceForCheckout = (TextView)findViewById (R.id.price_for_checkout);
    try{
      priceForCheckout.setText ("N" + price);
    }
    catch (NullPointerException e){
      e.printStackTrace ();
    }
    makePaymentNow.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        PaymentDialog dialog  = new PaymentDialog ();
        dialog.setArguments(args);
        dialog.show (getFragmentManager (), "payment_dialog");
      }
    });
  }

  void getIntentContent(Bundle args){
    Intent gotten = getIntent ();
    price = gotten.getDoubleExtra ("price",0);
    args.putDouble ("price", price);
  }
}
