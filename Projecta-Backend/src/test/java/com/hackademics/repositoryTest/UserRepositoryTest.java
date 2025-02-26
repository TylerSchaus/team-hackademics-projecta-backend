package com.hackademics.repositoryTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.hackademics.Model.Role;
import com.hackademics.Model.User;
import com.hackademics.repository.UserRepository;

@DataJpaTest
@ActiveProfiles("test") // Activates your H2-based application-test.properties
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User studentUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Create a "student" user
        studentUser = new User();
        studentUser.setFirstName("Alice");
        studentUser.setLastName("Example");
        studentUser.setEmail("alice@example.com");
        studentUser.setPassword("password");
        studentUser.setRole(Role.STUDENT);
        studentUser.setStudentId(1001L); // Just an example student ID
        studentUser.setEnrollStartDate(LocalDateTime.now());
        studentUser.setExpectGraduationDate(LocalDateTime.now().plusMonths(6));

        // Create an "admin" user
        adminUser = new User();
        adminUser.setFirstName("Bob");
        adminUser.setLastName("Admin");
        adminUser.setEmail("bob@example.com");
        adminUser.setPassword("password");
        adminUser.setRole(Role.ADMIN);
        adminUser.setAdminId(5001L); // Just an example admin ID
    }

    // Default JPA method.
    @Test
    void testSaveUser() {
        // Save the student user
        User savedStudent = userRepository.save(studentUser);

        // Verify it has an auto-generated primary ID
        Assertions.assertNotNull(savedStudent.getId(), "Saved student should have an ID");
        // Check that fields match
        Assertions.assertEquals("alice@example.com", savedStudent.getEmail());
        Assertions.assertEquals(Role.STUDENT, savedStudent.getRole());
    }

    @Test
    void testFindUserByEmail() {
        // Save both users
        userRepository.save(studentUser);
        userRepository.save(adminUser);

        // Try finding the student by email
        Optional<User> foundStudent = userRepository.findByEmail("alice@example.com");
        Assertions.assertTrue(foundStudent.isPresent(), "Student should be found by email");
        Assertions.assertEquals("Alice", foundStudent.get().getFirstName());
    }

    @Test
    void testFindUsersByRole() {
        // Save both users
        userRepository.save(studentUser);
        userRepository.save(adminUser);

        // Now find all students
        List<User> students = userRepository.findByRole(Role.STUDENT);
        Assertions.assertEquals(1, students.size(), "Should find exactly 1 student user");
        Assertions.assertEquals("alice@example.com", students.get(0).getEmail());

        // Find all admins
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        Assertions.assertEquals(1, admins.size(), "Should find exactly 1 admin user");
        Assertions.assertEquals("bob@example.com", admins.get(0).getEmail());
    }

    // Default JPA method.
    @Test
    void testGetUserById() {
        // Save a user
        User saved = userRepository.save(studentUser);
        Long id = saved.getId();

        // Retrieve by that ID
        Optional<User> found = userRepository.findById(id);
        Assertions.assertTrue(found.isPresent(), "User should exist by that primary ID");
        Assertions.assertEquals("Alice", found.get().getFirstName());
    }

    // Default JPA method.
    @Test
    void testUpdateUser() {
        // Save the student initially
        User saved = userRepository.save(studentUser);
        Long id = saved.getId();

        // Modify some fields
        saved.setFirstName("AliceUpdated");
        saved.setPassword("newPassword");
        userRepository.save(saved);

        // Fetch again
        User updated = userRepository.findById(id).orElseThrow();
        Assertions.assertEquals("AliceUpdated", updated.getFirstName());
        Assertions.assertEquals("newPassword", updated.getPassword());
    }

    @Test
    void testGetUserByStudentId() {
       
        userRepository.save(studentUser);
        Optional<User> foundStudent = userRepository.findByStudentId(1001L);
        Assertions.assertTrue(foundStudent.isPresent(), "Should find user by studentId=1001");
        Assertions.assertEquals("Alice", foundStudent.get().getFirstName());

    }

    @Test
    void testGetUserByAdminId() {

        userRepository.save(adminUser);
        Optional<User> foundAdmin = userRepository.findByAdminId(5001L);
        Assertions.assertTrue(foundAdmin.isPresent(), "Should find user by adminId=5001");
        Assertions.assertEquals("Bob", foundAdmin.get().getFirstName());
    }

    // Default JPA method.
    @Test
    void testDeleteUser() {
        User saved = userRepository.save(studentUser);
        Long id = saved.getId();

        userRepository.deleteById(id);

        Optional<User> afterDelete = userRepository.findById(id);
        Assertions.assertTrue(afterDelete.isEmpty(), "User should be deleted from the database");
    }

}
