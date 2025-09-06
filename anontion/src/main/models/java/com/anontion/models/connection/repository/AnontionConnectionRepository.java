package com.anontion.models.connection.repository;

import com.anontion.models.connection.model.AnontionConnection;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnontionConnectionRepository extends JpaRepository<AnontionConnection, Long> {

  Optional<AnontionConnection> findByConnectionId(UUID connectionId);

  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_a = :sipEndpointA AND sip_endpoint_b = :sipEndpointB and connection_type = 'direct' ", nativeQuery = true)
  Optional<AnontionConnection> findDirectBySipEndpointAAndSipEndpointBRelaxed(@Param("sipEndpointA") String sipEndpointA, @Param("sipEndpointB") String sipEndpointB);  
  
  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_a = :sipEndpointA AND sip_endpoint_b = :sipEndpointB and sip_signature_A IS NOT NULL and sip_signature_B IS NOT NULL and sip_ts_A IS NOT NULL and sip_ts_B IS NOT NULL", nativeQuery = true)
  Optional<AnontionConnection> findBySipEndpointAAndSipEndpointB(@Param("sipEndpointA") String sipEndpointA, @Param("sipEndpointB") String sipEndpointB);  
}
