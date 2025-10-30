package com.anontion.admin.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anontion.common.misc.AnontionConfig;
import com.anontion.common.misc.AnontionLog;
import com.anontion.common.misc.AnontionTime;
import com.anontion.models.connection.model.AnontionConnection;
import com.anontion.models.connection.repository.AnontionConnectionRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class DeleterService {

  @Autowired
  private AnontionConnectionRepository connectionRepository;

  private Thread _thread;
  
  private AtomicBoolean _stop = new AtomicBoolean(false);

  private static AtomicBoolean _flag = new AtomicBoolean(false);
  
  @PostConstruct
  public void start() {
    
    try {

      if (_flag.compareAndExchange(false, true)) {

        _logger.info("starting background connection deleter thread " + this);

        _thread = new Thread(this::runLoop, "deleter-Thread");

        _thread.setDaemon(true);

        _thread.start();
      
      }
      
    } catch (Exception e) {
     
      _logger.exception(e);
    }    
  }

  @PreDestroy
  public void stop() {

    _stop.set(true);

    if (_thread != null) {
 
      _thread.interrupt();
    }
  }

  public void runLoop() {
    
    while (!_stop.get()) {
    
      try {
      
        long now = AnontionTime.tsN();

        List<AnontionConnection> connections = connectionRepository.findBroadcastByTimeout(now);

        for (AnontionConnection connection : connections) {
          
          _logger.info("thread deleting contact '" + connection.getSipEndpointA() + "' and '" + connection.getSipEndpointB() + "'");
          
          connectionRepository.delete(connection);
        }
        
        Thread.sleep(AnontionConfig._CONTACT_CONNECTION_LOOP_DELAY_MS);

      } catch (InterruptedException e) {
      
        _logger.info("thread interrupted");
        
        Thread.currentThread().interrupt();
        
        break;
      
      } catch (Exception e) {
      
        _logger.exception(e);
        
        break;
      }
    }

    _logger.info("thread exiting cleanly");
  }
  
  private static final AnontionLog _logger = new AnontionLog(DeleterService.class.getName());
}