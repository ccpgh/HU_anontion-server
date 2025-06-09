package com.anontion.common.misc;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.logging.*;

import jakarta.servlet.http.HttpServletResponse;

public class AnontionLog {

  public AnontionLog(String name) {

    _logger = Logger.getLogger(name);
  }
  
  public void info(String message) {
    
    log(Level.INFO, _info + message);
  }

  public void info(String ... messages) {
    
    StringBuilder buffer = new StringBuilder();
    
    for (String message : messages) {
   
      buffer.append(message);
    
    }
    
    log(Level.INFO, _info + buffer.toString());
  }

  public void severe(String message) {
    
    log(Level.SEVERE, _severe + message);
  }

  public void severe(String ... messages) {
    
    StringBuilder buffer = new StringBuilder();
    
    for (String message : messages) {
    
      buffer.append(message);
    
    }
    
    log(Level.SEVERE, 
        _severe + buffer.toString());
  }

  public void severe_response(HttpServletResponse response, String ... messages) {
    
    StringBuilder buffer = new StringBuilder();
    
    for (String message : messages) {
      
      buffer.append(message);
    
    }
    
    severe_response(response, 
        buffer.toString());
  }
  
  public void severe_response(HttpServletResponse response, String message) {

    try {
      
      response.getWriter().append(message);

      severe(message);
    
    } catch (Exception e) {
    
      exception(e);
      
    }
  }
  
  public void exception(Exception e) {
    
    StringBuilder buffer = new StringBuilder();

    buffer.append(e.toString());
    buffer.append(" ");
    buffer.append(getStackTraceAsString(e));
    
    log(Level.SEVERE, 
        _exception + buffer.toString());
  }

  private void log(Level level, String message) {
    
    _logger.log(level, 
        prefix() + 
        " " +
        _logger.getName() +
        " " +
        message);
  }
  
  private String prefix() {
    
    return AnontionTime.now();
  }

  private String getStackTraceAsString(Throwable t) {

    StringWriter stringWriter = new StringWriter();
    
    PrintWriter printWriter = new PrintWriter(stringWriter, true);

    t.printStackTrace(printWriter);

    return stringWriter.getBuffer().toString();
  }

  private Logger _logger = null;
 
  private String _severe = "severe: ";
  
  private String _info = "info: ";

  private String _exception = "exception: ";
}
