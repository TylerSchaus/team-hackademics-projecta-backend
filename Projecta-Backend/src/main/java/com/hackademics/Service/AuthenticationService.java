package com.hackademics.service;

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

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public User signupAdmin(SignUpDto input) {
        User admin = new User();
        admin.setFirstName(input.getFirstName());
        admin.setLastName(input.getLastName());
        admin.setEmail(input.getEmail());
        admin.setPassword(passwordEncoder.encode(input.getPassword()));
        admin.setRole(Role.ADMIN);

        return userRepository.save(admin);
    }

    public User signupStudent(SignUpDto input) { 
        User student = new User();
        student.setFirstName(input.getFirstName());
        student.setLastName(input.getLastName());
        student.setEmail(input.getEmail());
        student.setPassword(passwordEncoder.encode(input.getPassword()));
        student.setRole(Role.STUDENT);

        return userRepository.save(student);
    }

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
}
