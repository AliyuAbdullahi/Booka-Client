package com.example.bookac;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookac.Adapters.PlaceAutocompleteAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.IOException;
import java.util.List;

import logger.Log;

public class AutoComplete extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
  protected GoogleApiClient mGoogleApiClient;

  private PlaceAutocompleteAdapter mAdapter;
  private TextView mPlaceDetailsText;

  private AutoCompleteTextView mAutocompleteView;
  private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds (
          new LatLng (-34.041458, 150.790100), new LatLng (-33.682247, 151.383362));

  @Override
  protected void onCreate (Bundle savedInstanceState) {
    super.onCreate (savedInstanceState);
    mGoogleApiClient = new GoogleApiClient.Builder (this)
            .enableAutoManage (this, 0 /* clientId */, this)
            .addApi (Places.GEO_DATA_API)
            .build ();
    setContentView (R.layout.activity_auto_complete);

    mAutocompleteView = (AutoCompleteTextView)
            findViewById (R.id.autocomplete_places);

    mAutocompleteView.setOnItemClickListener (mAutocompleteClickListener);
    mPlaceDetailsText = (TextView) findViewById (R.id.place_details);
    mAdapter = new PlaceAutocompleteAdapter (this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
            null);
    mAutocompleteView.setAdapter (mAdapter);

  }

  private AdapterView.OnItemClickListener mAutocompleteClickListener
          = new AdapterView.OnItemClickListener () {
    @Override
    public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
      final AutocompletePrediction item = mAdapter.getItem (position);
      final String placeId = item.getPlaceId ();
      final CharSequence primaryText = item.getPrimaryText (null);


      Log.i ("", "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
      PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
              .getPlaceById (mGoogleApiClient, placeId);
      placeResult.setResultCallback (mUpdatePlaceDetailsCallback);

      Log.i ("", "Called getPlaceById to get Place details for " + placeId);
    }
  };

  private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
          = new ResultCallback<PlaceBuffer> () {
    @Override
    public void onResult (PlaceBuffer places) {
      if (!places.getStatus ().isSuccess ()) {
        // Request did not complete successfully
        Log.e ("", "Place query did not complete. Error: " + places.getStatus ().toString ());
        places.release ();
        return;
      }
      // Get the Place object from the buffer.
      final Place place = places.get (0);
      mPlaceDetailsText.setText (place.getAddress ());

      Log.i ("", "LatLog " + place.getLatLng ());
      Log.i ("", "Place details received: " + place.getName ());
      Intent returnIntent = new Intent ();
      returnIntent.putExtra ("lng", place.getLatLng ().longitude);
      returnIntent.putExtra ("lat", place.getLatLng ().latitude);
      returnIntent.putExtra ("latlong", place.getLatLng ());
      returnIntent.putExtra ("result", place.getAddress ());
      setResult (RESULT_OK, returnIntent);
      finish ();
      places.release ();
    }
  };

  @Override
  public void onConnectionFailed (ConnectionResult connectionResult) {

  }


}
