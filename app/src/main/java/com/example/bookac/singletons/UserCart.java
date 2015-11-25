package com.example.bookac.singletons;

import java.util.ArrayList;

/**
 * Created by aliyuolalekan on 26/10/2015.
 */
public class UserCart {

  public ArrayList<MenuItem> itemsToCart = new ArrayList<> ();
  public void setItemsToCart (ArrayList<MenuItem> itemsToCart) {
    this.itemsToCart = itemsToCart;
  }

  public ArrayList<MenuItem> getItemsToCart () {
    return itemsToCart;
  }
}
