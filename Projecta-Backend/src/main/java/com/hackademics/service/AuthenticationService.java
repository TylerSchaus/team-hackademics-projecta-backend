package com.hackademics.service;

import com.hackademics.dto.LoginDto;
import com.hackademics.dto.SignUpDto;
import com.hackademics.model.User;

public interface AuthenticationService {
    User signupUser(SignUpDto input);


    User authenticate(LoginDto input);
}
