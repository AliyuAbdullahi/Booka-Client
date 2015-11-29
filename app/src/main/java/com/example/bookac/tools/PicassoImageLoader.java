package com.example.bookac.tools;

import android.content.Context;
import android.widget.ImageView;

import com.example.bookac.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by aliyuolalekan on 29/11/2015.
 */
public class PicassoImageLoader {

  Context context;
  ImageView imageView;
  String url;

  public PicassoImageLoader(Context context){
    this.context = context;
  }
  public void loadImage(final ImageView imageView, final String url){
    this.imageView = imageView;
    this.url = url;
    try {
      Picasso.with (context).load (url)
              .error (R.drawable.logo).placeholder (R.drawable.logo)
              .into (imageView, new Callback () {
                @Override
                public void onSuccess () {

                }
                @Override
                public void onError () {
                  loadImage (imageView, url);
                }
              });
    } catch (Exception e) {
      e.printStackTrace ();
    }
  }
}
