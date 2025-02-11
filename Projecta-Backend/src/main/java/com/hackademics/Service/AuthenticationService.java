package com.hackademics.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hackademics.dto.LoginDto;
import com.hackademics.dto.SignUpDto;
import com.hackademics.model.Administrator;
import com.hackademics.model.Student;
import com.hackademics.model.User;
import com.hackademics.repository.AdministratorRepository;
import com.hackademics.repository.StudentRepository;

@Service
public class AuthenticationService {

    private final AdministratorRepository administratorRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            AdministratorRepository administratorRepository,
            StudentRepository studentRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.administratorRepository = administratorRepository;
        this.studentRepository = studentRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public Administrator signupAdmin(SignUpDto input) {
        Administrator admin = new Administrator();
        admin.setFirstName(input.getFirstName());
        admin.setLastName(input.getLastName());
        admin.setEmail(input.getEmail());
        admin.setPassword(passwordEncoder.encode(input.getPassword()));

        return administratorRepository.save(admin);
    }

    public Student signupStudent(SignUpDto input) { 
        Student student = new Student();
        student.setFirstName(input.getFirstName());
        student.setLastName(input.getLastName());
        student.setEmail(input.getEmail());
        student.setPassword(passwordEncoder.encode(input.getPassword()));

        return studentRepository.save(student);
    }

    public User authenticate(LoginDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return administratorRepository.findByEmail(input.getEmail())
                .map(admin -> (User) admin) 
                .orElseGet(() -> studentRepository.findByEmail(input.getEmail())
                        .map(student -> (User) student) 
                        .orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }
}
