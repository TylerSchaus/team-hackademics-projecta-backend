package com.hackademics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackademics.model.WaitlistRequest;

@Repository
public interface WaitlistRequestRepository extends JpaRepository<WaitlistRequest, Long> {
    
    List<WaitlistRequest> findAll();

    List<WaitlistRequest> findByWaitlistIdAndStudentId(Long waitlistId, Long studentId);
}