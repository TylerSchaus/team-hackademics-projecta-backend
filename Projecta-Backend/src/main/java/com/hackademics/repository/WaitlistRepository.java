package com.hackademics.repository;

import com.hackademics.model.Waitlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {
    Optional<Waitlist> findByWaitlistId(Long waitlistId);
    List<Waitlist> findByCourseId(Long courseId);
}
