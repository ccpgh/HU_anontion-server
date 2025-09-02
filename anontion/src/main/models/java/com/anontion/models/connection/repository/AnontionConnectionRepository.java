package com.anontion.models.connection.repository;

import com.anontion.models.connection.model.AnontionConnection;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnontionConnectionRepository extends JpaRepository<AnontionConnection, Long> {

  Optional<AnontionConnection> findByConnectionId(UUID connectionId);
  
  Optional<AnontionConnection> findBySipEndpointAAndSipEndpointB(String sipEndpointA, String sipEndpointB);

//  @Lock(LockModeType.PESSIMISTIC_WRITE)
//  @Query("SELECT c FROM AnontionConnection c WHERE c.sipEndpointA = :A AND c.sipEndpointB = :B")
//  Optional<AnontionConnection> findForUpdate(@Param("A") String sipEndpointA, @Param("B") String sipEndpointB);
  
}
