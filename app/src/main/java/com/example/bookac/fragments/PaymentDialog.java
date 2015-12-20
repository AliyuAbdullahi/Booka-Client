package com.example.bookac.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookac.R;
import com.example.bookac.activities.MainActivity;
import com.example.bookac.tools.PaymentController;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.model.Card;
import co.paystack.android.model.Token;

/**
 * Created by aliyuolalekan on 11/12/2015.
 */
public class PaymentDialog extends DialogFragment {

  EditText mEditCardNum;
  EditText mEditCVC;
  EditText mEditExpiryMonth;
  EditText mEditExpiryYear;
  Button mButtonCreateToken;

  TextView mTextCard;
  TextView mTextToken;
  double price;
  Token token;
  Card card;

  ProgressDialog dialog;

  @Nullable
  @Override
  public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate (R.layout.payment_dialog, container, false);
    Bundle args = getArguments ();
    price = args.getDouble ("price");
    Toast.makeText (getActivity (), price + "", Toast.LENGTH_SHORT).show ();
    mEditCardNum = (EditText)view.findViewById (R.id.edit_card_number);
    mEditCVC = (EditText)view.findViewById (R.id.edit_cvc);
    mEditExpiryMonth = (EditText)view.findViewById (R.id.edit_expiry_month);
    mEditExpiryYear = (EditText)view.findViewById (R.id.edit_expiry_year);
    mButtonCreateToken = (Button)view.findViewById (R.id.m_button_create_token);
    mTextCard = (TextView)view.findViewById (R.id.mTextCard);
    mTextToken = (TextView)view.findViewById (R.id.mTextToken);
    PaystackSdk.initialize(getActivity ());
    dialog = new ProgressDialog(getActivity ());
//    card = new Card ("4123450131001381", 4, 2022, "883");

    mButtonCreateToken.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        //validate form
        validateForm();

        //check card validity
        if(card.isValid()){
          dialog.setMessage("Request token please wait");
          dialog.setCancelable(true);
          dialog.setCanceledOnTouchOutside(true);

          dialog.show();

          createToken(card);
        }
      }
    });
    return view;
  }

  private void validateForm(){
    //validate fields
    String cardNum = mEditCardNum.getText().toString().trim();

    if(isEmpty(cardNum)){
      mEditCardNum.setError("Empty card number");
      return;
    }

    //build card object with ONLY the number, update the other fields later
    card = new Card.Builder(cardNum, 0, 0, "").build();
    if(!card.validNumber()){
      mEditCardNum.setError("Invalid card number");
      return;
    }

    //validate cvc
    String cvc = mEditCVC.getText().toString().trim();
    if(isEmpty(cvc)){
      mEditCVC.setError("Empty cvc");
      return;
    }
    //update the cvc field of the card
    card.setCvc(cvc);

    //check that it's valid
    if(!card.validCVC()){
      mEditCVC.setError("Invalid cvc");
      return;
    }

    //validate expiry month;
    String sMonth = mEditExpiryMonth.getText().toString().trim();
    int month = -1;
    try{
      month = Integer.parseInt(sMonth);
    }
    catch (Exception e){}

    if(month < 1){
      mEditExpiryMonth.setError("Invalid month");
      return;
    }

    card.setExpiryMonth(month);

    String sYear = mEditExpiryYear.getText().toString().trim();
    int year = -1;
    try{
      year = Integer.parseInt(sYear);
    }
    catch(Exception e){}

    if(year < 1){
      mEditExpiryYear.setError("invalid year");
      return;
    }

    card.setExpiryYear(year);

    //validate expiry
    if(!card.validExpiryDate()){
      mEditExpiryMonth.setError("Invalid expiry");
      mEditExpiryYear.setError("Invalid expiry");
      return;
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    // request a window without the title
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    return dialog;
  }

  private void createToken(Card card){
    //then create token using PaystackSdk class
    PaystackSdk.createToken (card, new Paystack.TokenCallback () {
      @Override
      public void onCreate (Token token) {

        //here you retrieve the token, and send to your server for charging.
        if (dialog.isShowing ()) {
          dialog.dismiss ();
        }

        Toast.makeText (getActivity (), token.token, Toast.LENGTH_LONG).show ();
        PaymentDialog.this.token = token;
        updateTextViews (token);
        String iD = 100067+"";
        String secret = "3383d1a753b6aec287f29cac37742f72";
        PaymentController controller = new PaymentController (getActivity ());
        controller.makeRequest ("https://paystack.ng/charge/mobiletoken/", iD, secret, "age@demo.com", 200, token.token);
      }

      @Override
      public void onError (Exception error) {
        if (dialog.isShowing ()) {
          dialog.dismiss ();
        }
        Toast.makeText (getActivity (), error.getMessage (), Toast.LENGTH_LONG).show ();
        updateTextViews (null);
      }
    });
  }

  private void updateTextViews(Token token){
    if(token != null){
      mTextCard.setText("Card last 4 digits: " + token.last4);
      mTextToken.setText("Token: " + token.token);
    }
    else{
      mTextCard.setText("Unable to get token");
      mTextToken.setText("Token was null");
    }
  }

  @Override
  public void onActivityResult (int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  private boolean isEmpty(String s){
    return s == null || s.length() < 1;
  }
}
