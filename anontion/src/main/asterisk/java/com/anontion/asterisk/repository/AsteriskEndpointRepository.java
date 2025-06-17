package com.anontion.asterisk.repository;

import com.anontion.asterisk.model.AsteriskEndpoint;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AsteriskEndpointRepository extends JpaRepository<AsteriskEndpoint, String> {

    boolean existsById(String asteriskEndpointId);
    
}
