package com.anontion.common.misc;

public class AnontionConfig {


  public static String _ECDSA_CURVE = "secp256k1";
  
  public static int _CONTACT_CONNECTION_MAX = 60;
  public static int _CONTACT_CONNECTION_MIN = -15;

  public static String _CONTACT_CONNECTION_SIP_NAME = "sip";
  public static String _CONTACT_CONNECTION_PJSIP_NAME = "pjsip";
  public static String _CONTACT_CONNECTION_PJSIP_COLON = ":";
  public static char _CONTACT_CONNECTION_PJSIP_OPENBRACKET = '<';
  public static char _CONTACT_CONNECTION_PJSIP_CLOSEBRACKET = '>';
  public static String _CONTACT_CONNECTION_SIP_PREFIX = _CONTACT_CONNECTION_SIP_NAME + _CONTACT_CONNECTION_PJSIP_COLON;
  public static String _CONTACT_CONNECTION_PJSIP_PREFIX = _CONTACT_CONNECTION_PJSIP_NAME + _CONTACT_CONNECTION_PJSIP_COLON;
}
