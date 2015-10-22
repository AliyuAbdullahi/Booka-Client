package com.example.bookac.singletons;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by aliyuolalekan on 9/29/15.
 */
public class Chef implements Serializable{

  public void setAddress (String address) {
    this.address = address;
  }

  public void setFirstname (String firstname) {
    this.firstname = firstname;
  }

  public void setLastname (String lastname) {
    this.lastname = lastname;
  }

  public void setLongitude (double longitude) {
    this.longitude = longitude;
  }

  public void setLatitude (double latitude) {
    this.latitude = latitude;
  }

  public void setProfilePhoto (String profilePhoto) {
    this.profilePhoto = profilePhoto;
  }

  public void setNickName (String nickName) {
    this.nickName = nickName;
  }

  public static ArrayList<Chef> chefs = new ArrayList<Chef> ();
  @SerializedName("address")
  public  String address;
  @SerializedName("firstname")
  public  String firstname;
  @SerializedName("lastname")
  public  String lastname;
  @SerializedName("longitude")
  public  double longitude;
  @SerializedName ("latitude")
  public  double latitude;
  @SerializedName ("profilePhoto")
  public  String profilePhoto;

  public static void setChefs (ArrayList<Chef> chefs) {
    Chef.chefs = chefs;
  }

  public ArrayList<MenuItem> getMenuItems () {
    return menuItems;
  }

  public ArrayList<MenuItem> menuItems;

  public void setPhoneNumber (long phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @SerializedName("nickName")
  public  String nickName;

  public double getPhoneNumber () {
    return phoneNumber;
  }

  @SerializedName ("phone_number")
  public long phoneNumber;
  public String getAddress () {
    return address;
  }

  public String getFirstname () {
    return firstname;
  }

  public String getLastname () {
    return lastname;
  }

  public double getLongitude () {
    return longitude;
  }

  public double getLatitude () {
    return latitude;
  }

  public String getProfilePhoto () {
    return profilePhoto;
  }

  public String getNickName () {
    return nickName;
  }
}
