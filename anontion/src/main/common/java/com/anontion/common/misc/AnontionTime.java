package com.anontion.common.misc;

import java.util.Date;


public class AnontionTime {

  public static Long tsN() {

    return (long) System.currentTimeMillis() / 1000;
  }
  
  public static String now() {
   
    return new Date().toString();
  }
}

