package com.hackademics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackademics.model.WaitlistEnrollment;

@Repository
public interface WaitlistEnrollmentRepository extends JpaRepository<WaitlistEnrollment, Long> {
    List<WaitlistEnrollment> findByWaitlistId(Long waitlistId);
    List<WaitlistEnrollment> findByStudentId(Long studentId);
}
