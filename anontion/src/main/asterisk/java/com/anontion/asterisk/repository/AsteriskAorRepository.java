package com.anontion.asterisk.repository;

import com.anontion.asterisk.model.AsteriskAor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AsteriskAorRepository extends JpaRepository<AsteriskAor, String> {

  boolean existsById(String asteriskAorId);
 
  Optional<AsteriskAor> findById(String id);
}


