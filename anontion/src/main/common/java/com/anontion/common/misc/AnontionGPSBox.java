package com.anontion.common.misc;

public class AnontionGPSBox {

  final private double _latitudeMin;

  final private double _latitudeMax;

  final private double _longitudeMin;

  final private double _longitudeMax;

  public AnontionGPSBox(double latitude, double longitude, double distance) {

    {
      double latitudeOffset = (distance / AnontionGPS._EARTH_RADIUS_IN_M) * (180 / Math.PI);

      double latitudeA = latitude - latitudeOffset;

      double latitudeB = latitude + latitudeOffset;

      _latitudeMin = Math.min(latitudeA, latitudeB);

      _latitudeMax = Math.max(latitudeA, latitudeB);

    }

    {
      double longitudeOffset = (distance / (AnontionGPS._EARTH_RADIUS_IN_M * Math.cos(Math.toRadians(latitude)))) * (180 / Math.PI);

      double longitudeA = longitude - longitudeOffset;

      double longitudeB = longitude + longitudeOffset;

      _longitudeMin = Math.min(longitudeA, longitudeB);

      _longitudeMax = Math.max(longitudeA, longitudeB);
    }
  }

  public double getLatitudeMin() {

    return _latitudeMin;
  }

  public double getLatitudeMax() {

    return _latitudeMax;
  }

  public double getLongitudeMin() {

    return _longitudeMin;
  }

  public double getLongitudeMax() {

    return _longitudeMax;
  }

  @Override
  public String toString() {

    return "AnontionGPSBox { " +
          " latitudeMin = "    + _latitudeMin  +
          " latitudeMax = "    + _latitudeMax  +
          " longitudeMin = "   + _longitudeMin +
          " longitudeMax ="    + _longitudeMax +
          " }";
  }
}
