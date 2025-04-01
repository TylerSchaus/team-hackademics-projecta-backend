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

import com.hackademics.dto.ResponseDto.UserResponseDTO;
import com.hackademics.dto.UpdateDto.UserUpdateDto;
import com.hackademics.model.Grade;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.UserService;
import com.hackademics.util.RoleBasedAccessVerification;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private RoleBasedAccessVerification roleBasedAccessVerification;

    private UserResponseDTO convertToResponseDto(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().toString(),
                user.getStudentId(),
                user.getEnrollStartDate() != null ? user.getEnrollStartDate().toLocalDate() : null,
                user.getExpectGraduationDate() != null ? user.getExpectGraduationDate().toLocalDate() : null,
                user.getAdminId()
        );
    }

    @Override
    public List<UserResponseDTO> getUsersByRole(Role role, UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
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
        return convertToResponseDto(userRepository.save(user)); // Reference from auth service for saving and dto conversion.
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

        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete users.");
        }

        if (authenticatedUser.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admins cannot delete themselves.");
        }

        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        userRepository.deleteById(id);
    }

    @Override
    public List<UserResponseDTO> getStudentsByNamePrefix(String prefix, UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can access this method.");
        }
        return userRepository.findByRole(Role.STUDENT).stream()
                .filter(student -> student.getFirstName().startsWith(prefix))
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> getStudentsByGradeRange(double low, double high, UserDetails currentUser) {
        // Ensure only admins can access this method
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can access this method.");
        }
        // Use findByRole to get all students
        return userRepository.findByRole(Role.STUDENT).stream()
                .filter(student -> {
                    double avgGrade = computeGradeAverage(student);
                    return avgGrade >= low && avgGrade <= high;
                })
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    // Utility methods

    private double computeGradeAverage(User student) {
        if (student.getGrades() == null
                || student.getGrades().isEmpty()) {
            return 0; // Return 0 if no grades exist
        }
        return student.getGrades().stream()
                .mapToDouble(Grade::getGrade) // Get the grade value from each Grade object
                .average() // Calculate the average
                .orElse(0); // Return 0 if no grades exist
    }

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
