package com.hackademics.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class LoginDto {

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

    
}