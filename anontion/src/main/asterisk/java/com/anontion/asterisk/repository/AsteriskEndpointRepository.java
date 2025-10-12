package com.anontion.asterisk.repository;

import com.anontion.asterisk.model.AsteriskEndpoint;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AsteriskEndpointRepository extends JpaRepository<AsteriskEndpoint, String> {

  boolean existsById(String asteriskEndpointId);

  Optional<AsteriskEndpoint> findById(String id);  
  
  @Query(value = "SELECT * FROM ps_endpoints WHERE auth = :auth LIMIT 1 ", nativeQuery = true)
  Optional<AsteriskEndpoint> findByAuth(@Param("auth") String auth);
}
