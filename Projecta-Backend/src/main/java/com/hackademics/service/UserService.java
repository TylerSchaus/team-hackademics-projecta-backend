package com.hackademics.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.ResponseDto.UserResponseDTO;
import com.hackademics.dto.UpdateDto.UserUpdateDto;
import com.hackademics.model.Role;
import com.hackademics.model.User;

public interface UserService {
    List<UserResponseDTO> getUsersByRole(Role role, UserDetails currentUser);

    Optional<UserResponseDTO> getUserByEmail(String email);

    Optional<UserResponseDTO> getUserById(Long id);

    UserResponseDTO saveUser(User user);

    User createUser(User user); // Only used for testing. All real users should go through authentication.

    UserResponseDTO updateUser(Long id, UserUpdateDto userUpdateDto, UserDetails currentUser);

    void deleteUser(Long userId, UserDetails currentUser);
}
