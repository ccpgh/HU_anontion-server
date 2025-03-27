package com.anontion.account.repository;

import com.anontion.account.model.AnontionApplication;
import com.anontion.account.model.AnontionApplicationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnontionApplicationRepository extends JpaRepository<AnontionApplication, AnontionApplicationId> {

    boolean existsById(AnontionApplicationId anontionApplicationId);
}
