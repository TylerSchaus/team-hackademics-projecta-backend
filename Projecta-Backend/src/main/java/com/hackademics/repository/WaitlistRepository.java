package com.hackademics.repository;

import com.hackademics.Model.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, String> {
    Optional<Waitlist> findByWaitlistId(String waitlistId);
    List<Waitlist> findByCourseId(String courseId);
}
