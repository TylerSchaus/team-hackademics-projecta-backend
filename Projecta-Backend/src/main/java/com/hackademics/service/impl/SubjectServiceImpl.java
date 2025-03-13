package com.hackademics.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.SubjectDto;
import com.hackademics.model.Role;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.service.SubjectService;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    @Override
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Override
    public Subject createSubject(SubjectDto subjectDto, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can create subjects.");
        }

        Subject newSubject = new Subject(subjectDto.getSubjectName(), subjectDto.getSubjectTag());
        return subjectRepository.save(newSubject);
    }

    @Override
    public Optional<Subject> updateSubject(Long id, SubjectDto updatedSubjectDto, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can update subjects.");
        }

        return subjectRepository.findById(id).map(subject -> {
            subject.setSubjectName(updatedSubjectDto.getSubjectName());
            subject.setSubjectTag(updatedSubjectDto.getSubjectTag());
            return subjectRepository.save(subject);
        });
    }

    @Override
    public boolean deleteSubject(Long id, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete subjects.");
        }

        if (!subjectRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found.");
        }

        subjectRepository.deleteById(id);
        return true;
    }
}
