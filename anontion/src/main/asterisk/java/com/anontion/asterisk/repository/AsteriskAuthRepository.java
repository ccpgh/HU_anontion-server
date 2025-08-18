package com.anontion.asterisk.repository;

import com.anontion.asterisk.model.AsteriskAuth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AsteriskAuthRepository extends JpaRepository<AsteriskAuth, String> {

  boolean existsById(String asteriskAuthId);
    
  Optional<AsteriskAuth> findById(String id);
  
  Optional<AsteriskAuth> findByUsernameAndMd5Cred(String username, String md5Cred);
}
