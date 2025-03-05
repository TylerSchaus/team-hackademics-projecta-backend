package com.hackademics.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    private Gender gender; // MALE, FEMALE, OTHER

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // STUDENT or ADMIN

    // Student-specific fields

    @Getter
    @Setter
    @Column(name = "student_id")
    private Long studentId;

    @Getter
    @Setter
    private LocalDateTime enrollStartDate;

    @Getter
    @Setter
    private LocalDateTime expectGraduationDate;

    
    @Getter 
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Grade> grades = new ArrayList<>();

    @Getter
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Enrollment> enrollments = new ArrayList<>();

    @Getter
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<WaitlistEnrollment> waitListEnrollments = new ArrayList<>();

    // Admin specific properties
    @Getter
    @Setter
    @Column(name = "admin_id")
    private Long adminId;

    @Getter
    @OneToMany(mappedBy = "admin", cascade = CascadeType.PERSIST)
    private List<Course> courses = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

}
