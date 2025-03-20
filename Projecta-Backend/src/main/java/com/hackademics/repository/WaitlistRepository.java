package com.hackademics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackademics.model.Waitlist;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {
    
    Waitlist findByCourseId(Long id);
}
