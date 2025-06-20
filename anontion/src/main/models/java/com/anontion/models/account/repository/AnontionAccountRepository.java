package com.anontion.models.account.repository;

import com.anontion.models.account.model.AnontionAccount;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnontionAccountRepository extends JpaRepository<AnontionAccount, Long> {

  Optional<AnontionAccount> findById(UUID id);
  
  Optional<AnontionAccount> findByTsAndNameAndApplication(Long ts, String name, UUID application);
}