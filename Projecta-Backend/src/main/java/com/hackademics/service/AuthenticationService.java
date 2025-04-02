package com.hackademics.service;

import com.hackademics.dto.RequestDto.LoginDto;
import com.hackademics.dto.RequestDto.SignUpDto;
import com.hackademics.dto.ResponseDto.UserResponseDTO;
import com.hackademics.model.User;

public interface AuthenticationService {
    UserResponseDTO signupUser(SignUpDto input);

    User authenticate(LoginDto input);
}
