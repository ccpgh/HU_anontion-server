package com.anontion.common.dto.response;

public class ResponsePostApplicationBodyDTO extends ResponseBodyDTO {

  private String text;

  private Long target;

  private String ename;

  private String remote;

  private String uid;
  
  private String sign;

  private Long ts;
  
  public ResponsePostApplicationBodyDTO() {

  }

  public ResponsePostApplicationBodyDTO(String text, Long target, String ename, String remote, String uid, String sign, Long ts) {

    this.text = text;

    this.target = target;

    this.ename = ename;

    this.remote = remote;
    
    this.uid = uid;
    
    this.sign = sign;
    
    this.ts = ts;
  }

  public String getText() {
    
    return text;
  }

  public void setText(String text) {
    
    this.text = text;
  }

  public Long getTarget() {
    
    return target;
  }

  public void setTarget(Long target) {
    
    this.target = target;
  }

  public String getEname() {
    
    return ename;
  }

  public void setEname(String ename) {
    
    this.ename = ename;
  }

  public String getRemote() {
    
    return remote;
  }

  public void setRemote(String remote) {
    
    this.remote = remote;
  }
  
  public String getUid() {
    
    return uid;
  }

  public void setUid(String uid) {
    
    this.uid = uid;
  }

  public String getSign() {
    
    return sign;
  }

  public void setSign(String sign) {
    
    this.sign = sign;
  }
  
  public Long getTs() {
    
    return ts;
  }

  public void setTs(Long ts) {
    
    this.ts = ts;
  }
}

