package com.anontion.models.account.repository;

import com.anontion.models.account.model.AnontionAccount;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnontionAccountRepository extends JpaRepository<AnontionAccount, UUID> {

  //Optional<AnontionAccount> findById(UUID accountId);
  
  //Optional<AnontionAccount> findByAccountId(UUID accountId);

  Optional<AnontionAccount> findByClientTsAndClientNameAndClientId(Long clientTs, String clientName, UUID clientId);
  
  @Query(value = "SELECT * FROM anontion_account WHERE client_pub = :clientPub AND account_type = 'authorized' LIMIT 1 ", nativeQuery = true)
  Optional<AnontionAccount> findByClientPubAuthorized(@Param("clientPub") String clientPub); 
}