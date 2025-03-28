package com.anontion.application.repository;

import com.anontion.application.model.AnontionApplication;
import com.anontion.application.model.AnontionApplicationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnontionApplicationRepository extends JpaRepository<AnontionApplication, AnontionApplicationId> {

    boolean existsById(AnontionApplicationId anontionApplicationId);
}
