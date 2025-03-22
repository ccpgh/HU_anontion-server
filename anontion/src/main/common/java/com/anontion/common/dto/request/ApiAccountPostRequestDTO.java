package com.anontion.common.dto.request;

import com.anontion.common.dto.response.ApiResponseBodyDTO;
import com.anontion.common.dto.response.ApiResponseHeaderDTO;

public class ApiAccountPostRequestDTO {

  private ApiResponseHeaderDTO header;
  private ApiResponseBodyDTO body;

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

