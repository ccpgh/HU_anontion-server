package com.anontion.common.dto.response;

import com.anontion.common.misc.AnontionTime;

public class ResponseBodyPOWDTO extends ResponseBodyDTO {

  private String text;

  private Long target;

  private Long ts;

  public ResponseBodyPOWDTO() {

  }

  public ResponseBodyPOWDTO(String text, Long target) {

    this.text = text;
    this.target = target;
    this.ts = AnontionTime.ts();
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

  public Long getTs() {
    
    return ts;
  }

  public void setTs(Long ts) {

    this.ts = ts;
  }
}
