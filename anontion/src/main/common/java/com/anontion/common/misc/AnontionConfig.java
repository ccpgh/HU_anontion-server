package com.anontion.common.misc;

public class AnontionConfig {


  public static String _ECDSA_CURVE = "secp256k1";
  
  public static int _CONTACT_CONNECTION_MAX = 60;
  public static int _CONTACT_CONNECTION_MIN = -15;

  public static int _CONTACT_CONNECTION_LOOP_DELAY_MS = 60 * 1000;

  public static String _CONTACT_CONNECTION_SIP_NAME = "sip";
  public static String _CONTACT_CONNECTION_SIPS_NAME = "sips";
  public static String _CONTACT_CONNECTION_PJSIP_NAME = "pjsip";
  public static String _CONTACT_CONNECTION_SIP_COLON = ":";
  public static char _CONTACT_CONNECTION_PJSIP_OPENBRACKET = '<';
  public static char _CONTACT_CONNECTION_PJSIP_CLOSEBRACKET = '>';
  public static String _CONTACT_CONNECTION_SIP_PREFIX = _CONTACT_CONNECTION_SIP_NAME + _CONTACT_CONNECTION_SIP_COLON;
  public static String _CONTACT_CONNECTION_PJSIP_PREFIX = _CONTACT_CONNECTION_PJSIP_NAME + _CONTACT_CONNECTION_SIP_COLON;
  public static String _CONTACT_CONNECTION_SIPS_PREFIX = _CONTACT_CONNECTION_SIPS_NAME + _CONTACT_CONNECTION_SIP_COLON;
  
  
  public static String _ASTERISK_PASSWORD_ENCODING_AUTHTYPE = "md5";
  public static String _ASTERISK_PASSWORD_ENCODING_REALM = "asterisk";
  
  public static Long _CONTACT_CONNECTION_BROADCAST_TIMEOUT = 300L; // 5 minutes possible to make new call.
  public static Long _CONTACT_CONNECTION_BROADCAST_TIMEOUT_NOTIMEOUT = 0L;
  
  public static Double _CONTACT_GPS_NULL_LONGITUDE = 1800.0;
  public static Double _CONTACT_GPS_NULL_LATITUDE = 1800.0;

}
