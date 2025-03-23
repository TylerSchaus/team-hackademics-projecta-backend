package com.hackademics.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.UserResponseDTO;
import com.hackademics.dto.UserUpdateDto;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private UserResponseDTO convertToResponseDto(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole().toString(),
            user.getStudentId() != null ? user.getStudentId().toString() : null,
            user.getEnrollStartDate() != null ? user.getEnrollStartDate().toLocalDate() : null,
            user.getExpectGraduationDate() != null ? user.getExpectGraduationDate().toLocalDate() : null,
            user.getAdminId()
        );
    }

    @Override
    public List<UserResponseDTO> getUsersByRole(Role role, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        // Ensure the request is from an admin
        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view user data.");
        }

        return userRepository.findByRole(role).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserResponseDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToResponseDto);
    }

    @Override
    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToResponseDto);
    }

    @Override
    public UserResponseDTO saveUser(User user) {
        return convertToResponseDto(userRepository.save(user));
    }

    @Override
    public User createUser(User user) { // Only used for testing. All real users should go through authentication. 
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == Role.ADMIN) {
            user.setAdminId(generateNextAdminId());
        }
        if (user.getRole() == Role.STUDENT) {
            user.setStudentId(generateNextStudentId());
        }
        return userRepository.save(user);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateDto userUpdateDto, UserDetails currentUser) {
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        boolean isAdmin = authenticatedUser.getRole() == Role.ADMIN;
        boolean isSelfUpdate = authenticatedUser.getId().equals(userToUpdate.getId());

        // Check if user is authorized to update this profile
        if (!isAdmin && !isSelfUpdate) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this user.");
        }

        // Admins can update more fields
        if (isAdmin) {
            if (userUpdateDto.getFirstName() != null) {
                userToUpdate.setFirstName(userUpdateDto.getFirstName());
            }
            if (userUpdateDto.getLastName() != null) {
                userToUpdate.setLastName(userUpdateDto.getLastName());
            }
            if (userUpdateDto.getGender() != null) {
                userToUpdate.setGender(userUpdateDto.getGender());
            }

            // Ensure Student ID is unique before updating
            if (userUpdateDto.getStudentId() != null) {
                validateUniqueStudentId(userUpdateDto.getStudentId());
                userToUpdate.setStudentId(userUpdateDto.getStudentId());
            }
        } // Regular users cannot modify restricted fields
        else if (userUpdateDto.getStudentId() != null || userUpdateDto.getFirstName() != null || userUpdateDto.getLastName() != null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this information.");
        }

        // Both admin & self-update users can change their email
        if (userUpdateDto.getEmail() != null) {
            userToUpdate.setEmail(userUpdateDto.getEmail());
        }

        return convertToResponseDto(userRepository.save(userToUpdate));
    }

    @Override
    public void deleteUser(Long id, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        // Ensure only admins can delete users
        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete users.");
        }

        // Prevent an admin from deleting themselves
        if (authenticatedUser.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admins cannot delete themselves.");
        }

        // Check if the user exists before attempting deletion
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        userRepository.deleteById(id);
    }

    // Utility methods
    public void validateUniqueStudentId(Long studentId) {
        Optional<User> existingUser = userRepository.findByStudentId(studentId);
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student ID is already taken.");
        }
    }

    private Long generateNextStudentId() {
        Long maxStudentId = userRepository.findMaxStudentId();
        return (maxStudentId != null) ? maxStudentId + 1 : 1L;
    }

    private Long generateNextAdminId() {
        Long maxAdminId = userRepository.findMaxAdminId();
        return (maxAdminId != null) ? maxAdminId + 1 : 1L;
    }
}
