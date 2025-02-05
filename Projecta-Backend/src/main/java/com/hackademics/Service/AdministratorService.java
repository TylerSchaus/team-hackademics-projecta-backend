package com.hackademics.Service;
import com.hackademics.Model.Administrator;
import com.hackademics.Repository.AdministratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdministratorService {

    private final AdministratorRepository administratorRepository;

    @Autowired
    public AdministratorService(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }

    // Get all administrators
    public List<Administrator> getAllAdministrators() {
        return administratorRepository.findAll();
    }

    // Get an administrator by ID
    public Optional<Administrator> getAdministratorById(Long adminId) {
        return administratorRepository.findById(adminId);
    }

    // Create a new administrator
    public Administrator createAdministrator(Administrator administrator) {
        return administratorRepository.save(administrator);
    }

    // Update an existing administrator
    public Administrator updateAdministrator(Long adminId, Administrator updatedAdministrator) {
        return administratorRepository.findById(adminId)
                .map(administrator -> {
                    // Update fields if needed
                    return administratorRepository.save(administrator);
                })
                .orElseThrow(() -> new RuntimeException("Administrator not found with id: " + adminId));
    }

    // Delete an administrator by ID
    public void deleteAdministrator(Long adminId) {
        administratorRepository.deleteById(adminId);
    }
}
