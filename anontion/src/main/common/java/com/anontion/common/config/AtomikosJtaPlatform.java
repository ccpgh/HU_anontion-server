package com.anontion.common.config;

import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;

import com.anontion.common.misc.AnontionLog;

public class AtomikosJtaPlatform extends AbstractJtaPlatform {

  private static final long serialVersionUID = 1L;

  private static TransactionManager transactionManager;

  private static UserTransaction userTransaction;

  public static void setTransactionManager(TransactionManager tm) {
    
    _logger.info("setTransactionManager CALLED!");

    transactionManager = tm;
  }

  public static void setUserTransaction(UserTransaction ut) {
    
    _logger.info("setUserTransaction CALLED!");

    userTransaction = ut;
  }

  @Override
  protected TransactionManager locateTransactionManager() {

    _logger.info("locateTransactionManager CALLED!");

    return transactionManager;
  }

  @Override
  protected UserTransaction locateUserTransaction() {
    
    _logger.info("locateUserTransaction CALLED!");
    
    return userTransaction;
  }
  
  final private static AnontionLog _logger = new AnontionLog(AtomikosJtaPlatform.class.getName());
}