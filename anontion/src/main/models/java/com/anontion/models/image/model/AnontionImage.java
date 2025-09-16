package com.anontion.models.image.model;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(
  name = "anontion_image",
  uniqueConstraints = { @UniqueConstraint(columnNames = {"image_id"}) }
)
public class AnontionImage {

  @Column(name = "image_id")
  private UUID imageId;

  @Id
  @Column(name = "sip_endpoint", nullable = false, length = 255)
  private String sipEndpoint;

  @Lob
  @Column(name = "base64_encoded_image", columnDefinition = "LONGTEXT")
  private String base64EncodedImage;
  
  public AnontionImage(String sipEndpoint, String base64EncodedImage) {
    
    this.imageId = UUID.randomUUID();

    this.sipEndpoint = sipEndpoint;
    
    this.base64EncodedImage = base64EncodedImage;
  }

  public AnontionImage() {

  }

  public UUID getImageId() {
    
    return imageId;
  }

  public void setImageId(UUID imageId) {
    
    this.imageId = imageId;
  }

  public String getSipEndpoint() {
    
    return sipEndpoint;
  }

  public void setSipEndpoint(String sipEndpoint) {
    
    this.sipEndpoint = sipEndpoint;
  }
  
  public String getBase64EncodedImage() {
    
    return base64EncodedImage;
  }

  public void setBase64EncodedImage(String base64EncodedImage) {
    
    this.base64EncodedImage = base64EncodedImage;
  }
  
}



