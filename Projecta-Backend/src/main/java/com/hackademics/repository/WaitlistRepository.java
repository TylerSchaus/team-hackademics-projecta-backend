package com.hackademics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackademics.model.Waitlist;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {
    
    List<Waitlist> findByCourseId(Long id);
}
