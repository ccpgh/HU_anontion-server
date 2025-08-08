package com.anontion.common.dto.response;

public class ResponseGetAccountBodyDTO extends ResponseBodyDTO {

  private String placeholder;

  public ResponseGetAccountBodyDTO() {

  }

  public ResponseGetAccountBodyDTO(String placeholder) {
    
    this.placeholder = placeholder;
  }

  public String getPLaceholder() {

    return placeholder;
  }

  public void setPlaceholder(String placeholder) {
    
    this.placeholder = placeholder;
  }
  
}

