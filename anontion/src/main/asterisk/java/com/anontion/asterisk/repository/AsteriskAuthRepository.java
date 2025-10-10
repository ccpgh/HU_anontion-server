package com.anontion.asterisk.repository;

import com.anontion.asterisk.model.AsteriskAuth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AsteriskAuthRepository extends JpaRepository<AsteriskAuth, String> {

  boolean existsById(String asteriskAuthId);
    
  Optional<AsteriskAuth> findById(String id);
  
  Optional<AsteriskAuth> findByUsernameAndMd5Cred(String username, String md5Cred);
  
  @Query(value = "SELECT * FROM ps_auths WHERE username = :username LIMIT 1 ", nativeQuery = true)
  Optional<AsteriskAuth> findByUsername(@Param("username") String username);
}
