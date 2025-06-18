package com.anontion.asterisk.repository;

import com.anontion.asterisk.model.AsteriskAuth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AsteriskAuthRepository extends JpaRepository<AsteriskAuth, String> {

  boolean existsById(String asteriskAuthId);
    
}
