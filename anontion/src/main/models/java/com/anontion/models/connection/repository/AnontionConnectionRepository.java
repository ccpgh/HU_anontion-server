package com.anontion.models.connection.repository;

import com.anontion.models.connection.model.AnontionConnection;
import com.anontion.models.connection.model.AnontionConnectionId;

import java.util.List;
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

  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_a = :sipEndpointA AND sip_endpoint_b = :sipEndpointB and connection_type = :connectionType ", nativeQuery = true)
  Optional<AnontionConnection> findBySipEndpointAAndSipEndpointBRelaxed(@Param("sipEndpointA") String sipEndpointA, @Param("sipEndpointB") String sipEndpointB, @Param("connectionType") String connectionType);  

  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_a != sip_endpoint_b AND sip_endpoint_a = :sipEndpointA AND sip_endpoint_b = :sipEndpointB and sip_signature_A IS NOT NULL and sip_signature_B IS NOT NULL and sip_ts_A IS NOT NULL and sip_ts_B IS NOT NULL AND connection_type != 'broadcast' ", nativeQuery = true)
  Optional<AnontionConnection> findBySipEndpointAAndSipEndpointB(@Param("sipEndpointA") String sipEndpointA, @Param("sipEndpointB") String sipEndpointB);  

  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_a = sip_endpoint_b AND sip_endpoint_a = :sipEndpointA AND sip_endpoint_b = :sipEndpointA and sip_signature_A IS NOT NULL and sip_signature_B IS NOT NULL and sip_ts_A IS NOT NULL and sip_ts_B IS NOT NULL", nativeQuery = true)
  Optional<AnontionConnection> findBySipEndpointAAndSipEndpointBIdentical(@Param("sipEndpointA") String sipEndpointA);  

  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_a = :sipEndpointA AND sip_endpoint_b = :sipEndpointB AND connection_type = :connectionType ", nativeQuery = true)
  Optional<AnontionConnection> findIndirectBySipEndpointAAndSipEndpointBRelaxed(@Param("sipEndpointA") String sipEndpointA, @Param("sipEndpointB") String sipEndpointB, @Param("connectionType") String connectionType);  

  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_a = :sipEndpointA AND sip_signature_a = :sipSignatureA AND connection_type = :connectionType AND sip_endpoint_a != sip_endpoint_b ", nativeQuery = true)
  Optional<AnontionConnection> findIndirectBySipEndpointAAndSipSignatureARelaxed(@Param("sipEndpointA") String sipEndpointA, @Param("sipSignatureA") String sipSignatureA, @Param("connectionType") String connectionType);  
  
  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_b = :sipEndpointB AND sip_signature_b = :sipSignatureB AND connection_type = :connectionType AND sip_endpoint_a != sip_endpoint_b ", nativeQuery = true)
  Optional<AnontionConnection> findIndirectBySipEndpointBAndSipSignatureBRelaxed(@Param("sipEndpointB") String sipEndpointB, @Param("sipSignatureB") String sipSignatureB, @Param("connectionType") String connectionType);  

  Optional<AnontionConnection> findByRollSipEndpoint(String rollSipEndpoint);    
  
  @Query(value = "SELECT * FROM anontion_connection WHERE latitude >= :latitudeMin AND latitude <= :latitudeMax AND longitude >= :longitudeMin AND longitude <= :longitudeMax AND connection_type = 'broadcast' AND sip_endpoint_a = sip_endpoint_b AND timeout_ts >= :timeoutTs ", nativeQuery = true)
  List<AnontionConnection> findByGPSBox(@Param("latitudeMin") Double latitudeMin, @Param("latitudeMax") Double latitudeMax, @Param("longitudeMin") Double longitudeMin, @Param("longitudeMax") Double longitudeMax, @Param("timeoutTs") Long timeoutTs);
  
  @Query(value = "SELECT * FROM anontion_connection WHERE sip_endpoint_a = :sipEndpoint AND sip_endpoint_b = :sipEndpoint AND connection_type = 'broadcast' AND timeout_ts >= :timeoutTs ", nativeQuery = true)
  Optional<AnontionConnection> findBroadcastBySipEndpoint(@Param("sipEndpoint") String sipEndpoint, @Param("timeoutTs") Long timeoutTs);  

  @Query(value = "SELECT * FROM anontion_connection WHERE connection_type = 'broadcast' AND timeout_ts < :timeoutTs AND NOT timeout_ts = 0", nativeQuery = true)
  List<AnontionConnection> findBroadcastByTimeout(@Param("timeoutTs") Long timeoutTs);  
}
