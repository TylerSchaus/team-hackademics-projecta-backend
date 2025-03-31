package com.hackademics.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.RequestDto.SubjectDto;
import com.hackademics.dto.ResponseDto.SubjectResponseDto;
import com.hackademics.dto.UpdateDto.SubjectUpdateDto;
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

    private SubjectResponseDto convertToResponseDto(Subject subject) {
        return new SubjectResponseDto(
            subject.getId(),
            subject.getSubjectName(),
            subject.getSubjectTag()
        );
    }

    @Override
    public Optional<SubjectResponseDto> getSubjectById(Long id) {
        return subjectRepository.findById(id)
            .map(this::convertToResponseDto);
    }

    @Override
    public List<SubjectResponseDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public SubjectResponseDto createSubject(SubjectDto subjectDto, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can create subjects.");
        }

        // Check for duplicate subject name
        if (subjectRepository.existsBySubjectName(subjectDto.getSubjectName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A subject with this name already exists.");
        }

        // Check for duplicate subject tag
        if (subjectRepository.existsBySubjectTag(subjectDto.getSubjectTag())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A subject with this tag already exists.");
        }

        Subject newSubject = new Subject(subjectDto.getSubjectName(), subjectDto.getSubjectTag());
        return convertToResponseDto(subjectRepository.save(newSubject));
    }

    @Override
    public Optional<SubjectResponseDto> updateSubject(Long id, SubjectUpdateDto updatedSubjectDto, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can update subjects.");
        }

        return subjectRepository.findById(id).map(subject -> {
            // Update fields only if they are provided (non-null)
            if (updatedSubjectDto.getSubjectName() != null) {
                subject.setSubjectName(updatedSubjectDto.getSubjectName());
            }
            if (updatedSubjectDto.getSubjectTag() != null) {
                subject.setSubjectTag(updatedSubjectDto.getSubjectTag());
            }

            return convertToResponseDto(subjectRepository.save(subject));
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
