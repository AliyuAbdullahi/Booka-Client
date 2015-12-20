package com.example.bookac.tools;

import android.content.Context;
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

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aliyuolalekan on 20/12/2015.
 */
public class PaymentController {
  Context context;
  public PaymentController(Context context){
    this.context = context;
  }

  public void makeRequest(String url, final String merchantid, final String secretkey, final String email, final double amount, final String token){
    RequestQueue queue = Volley.newRequestQueue (context);
    StringRequest request = new StringRequest (Request.Method.POST, url, new Response.Listener<String> () {
      @Override
      public void onResponse (String response) {
        Toast.makeText (context.getApplicationContext (), response, Toast.LENGTH_SHORT).show ();
      }
    }, new Response.ErrorListener () {
      @Override
      public void onErrorResponse (VolleyError error) {
        Toast.makeText (context.getApplicationContext (), "Error occured", Toast.LENGTH_SHORT).show ();
      }
    })
    {
      @Override
      protected Map<String, String> getParams () throws AuthFailureError {
        super.getParams ();
        Map<String,String> params = new HashMap<String, String> ();
        params.put ("merchantid", merchantid);
        params.put ("secretkey", secretkey);
        params.put ("email", email);
        params.put ("amount", amount+"");
        params.put ("token", token);
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
