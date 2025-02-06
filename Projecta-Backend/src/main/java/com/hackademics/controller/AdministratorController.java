package com.hackademics.controller;

import com.hackademics.dto.SignUpDto;
import com.hackademics.model.Administrator;
import com.hackademics.model.Student;
import com.hackademics.service.AdministratorService;
import com.hackademics.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/{id}")
    public ResponseEntity<Administrator> getAdministratorById(@PathVariable Long id) {
        Optional<Administrator> administrator = administratorService.getAdministratorById(id);
        return administrator.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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