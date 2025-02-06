package com.hackademics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackademics.model.Administrator;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    // Custom query methods 
}
