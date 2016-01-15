package com.example.bookac.singletons;

/**
 * Created by aliyuolalekan on 09/01/2016.
 */
public enum FaceBookToken {
  INSTANCE;
  String token;
  public String getToken(){
    return token;
  }
  public void setToken(String token){
    token = token;
  }
}
