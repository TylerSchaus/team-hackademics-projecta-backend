package com.hackademics.Model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(length = 50, nullable = false)
    private String firstName;

    @Getter
    @Setter
    @Column(length = 50, nullable = false)
    private String lastName;

    @Getter
    @Setter
    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Getter
    @Setter
    @Column(length = 255, nullable = false)
    private String password;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(/* nullable = false */)
    private Gender gender;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // STUDENT or ADMIN

    // Student-specific fields
    @Getter
    @Setter
    private LocalDateTime enrollStartDate;

    @Getter
    @Setter
    private LocalDateTime expectGraduationDate;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }


}
