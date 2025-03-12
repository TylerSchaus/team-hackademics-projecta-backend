package com.hackademics.Service;

import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.repository.WaitlistEnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WaitlistEnrollmentServiceImpl implements WaitlistEnrollmentService {

    @Autowired
    private WaitlistEnrollmentRepository waitlistEnrollmentRepository;

    @Override
    public WaitlistEnrollment saveWaitlistEnrollment(WaitlistEnrollment waitlistEnrollment) {
        return waitlistEnrollmentRepository.save(waitlistEnrollment);
    }

    @Override
    public List<WaitlistEnrollment> getAllWaitlistEnrollments() {
        return waitlistEnrollmentRepository.findAll();
    }

    @Override
    public WaitlistEnrollment getWaitlistEnrollmentById(Long id) {
        return waitlistEnrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WaitlistEnrollment not found with ID: " + id));
    }

    @Override
    public WaitlistEnrollment updateWaitlistEnrollment(WaitlistEnrollment waitlistEnrollment) {
        return waitlistEnrollmentRepository.save(waitlistEnrollment);
    }

    @Override
    public void deleteWaitlistEnrollment(Long id) {
        waitlistEnrollmentRepository.deleteById(id);
    }
}