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
import com.hackademics.model.Subject;
import com.hackademics.repository.SubjectRepository;
import com.hackademics.service.SubjectService;
import com.hackademics.util.ConvertToResponseDto;
import com.hackademics.util.RoleBasedAccessVerification;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private RoleBasedAccessVerification roleBasedAccessVerification;

    @Override
    public Optional<SubjectResponseDto> getSubjectById(Long id) {
        return subjectRepository.findById(id)
            .map(ConvertToResponseDto::convertToSubjectResponseDto);
    }

    @Override
    public List<SubjectResponseDto> getAllSubjects() {
        return subjectRepository.findAll().stream()
            .map(ConvertToResponseDto::convertToSubjectResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public SubjectResponseDto createSubject(SubjectDto subjectDto, UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
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
        return ConvertToResponseDto.convertToSubjectResponseDto(subjectRepository.save(newSubject));
    }

    @Override
    public Optional<SubjectResponseDto> updateSubject(Long id, SubjectUpdateDto updatedSubjectDto, UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
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

            return ConvertToResponseDto.convertToSubjectResponseDto(subjectRepository.save(subject));
        });
    }

    @Override
    public boolean deleteSubject(Long id, UserDetails currentUser) {
        if (!roleBasedAccessVerification.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can delete subjects.");
        }

        if (!subjectRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found.");
        }

        subjectRepository.deleteById(id);
        return true;
    }
}
