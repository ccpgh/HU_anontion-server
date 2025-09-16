package com.anontion.models.connection.repository;

import com.anontion.models.connection.model.AnontionConnection;
import com.anontion.models.connection.model.AnontionConnectionId;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnontionConnectionRepository extends JpaRepository<AnontionConnection, AnontionConnectionId> {

  // NYI - these need to lock their row/page/table when within a transaction.

  Optional<AnontionConnection> findByConnectionId(UUID connectionId);

  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_a = :sipEndpointA AND sip_endpoint_b = :sipEndpointB and connection_type = 'direct' ", nativeQuery = true)
  Optional<AnontionConnection> findDirectBySipEndpointAAndSipEndpointBRelaxed(@Param("sipEndpointA") String sipEndpointA, @Param("sipEndpointB") String sipEndpointB);  

  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_a = :sipEndpointA AND sip_endpoint_b = :sipEndpointB and connection_type = 'indirect' ", nativeQuery = true)
  Optional<AnontionConnection> findIndirectBySipEndpointAAndSipEndpointBRelaxed(@Param("sipEndpointA") String sipEndpointA, @Param("sipEndpointB") String sipEndpointB);  

  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_a = :sipEndpointA AND sip_endpoint_b = :sipEndpointB and connection_type = 'multiple' ", nativeQuery = true)
  Optional<AnontionConnection> findMultipleBySipEndpointAAndSipEndpointBRelaxed(@Param("sipEndpointA") String sipEndpointA, @Param("sipEndpointB") String sipEndpointB);  

  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_a != sip_endpoint_b AND sip_endpoint_a = :sipEndpointA AND sip_endpoint_b = :sipEndpointB and sip_signature_A IS NOT NULL and sip_signature_B IS NOT NULL and sip_ts_A IS NOT NULL and sip_ts_B IS NOT NULL", nativeQuery = true)
  Optional<AnontionConnection> findBySipEndpointAAndSipEndpointB(@Param("sipEndpointA") String sipEndpointA, @Param("sipEndpointB") String sipEndpointB);  

  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_a = sip_endpoint_b AND sip_endpoint_a = :sipEndpointA AND sip_endpoint_b = :sipEndpointA and sip_signature_A IS NOT NULL and sip_signature_B IS NOT NULL and sip_ts_A IS NOT NULL and sip_ts_B IS NOT NULL", nativeQuery = true)
  Optional<AnontionConnection> findBySipEndpointAAndSipEndpointBIdentical(@Param("sipEndpointA") String sipEndpointA);  

  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_a = :sipEndpointA AND sip_signature_a = :sipSignatureA and connection_type = 'indirect' AND sip_endpoint_a != sip_endpoint_b ", nativeQuery = true)
  Optional<AnontionConnection> findIndirectBySipEndpointAAndSipSignatureARelaxed(@Param("sipEndpointA") String sipEndpointA, @Param("sipSignatureA") String sipSignatureA);  
  
  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_b = :sipEndpointB AND sip_signature_b = :sipSignatureB and connection_type = 'indirect' AND sip_endpoint_a != sip_endpoint_b ", nativeQuery = true)
  Optional<AnontionConnection> findIndirectBySipEndpointBAndSipSignatureBRelaxed(@Param("sipEndpointB") String sipEndpointB, @Param("sipSignatureB") String sipSignatureB);  
}
