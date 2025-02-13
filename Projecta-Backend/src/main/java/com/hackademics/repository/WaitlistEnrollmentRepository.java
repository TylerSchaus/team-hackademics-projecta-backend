package com.hackademics.repository;

import com.hackademics.Model.WaitlistEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitlistEnrollmentRepository extends JpaRepository<WaitlistEnrollment, String> {
    List<WaitlistEnrollment> findByWaitlistId(String waitlistId);
    List<WaitlistEnrollment> findByStudentId(String studentId);
}
