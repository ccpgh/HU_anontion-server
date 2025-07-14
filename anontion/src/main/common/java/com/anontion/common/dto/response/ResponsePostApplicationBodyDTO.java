package com.anontion.common.dto.response;

public class ResponsePostApplicationBodyDTO extends ResponseBodyDTO {

  private String powText;

  private Long powTarget;

  private String encryptedName;

  private String serverPub;

  private String clientUID;
  
  private String serverSignature;

  private Long clientTs;
  
  public ResponsePostApplicationBodyDTO() {

  }

  public ResponsePostApplicationBodyDTO(String powText, Long powTarget, String encryptedName, 
      String serverPub, String clientUID, String serverSignature, Long clientTs) {

    this.clientUID = clientUID;
    
    this.clientTs = clientTs;

    this.encryptedName = encryptedName;

    this.powText = powText;

    this.powTarget = powTarget;

    this.serverPub = serverPub;
    
    this.serverSignature = serverSignature;
  }

  public String getPowText() {
    
    return powText;
  }

  public void setPowText(String powText) {
    
    this.powText = powText;
  }

  public Long getPowTarget() {
    
    return powTarget;
  }

  public void setPowTarget(Long powTarget) {
    
    this.powTarget = powTarget;
  }

  public String getEncryptedName() {
    
    return encryptedName;
  }

  public void setEncryptedName(String encryptedName) {
    
    this.encryptedName = encryptedName;
  }

  public String getServerPub() {
    
    return serverPub;
  }

  public void setServerPub(String serverPub) {
    
    this.serverPub = serverPub;
  }
  
  public String getClientUID() {
    
    return clientUID;
  }

  public void setClientUID(String clientUID) {
    
    this.clientUID = clientUID;
  }

  public String getServerSignature() {
    
    return serverSignature;
  }

  public void setServerSignature(String serverSignature) {
    
    this.serverSignature = serverSignature;
  }
  
  public Long getClientTs() {
    
    return clientTs;
  }

  public void setClientTs(Long clientTs) {
    
    this.clientTs = clientTs;
  }
}

