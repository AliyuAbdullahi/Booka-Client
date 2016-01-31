package com.example.bookac.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookac.GetPeopleAround;
import com.example.bookac.R;
import com.example.bookac.singletons.User;

public class UserFormUpdate extends AppCompatActivity {
  EditText firstname;
  EditText lastname;
  EditText expiry_date;
  EditText cvv;
  EditText email;
  EditText phonenumber;
  EditText nameoncard;
  EditText cardnumber;

  Button save;
  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    setContentView (R.layout.activity_user_form_update);
    setUpView ();
    try{
      autoAddContent ();
    }catch (Exception e){
      e.printStackTrace ();
    }

    save.setOnClickListener (new View.OnClickListener () {
      @Override
      public void onClick (View v) {
        makePayment();
      }
    });
  }

  private void setUpView () {
    firstname = (EditText)findViewById (R.id.firstname_complete);
    lastname = (EditText)findViewById (R.id.lastname_complete);
    expiry_date = (EditText)findViewById (R.id.expirydate_complete);
    cvv = (EditText)findViewById (R.id.cvv_complete);
    email = (EditText)findViewById (R.id.email_complete);
    phonenumber = (EditText)findViewById (R.id.phonenumber_complete);
    nameoncard = (EditText)findViewById (R.id.nameoncard_complete);
    cardnumber = (EditText)findViewById (R.id.cardnumber_complete);
    save = (Button)findViewById (R.id.save_complete);
  }

  private void autoAddContent () {
    firstname.setText (User.getContent (UserFormUpdate.this, "firstname", "firstname"));
    lastname.setText (User.getContent (UserFormUpdate.this, "lastname", "lastname"));
    email.setText (User.getContent (UserFormUpdate.this, "email", "email"));

  }

  private void makePayment () {
    Intent intent = new Intent (UserFormUpdate.this, GetPeopleAround.class);
    if (firstname.getText ().toString ().isEmpty ()
            && lastname.getText ().toString ().isEmpty ()
            && expiry_date.getText ().toString ().isEmpty ()
            && phonenumber.getText ().toString ().isEmpty ()
            && nameoncard.getText ().toString ().isEmpty ()
            && cardnumber.getText ().toString ().isEmpty ()
            && email.getText ().toString ().isEmpty ()
            && cvv.getText ().toString ().isEmpty ()) {

      ToastMessage ("All Fields are empty");

    } else if(lastname.getText ().toString ().isEmpty ()
            && expiry_date.getText ().toString ().isEmpty ()
            && phonenumber.getText ().toString ().isEmpty ()
            && nameoncard.getText ().toString ().isEmpty ()
            && cardnumber.getText ().toString ().isEmpty ()
            && email.getText ().toString ().isEmpty ()
            && cvv.getText ().toString ().isEmpty ()) {
      ToastMessage ("Only first name available fill the rest");

    } else if (expiry_date.getText ().toString ().isEmpty ()
            && phonenumber.getText ().toString ().isEmpty ()
            && nameoncard.getText ().toString ().isEmpty ()
            && cardnumber.getText ().toString ().isEmpty ()
            && email.getText ().toString ().isEmpty ()
            && cvv.getText ().toString ().isEmpty ()) {
      ToastMessage ("Only first name and last name available fill the rest");

    } else if (phonenumber.getText ().toString ().isEmpty ()
            && nameoncard.getText ().toString ().isEmpty ()
            && cardnumber.getText ().toString ().isEmpty ()
            && email.getText ().toString ().isEmpty ()
            && cvv.getText ().toString ().isEmpty ()) {
      ToastMessage ("phone number \n name on card  \n cardnumber\n email \n cvv not available");

    }

    else if (nameoncard.getText ().toString ().isEmpty ()
            && cardnumber.getText ().toString ().isEmpty ()
            && email.getText ().toString ().isEmpty ()
            && cvv.getText ().toString ().isEmpty ()) {
      ToastMessage ("name on card \n cardnumber\n email \n cvv not available");
    }

    else if (cardnumber.getText ().toString ().isEmpty ()
            && email.getText ().toString ().isEmpty ()
            && cvv.getText ().toString ().isEmpty ()){
      ToastMessage ("cardnumber\n email \n cvv not available");
    }

    else if (email.getText ().toString ().isEmpty ()
            && cvv.getText ().toString ().isEmpty ()) {
      ToastMessage ("email \n cvv not available");

    }

    else if (cvv.getText ().toString ().isEmpty ()){
      ToastMessage ("cvv not available");

    }

    else if (email.getText ().toString ().isEmpty ()){
      ToastMessage ("email not available");

    }

    else if (cardnumber.getText ().toString ().isEmpty ()){
      ToastMessage ("cardnumber not available");

    }

    else if (nameoncard.getText ().toString ().isEmpty ()){
      ToastMessage ("name on card not available");

    }

    else if (phonenumber.getText ().toString ().isEmpty ()){
      ToastMessage ("phonenumber not available");
    }

    else if (expiry_date.getText ().toString ().isEmpty ()) {
      ToastMessage ("expiry date not available");
    }

    else if (firstname.getText ().toString ().isEmpty ()) {
      ToastMessage ("first name date not available");
    }

    else if (lastname.getText ().toString ().isEmpty ()) {
      ToastMessage ("last name not available");
    }
    else
      startActivity (intent);

  }

  public void ToastMessage(String message){
    Toast.makeText (UserFormUpdate.this, message, Toast.LENGTH_SHORT).show ();
  }
}
