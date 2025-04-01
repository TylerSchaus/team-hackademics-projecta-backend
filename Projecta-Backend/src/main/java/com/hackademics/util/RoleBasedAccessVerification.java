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
    private UserRepository userRepository;

    public boolean isAdmin(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        if (user.getRole() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "User role is not assigned.");
        }
        return user.getRole() == Role.ADMIN;
    }

    public boolean isCurrentUserRequestedStudentOrAdmin(UserDetails userDetails, Long studentId) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        return user.getStudentId().equals(studentId);
    }
}
