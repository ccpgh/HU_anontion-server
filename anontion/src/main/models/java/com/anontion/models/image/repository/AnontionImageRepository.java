package com.anontion.models.image.repository;

import com.anontion.models.image.model.AnontionImage;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnontionImageRepository extends JpaRepository<AnontionImage, String> {

  Optional<AnontionImage> findById(String sipEndpoint);
}
