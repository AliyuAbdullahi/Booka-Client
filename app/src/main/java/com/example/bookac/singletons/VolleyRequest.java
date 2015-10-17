package com.example.bookac.singletons;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aliyuolalekan on 9/18/15.
 */
public class VolleyRequest {
  Context context;
  public final String GET = "GET";
  public final String POST = "POST";
  public final String DELETE = "DELETE";
  private int method;
  public String result;
  public boolean hasError = false;
  private String key;
  private String value;

  public VolleyRequest (Context context){
    this.context = context;

  }

  public String get(String Url){
    RequestQueue queue = Volley.newRequestQueue (context);
    StringRequest request = new StringRequest (Request.Method.GET, Url, new Response.Listener<String> () {
      @Override
      public void onResponse (String response) {
        result = response;
      }
    }, new Response.ErrorListener () {
      @Override
      public void onErrorResponse (VolleyError error) {
        hasError = true;
        result = error +"";
      }
    });
    queue.add (request);

    int socketTimeout = 30000;//30 seconds - change to what you want
    RetryPolicy policy = new DefaultRetryPolicy (socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    request.setRetryPolicy (policy);
    queue.add (request);
    return  result;
  }


  public String delete(String Url){
    RequestQueue queue = Volley.newRequestQueue (context);
    StringRequest request = new StringRequest (Request.Method.POST, Url, new Response.Listener<String> () {
      @Override
      public void onResponse (String response) {

      }
    }, new Response.ErrorListener () {
      @Override
      public void onErrorResponse (VolleyError error) {
        hasError = true;
      }
    });

    int socketTimeout = 30000;//30 seconds - change to what you want
    RetryPolicy policy = new DefaultRetryPolicy (socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    request.setRetryPolicy (policy);
    queue.add (request);

    return result;
  }

}
