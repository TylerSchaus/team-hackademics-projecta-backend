package com.hackademics.dto;

import com.hackademics.model.Role;

import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class SignUpDto {
    
    @Getter
    @Setter
    @NotBlank(message = "First name is required")
    private String firstName;

    @Getter
    @Setter
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Getter
    @Setter
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Getter
    @Setter
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @Getter
    @Setter
    @NotNull(message = "Role is required")
    @Enumerated
    private Role role;

}
