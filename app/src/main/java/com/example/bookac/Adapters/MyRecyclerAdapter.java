package com.example.bookac.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by aliyuolalekan on 10/23/15.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
  private final Context mContext;
  public MyRecyclerAdapter (Context context, Context mContext){

    this.mContext = mContext;
  }

  @Override
  public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
    return null;
  }

  @Override
  public void onBindViewHolder (MyViewHolder holder, int position) {

  }

  @Override
  public int getItemCount () {
    return 0;
  }

  public class MyViewHolder extends RecyclerView.ViewHolder{

    public MyViewHolder (View itemView) {
      super (itemView);
    }
  }
}
