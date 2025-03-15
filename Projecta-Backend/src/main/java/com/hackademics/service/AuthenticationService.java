package com.hackademics.service;

import com.hackademics.dto.LoginDto;
import com.hackademics.dto.SignUpDto;
import com.hackademics.model.User;

public interface AuthenticationService {
    User signupAdmin(SignUpDto input);

    User signupStudent(SignUpDto input);

    User authenticate(LoginDto input);
}
