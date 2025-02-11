package com.hackademics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackademics.dto.LoginDto;
import com.hackademics.dto.LoginResponse;
import com.hackademics.dto.SignUpDto;
import com.hackademics.model.Administrator;
import com.hackademics.model.User;
import com.hackademics.service.AuthenticationService;
import com.hackademics.service.JwtService;

@RestController
@RequestMapping("/auth/admin")
public class AdminAuthController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AdminAuthController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Administrator> register(@RequestBody SignUpDto signUpDto) {
        Administrator registeredAdmin = authenticationService.signupAdmin(signUpDto);
        return ResponseEntity.ok(registeredAdmin);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginDto loginDto) {
        User authenticatedUser = authenticationService.authenticate(loginDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
