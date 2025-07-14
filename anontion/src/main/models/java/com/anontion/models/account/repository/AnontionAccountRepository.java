package com.anontion.models.account.repository;

import com.anontion.models.account.model.AnontionAccount;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnontionAccountRepository extends JpaRepository<AnontionAccount, Long> {

  //Optional<AnontionAccount> findById(UUID accountId);
  
  //Optional<AnontionAccount> findByAccountId(UUID accountId);

  Optional<AnontionAccount> findByClientTsAndClientNameAndClientId(Long clientTs, String clientName, UUID clientId);
}