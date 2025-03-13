package com.hackademics.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.UserUpdateDto;
import com.hackademics.model.Role;
import com.hackademics.model.User;

public interface UserService {
    List<User> getUsersByRole(Role role, UserDetails currentUser);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserById(Long id);

    User saveUser(User user);

    User createUser(User user); // Only used for testing. All real users should go through authentication.

    User updateUser(Long id, UserUpdateDto userUpdateDto, UserDetails currentUser);

    void deleteUser(Long userId, UserDetails currentUser);


}
