package com.hackademics.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.repository.UserRepository;

@Component
public class RoleBasedAccessVerification {

    @Autowired
    private static UserRepository userRepository;

    public static boolean isAdmin(UserDetails userDetails) {
        // Assuming userRepository is injected or accessible here
        User user
                = userRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        // Handle case where the role is null, if applicable
        if (user.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User role is not assigned.");
        }
        return user.getRole() == Role.ADMIN;
    }

    public static boolean isCurrentUserRequestedStudent(UserDetails userDetails, Long studentId) {
        User user
                = userRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        // Return true only if user exists and the IDs match
        return user.getStudentId().equals(studentId);
    }
}
