package com.hackademics.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "administrators")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Administrator {

    @Id
    @Column(name = "admin_id", length = 5)
    private String adminId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;
}
