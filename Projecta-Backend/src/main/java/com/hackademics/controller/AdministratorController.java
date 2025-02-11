package com.hackademics.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackademics.dto.SignUpDto;
import com.hackademics.model.Administrator;
import com.hackademics.model.User;
import com.hackademics.service.AdministratorService;

@RestController
@RequestMapping("/api/administrators")
public class AdministratorController {

    private final AdministratorService administratorService;

    public AdministratorController(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @GetMapping
    public ResponseEntity<List<Administrator>> getAllAdministrators() {
        return ResponseEntity.ok(administratorService.getAllAdministrators());
    }

     @GetMapping("/me")
    public ResponseEntity<Administrator> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User user) { 
            return administratorService.getAdministratorByEmail(user.getUsername()) 
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping
    public ResponseEntity<Administrator> createAdministrator(@RequestBody SignUpDto signUpDto) {
        Administrator administrator = new Administrator();
        administrator.setFirstName(signUpDto.getFirstName());
        administrator.setLastName(signUpDto.getLastName());
        administrator.setEmail(signUpDto.getEmail());
        administrator.setPassword(signUpDto.getPassword());
        return ResponseEntity.ok(administratorService.createAdministrator(administrator));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Administrator> updateAdministrator(@PathVariable Long id, @RequestBody Administrator administrator) {
        return ResponseEntity.ok(administratorService.updateAdministrator(id, administrator));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdministrator(@PathVariable Long id) {
        administratorService.deleteAdministrator(id);
        return ResponseEntity.noContent().build();
    }
}