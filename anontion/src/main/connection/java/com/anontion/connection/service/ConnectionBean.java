package com.anontion.connection.service;

import org.springframework.stereotype.Service;

import com.anontion.models.connection.model.AnontionConnection;

@Service
public class ConnectionBean {

  public AnontionConnection getAnontionConnection() {

    return new AnontionConnection();
  }
}
