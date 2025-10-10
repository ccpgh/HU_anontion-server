package com.anontion.common.misc;

abstract public class AnontionGPS {

  public static double _MINIMUM_LATITUDE = -90.0;
  public static double _MAXIMUM_LATITUDE = 90.0;

  public static double _MINIMUM_LONGITUDE = -180.0;
  public static double _MAXIMUM_LONGITUDE = 180.0;

  public static double _EARTH_RADIUS_IN_M = 6378137.0;
  public static double _CONNECTION_SEARCH_CIRCLE = 100.0;

  public static boolean isValidGPS(double latitude, double longitude) {
    
    return latitude >= _MINIMUM_LATITUDE && latitude <= _MAXIMUM_LATITUDE &&
        longitude >= _MINIMUM_LONGITUDE && longitude <= _MAXIMUM_LONGITUDE;
  }
  
  public static boolean isInCircle(double latA, double lonA, double latB, double lonB, double radius) {

    return getDistance(latA, lonA, latB, lonB) <= radius;
  }
  
  public static double getDistance(double latA, double lonA, double latB, double lonB) {

    double latitude = Math.toRadians(latB - latA);
    
    double longitude = Math.toRadians(lonB - lonA);

    double a = Math.sin(latitude / 2) * Math.sin(latitude / 2)
             + Math.cos(Math.toRadians(latA)) * Math.cos(Math.toRadians(latB))
             * Math.sin(longitude / 2) * Math.sin(longitude / 2);
    
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    
    return _EARTH_RADIUS_IN_M * c;
  }
  
}