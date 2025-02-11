package com.hackademics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.hackademics.repository.AdministratorRepository;
import com.hackademics.repository.StudentRepository;

@Configuration
public class ApplicationConfiguration {

    private final AdministratorRepository administratorRepository;
    private final StudentRepository studentRepository;

    public ApplicationConfiguration(AdministratorRepository administratorRepository, StudentRepository studentRepository) {
        this.administratorRepository = administratorRepository;
        this.studentRepository = studentRepository;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return userEmail -> {
            return administratorRepository.findByEmail(userEmail)
                    .map(admin -> (UserDetails) admin) // Explicitly cast to UserDetails
                    .orElseGet(() -> studentRepository.findByEmail(userEmail)
                    .map(student -> (UserDetails) student)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found")));
        };
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
