package com.example.bookac.singletons;

import java.util.ArrayList;

/**
 * Created by aliyuolalekan on 28/11/2015.
 */
public enum ItemCart {
  INSTANCE;

  public boolean itemIsinCart = false;

  public ArrayList<MenuItem> items = new ArrayList<MenuItem> ();

  public void addToItem(MenuItem item){
    items.add (item);
  }

  public MenuItem getItems(int position){
    return items.get (position);
  }

  public ArrayList<MenuItem> getAllItems(){
    return items;
  }

  public int getSize(){
    return items.size ();
  }

  public void delete(int position){
    items.remove (position);
  }

  public boolean deleteAll(){
    items.clear ();
    return items.size () <= 0;
  }

}
