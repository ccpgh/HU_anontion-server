package com.anontion.common.dto.response;

public class ResponseApplicationBodyDTO extends ResponseBodyDTO {

  private String text;

  private Long target;

  private String remote;

  private String plaintext;
  
  private String sign;

  private Long ts;
  
  public ResponseApplicationBodyDTO() {

  }

  public ResponseApplicationBodyDTO(String text, Long target, String remote, String plaintext, String sign, Long ts) {

    this.text = text;
    this.target = target;
    this.remote = remote;
    this.plaintext = plaintext;
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

  public String getRemote() {
    
    return remote;
  }

  public void setRemote(String remote) {
    
    this.remote = remote;
  }
  
  public String getPlaintext() {
    
    return plaintext;
  }

  public void setPlaintext(String plaintext) {
    
    this.plaintext = plaintext;
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

