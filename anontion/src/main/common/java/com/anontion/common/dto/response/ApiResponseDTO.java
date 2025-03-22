package com.anontion.common.dto.response;
  
public class ApiResponseDTO {

  private ApiResponseHeaderDTO header;
  private ApiResponseBodyDTO body;

  public ApiResponseDTO(ApiResponseHeaderDTO header, ApiResponseBodyDTO body) {

    this.header = header;
    this.body   = body;
  }

  public ApiResponseHeaderDTO getHeader() {
    
    return header;
  }

  public void setHeader(ApiResponseHeaderDTO header) {
    
    this.header = header;
  }

  public ApiResponseBodyDTO getBody() {
    
    return body;
  }

  public void setBody(ApiResponseBodyDTO body) {
    
    this.body = body;
  }
}

