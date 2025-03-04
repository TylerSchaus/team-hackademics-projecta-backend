package com.hackademics.dto;

import com.hackademics.model.Gender;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {
    private String firstName;
    private String lastName;
    private Gender gender;
    private String email;
    private Long studentId; 
}

