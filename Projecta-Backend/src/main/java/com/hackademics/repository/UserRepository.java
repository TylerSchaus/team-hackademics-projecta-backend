package com.hackademics.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hackademics.model.Role;
import com.hackademics.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    // Helper methods for getting max student and admin ideas, allows us to replicate the auto generate process with incremental generation. 
    @Query("SELECT MAX(u.studentId) FROM User u WHERE u.studentId IS NOT NULL")
    Long findMaxStudentId();

    @Query("SELECT MAX(u.adminId) FROM User u WHERE u.adminId IS NOT NULL")
    Long findMaxAdminId();

    Optional<User> findByStudentId(Long studentId); 
    Optional<User> findByAdminId(Long adminId);

}
