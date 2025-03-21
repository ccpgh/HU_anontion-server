package com.anontion.account.repository;

import com.anontion.account.model.AnontionAccount;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import java.util.UUID;

@Repository
public interface AnontionAccountRepository extends JpaRepository<AnontionAccount, Long> {

  Optional<AnontionAccount> findById(long id);
}