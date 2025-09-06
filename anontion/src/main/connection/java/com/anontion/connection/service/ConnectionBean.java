package com.anontion.connection.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.anontion.common.dto.request.RequestPostConnectionDTO;
import com.anontion.common.dto.response.ResponseDTO;
import com.anontion.common.dto.response.Responses;
import com.anontion.common.misc.AnontionLog;
import com.anontion.models.connection.model.AnontionConnection;

import jakarta.validation.Valid;

@Service
public class ConnectionBean {

  public AnontionConnection getAnontionConnection() {

    return new AnontionConnection();
  }

  final private static AnontionLog _logger = new AnontionLog(ConnectionBean.class.getName());
}
