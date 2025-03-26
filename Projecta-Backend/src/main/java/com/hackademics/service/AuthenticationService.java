package com.hackademics.service;

import com.hackademics.dto.LoginDto;
import com.hackademics.dto.SignUpDto;
import com.hackademics.dto.UserResponseDTO;
import com.hackademics.model.User;

public interface AuthenticationService {
    UserResponseDTO signupUser(SignUpDto input);

    User authenticate(LoginDto input);
}
