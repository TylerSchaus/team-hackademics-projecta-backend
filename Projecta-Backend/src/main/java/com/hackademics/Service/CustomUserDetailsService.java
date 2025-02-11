package com.hackademics.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hackademics.repository.AdministratorRepository;
import com.hackademics.repository.StudentRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdministratorRepository adminRepository;
    private final StudentRepository studentRepository;

    public CustomUserDetailsService(AdministratorRepository adminRepository, StudentRepository studentRepository) {
        this.adminRepository = adminRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return adminRepository.findByEmail(email)
                .map(admin -> (UserDetails) admin)
                .orElseGet(() -> studentRepository.findByEmail(email)
                        .map(student -> (UserDetails) student)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }
}
