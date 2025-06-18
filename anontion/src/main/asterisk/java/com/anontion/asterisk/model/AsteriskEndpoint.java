package com.anontion.asterisk.model;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "ps_endpoints")
public class AsteriskEndpoint {

  @Id
  @Column(nullable = false, unique = true, name = "id", length = 255)
  private String id;

  @Column(nullable = false, name = "transport", length = 40)
  private String transport;

  @Column(nullable = false, name = "aors", columnDefinition = "TEXT")
  private String aors;

  @Column(nullable = false, name = "auth", length = 255)
  private String auth;

  @Column(nullable = false, name = "context", length = 40)
  private String context;

  @Column(nullable = false, name = "disallow", length = 200)
  private String disallow;

  @Column(nullable = false, name = "allow", length = 200)
  private String allow;

  @Column(nullable = false, name = "direct_media", columnDefinition = "enum('0', '1', 'off', 'on', 'false', 'true', 'no', 'yes')")
  @Pattern(regexp = "^0|1|off|on|false|true|no|yes$")
  private String directMedia;

  @Column(nullable = false, name = "trust_id_outbound", columnDefinition = "enum('0', '1', 'off', 'on', 'false', 'true', 'no', 'yes')")
  @Pattern(regexp = "^0|1|off|on|false|true|no|yes$")
  private String trustIdOutbound;

  @Column(nullable = false, name = "dtmf_mode", columnDefinition = "enum('rfc4733','inband','info','auto','auto_info')")
  @Pattern(regexp = "^rfc4733|inband|info|auto|auto_info$")
  private String dtmfMode;

  @Column(nullable = false, name = "force_rport", columnDefinition = "enum('0', '1', 'off', 'on', 'false', 'true', 'no', 'yes')")
  @Pattern(regexp = "^0|1|off|on|false|true|no|yes$")
  private String forceRport;

  @Column(nullable = false, name = "rtp_symmetric", columnDefinition = "enum('0', '1', 'off', 'on', 'false', 'true', 'no', 'yes')")
  @Pattern(regexp = "^0|1|off|on|false|true|no|yes$")
  private String rtpSymmetric;

  @Column(nullable = false, name = "send_rpid", columnDefinition = "enum('0', '1', 'off', 'on', 'false', 'true', 'no', 'yes')")
  @Pattern(regexp = "^0|1|off|on|false|true|no|yes$")
  private String sendRpid;

  @Column(nullable = false, name = "ice_support", columnDefinition = "enum('0', '1', 'off', 'on', 'false', 'true', 'no', 'yes')")
  @Pattern(regexp = "^0|1|off|on|false|true|no|yes$")
  private String iceSupport;

  @Column(nullable = false, name = "tos_video", length = 10)
  private String tosVideo;

  @Column(nullable = false, name = "cos_video")
  private Integer cosVideo;

  @Column(nullable = false, name = "allow_subscribe", columnDefinition = "enum('0', '1', 'off', 'on', 'false', 'true', 'no', 'yes')")
  @Pattern(regexp = "^0|1|off|on|false|true|no|yes$")
  private String allowSubscribe;

  @Column(nullable = false, name = "callerid", length = 40)
  private String callerId;

  public AsteriskEndpoint() {

  }

  public AsteriskEndpoint(String id, String transport, String aors, String auth, String context, String disallow,
      String allow, String directMedia, String trustIdOutbound, String dtmfMode, String forceRport,
      String rtpSymmetric, String sendRpid, String iceSupport, String tosVideo, Integer cosVideo, 
      String allowSubscribe, String callerId) {

    this.id = id;

    this.transport = transport;

    this.aors = aors;

    this.auth = auth;

    this.context = context;

    this.disallow = disallow;

    this.allow = allow;

    this.directMedia = directMedia;

    this.trustIdOutbound = trustIdOutbound;

    this.dtmfMode = dtmfMode;

    this.forceRport = forceRport;

    this.rtpSymmetric = rtpSymmetric;

    this.sendRpid = sendRpid;

    this.iceSupport = iceSupport;

    this.tosVideo = tosVideo;

    this.cosVideo = cosVideo;

    this.allowSubscribe = allowSubscribe;

    this.callerId = callerId;
  }

  public String getId() {

    return id;
  }

  public void setId(String id) {

    this.id = id;
  }

  public String getTransport() {

    return transport;
  }

  public void setTransport(String transport) {

    this.transport = transport;
  }

  public String getAors() {

    return aors;
  }

  public void setAors(String aors) {

    this.aors = aors;
  }

  public String getAuth() {

    return auth;
  }

  public void setAuth(String auth) {

    this.auth = auth;
  }

  public String getContext() {

    return context;
  }

  public void setContext(String context) {

    this.context = context;
  }

  public String getDisallow() {

    return disallow;
  }

  public void setDisallow(String disallow) {

    this.disallow = disallow;
  }

  public String getAllow() {

    return allow;
  }

  public void setAllow(String allow) {

    this.allow = allow;
  }

  public String getDirectMedia() {

    return directMedia;
  }

  public void setDirectMedia(String directMedia) {

    this.directMedia = directMedia;
  }

  public String getTrustIdOutbound() {
    
    return trustIdOutbound;
  }

  public void setTrustIdOutbound(String trustIdOutbound) {
    
    this.trustIdOutbound = trustIdOutbound;
  }

  public String getDtmfMode() {
    
    return dtmfMode;
  }

  public void setDtmfMode(String dtmfMode) {
    
    this.dtmfMode = dtmfMode;
  }

  public String getForceRport() {
    
    return forceRport;
  }

  public void setForceRport(String forceRport) {
    
    this.forceRport = forceRport;
  }

  public String getRtpSymmetric() {
    
    return rtpSymmetric;
  }

  public void setRtpSymmetric(String rtpSymmetric) {
    
    this.rtpSymmetric = rtpSymmetric;
  }

  public String getSendRpid() {
    
    return sendRpid;
  }

  public void setSendRpid(String sendRpid) {
    
    this.sendRpid = sendRpid;
  }

  public String getIceSupport() {
    
    return iceSupport;
  }

  public void setIceSupport(String iceSupport) {
    
    this.iceSupport = iceSupport;
  }

  public String getTosVideo() {
    
    return tosVideo;
  }

  public void setTosVideo(String tosVideo) {
    
    this.tosVideo = tosVideo;
  }

  public Integer getCosVideo() {
    
    return cosVideo;
  }

  public void setCosVideo(Integer cosVideo) {
    
    this.cosVideo = cosVideo;
  }

  public String getAllowSubscribe() {
    
    return allowSubscribe;
  }

  public void setAllowSubscribe(String allowSubscribe) {
    
    this.allowSubscribe = allowSubscribe;
  }

  public String getCallerId() {
    
    return callerId;
  }

  public void setCallerId(String callerId) {
    
    this.callerId = callerId;
  }

}
