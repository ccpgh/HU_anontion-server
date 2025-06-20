package com.anontion.models.application.repository;

import com.anontion.models.application.model.AnontionApplication;
import com.anontion.models.application.model.AnontionApplicationId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnontionApplicationRepository extends JpaRepository<AnontionApplication, AnontionApplicationId> {

    boolean existsById(AnontionApplicationId anontionApplicationId);
    
}
