package com.anontion.asterisk.model;

import java.util.UUID;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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

  @Column(nullable = false, name = "direct_media")
  @Pattern(regexp = "0|1|off|on|false|true|no|yes")
  private String directMedia;

  @Column(nullable = false, name = "connected_line_method")
  @Pattern(regexp = "invite|reinvite|update")
  private String connectedLineMethod;

  @Column(nullable = false, name = "direct_media_method")
  @Pattern(regexp = "invite|reinvite|update")
  private String directMediaMethod;

  @Column(nullable = false, name = "direct_media_glare_mitigation")
  @Pattern(regexp = "none|outgoing|incoming")
  private String directMediaGlareMitigation;

  @Column(nullable = false, name = "disable_direct_media_on_nat")
  @Pattern(regexp = "0|1|off|on|false|true|no|yes")
  private String disableDirectMediaOnNat;

  @Column(nullable = false, name = "mailboxes")
  private String mailboxes;

  @Column(nullable = false, name = "fax_detect")
  @Pattern(regexp = "0|1|off|on|false|true|no|yes")
  private String faxDetect;

  @Column(nullable = false, name = "t38_udptl")
  @Pattern(regexp = "0|1|off|on|false|true|no|yes")
  private String t38Udptl;

  @Column(nullable = false, name = "t38_udptl_ec")
  @Pattern(regexp = "none|fec|redundancy")
  private String t38UdptlEc;

  @Column(nullable = false, name = "t38_udptl_nat")
  @Pattern(regexp = "0|1|off|on|false|true|no|yes")
  private String t38UdptlNat;

  @Column(nullable = false, name = "t38_udptl_ipv6")
  @Pattern(regexp = "0|1|off|on|false|true|no|yes")
  private String t38UdptlIpv6;

  public AsteriskEndpoint() {

  }

  public AsteriskEndpoint(String id) {

    this.id = id;
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

  public String getConnectedLineMethod() {
    return connectedLineMethod;
  }

  public void setConnectedLineMethod(String connectedLineMethod) {
    this.connectedLineMethod = connectedLineMethod;
  }

  public String getDirectMediaMethod() {
    return directMediaMethod;
  }

  public void setDirectMediaMethod(String directMediaMethod) {
    this.directMediaMethod = directMediaMethod;
  }

  public String getDirectMediaGlareMitigation() {
    return directMediaGlareMitigation;
  }

  public void setDirectMediaGlareMitigation(String directMediaGlareMitigation) {
    this.directMediaGlareMitigation = directMediaGlareMitigation;
  }

  public String getDisableDirectMediaOnNat() {
    return disableDirectMediaOnNat;
  }

  public void setDisableDirectMediaOnNat(String disableDirectMediaOnNat) {
    this.disableDirectMediaOnNat = disableDirectMediaOnNat;
  }

  public String getMailboxes() {
    return mailboxes;
  }

  public void setMailboxes(String mailboxes) {
    this.mailboxes = mailboxes;
  }

  public String getFaxDetect() {
    return faxDetect;
  }

  public void setFaxDetect(String faxDetect) {
    this.faxDetect = faxDetect;
  }

  public String getT38Udptl() {
    return t38Udptl;
  }

  public void setT38Udptl(String t38Udptl) {
    this.t38Udptl = t38Udptl;
  }

  public String getT38UdptlEc() {
    return t38UdptlEc;
  }

  public void setT38UdptlEc(String t38UdptlEc) {
    this.t38UdptlEc = t38UdptlEc;
  }

  public String getT38UdptlNat() {
    return t38UdptlNat;
  }

  public void setT38UdptlNat(String t38UdptlNat) {
    this.t38UdptlNat = t38UdptlNat;
  }

  public String getT38UdptlIpv6() {
    return t38UdptlIpv6;
  }

  public void setT38UdptlIpv6(String t38UdptlIpv6) {
    this.t38UdptlIpv6 = t38UdptlIpv6;
  }

}
