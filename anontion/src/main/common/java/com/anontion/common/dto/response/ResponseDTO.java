package com.anontion.common.dto.response;
  
public class ResponseDTO {

  private ResponseHeaderDTO header;
  private ResponseBodyDTO body;

  public ResponseDTO(ResponseHeaderDTO header, ResponseBodyDTO body) {

    this.header = header;
    this.body   = body;
  }

  public ResponseHeaderDTO getHeader() {
    
    return header;
  }

  public void setHeader(ResponseHeaderDTO header) {
    
    this.header = header;
  }

  public ResponseBodyDTO getBody() {
    
    return body;
  }

  public void setBody(ResponseBodyDTO body) {
    
    this.body = body;
  }
}

