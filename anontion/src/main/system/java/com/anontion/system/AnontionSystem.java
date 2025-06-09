package com.anontion.system;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

import com.anontion.common.misc.AnontionLog;
import com.anontion.common.security.AnontionSecurityTests;

public class AnontionSystem extends HttpServlet {

  private static final long serialVersionUID = 1L; 

  public void init(ServletConfig config) throws ServletException {
    super.init(config);
  
    if (!AnontionSecurityTests.run()) {

      _logger.severe("Tests failed.");
      
      return;
    }
    
    _logger.info("Tests ok.");
  }
  
  final private static AnontionLog _logger = new AnontionLog(AnontionSystem.class.getName());
}
