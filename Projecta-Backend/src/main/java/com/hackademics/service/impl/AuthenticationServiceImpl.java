package com.hackademics.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hackademics.dto.LoginDto;
import com.hackademics.dto.SignUpDto;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.AuthenticationService;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public User signupUser(SignUpDto input) {
        Long specialId = input.getRole() == Role.ADMIN ? generateNextAdminId() : generateNextStudentId(); 
        User admin = new User(input.getFirstName(), input.getLastName(), input.getEmail(), input.getPassword(), input.getRole(), specialId);
        return userRepository.save(admin);
    }

    @Override
    public User authenticate(LoginDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Other methods for user management...
    private Long generateNextStudentId() {
        Long maxStudentId = userRepository.findMaxStudentId();
        return (maxStudentId != null) ? maxStudentId + 1 : 1L;
    }

    private Long generateNextAdminId() {
        Long maxAdminId = userRepository.findMaxAdminId();
        return (maxAdminId != null) ? maxAdminId + 1 : 1L;
    }
}
