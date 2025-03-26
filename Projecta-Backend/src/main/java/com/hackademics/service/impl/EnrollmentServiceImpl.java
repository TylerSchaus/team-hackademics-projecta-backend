package com.hackademics.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hackademics.dto.CourseResponseDto;
import com.hackademics.dto.EnrollmentDto;
import com.hackademics.dto.EnrollmentResponseDto;
import com.hackademics.dto.LabSectionResponseDto;
import com.hackademics.dto.StudentSummaryDto;
import com.hackademics.model.Course;
import com.hackademics.model.Enrollment;
import com.hackademics.model.LabSection;
import com.hackademics.model.Role;
import com.hackademics.model.User;
import com.hackademics.model.Waitlist;
import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.repository.CourseRepository;
import com.hackademics.repository.EnrollmentRepository;
import com.hackademics.repository.LabSectionRepository;
import com.hackademics.repository.UserRepository;
import com.hackademics.repository.WaitlistEnrollmentRepository;
import com.hackademics.repository.WaitlistRepository;
import com.hackademics.service.CourseService;
import com.hackademics.service.EnrollmentService;
import com.hackademics.service.LabSectionService;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private LabSectionRepository labSectionRepository;

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private WaitlistEnrollmentRepository waitlistEnrollmentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private LabSectionService labSectionService;

    private EnrollmentResponseDto convertToResponseDto(Enrollment enrollment) {
        CourseResponseDto courseDto = courseService.getCourseById(enrollment.getCourse().getId());
        StudentSummaryDto studentDto = new StudentSummaryDto(
                enrollment.getStudent().getId(),
                enrollment.getStudent().getFirstName(),
                enrollment.getStudent().getLastName(),
                enrollment.getStudent().getStudentId()
        );
        LabSectionResponseDto labSectionDto = enrollment.getLabSection() != null
                ? labSectionService.getLabSectionById(enrollment.getLabSection().getId()) : null;

        EnrollmentResponseDto responseDto = new EnrollmentResponseDto(
                enrollment.getId(),
                courseDto,
                studentDto,
                labSectionDto
        );
        return responseDto;
    }

    @Override
    public EnrollmentResponseDto saveEnrollment(EnrollmentDto enrollmentDto, UserDetails currentUser) {
        System.out.println("Entered saveEnrollment"); 
        // User verification
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        User student = userRepository.findByStudentId(enrollmentDto.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        if (authenticatedUser.getRole() != Role.ADMIN && !authenticatedUser.getStudentId().equals(student.getStudentId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Students can only enroll themselves.");
        }

        System.out.println("Got past RBA verfication. Still processing...");

        // Check that the course exists
        Course course = courseRepository.findById(enrollmentDto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        System.out.println("Course found. Still processing...");

        // Check if the student is already enrolled in the course
        List<Enrollment> currentEnrollments = enrollmentRepository.findByTermAndStudentId(course.getTerm(),student.getStudentId());

        boolean existingEnrollmentsEmpty = currentEnrollments.isEmpty();
        System.out.println("Existing enrollments empty? "+ existingEnrollmentsEmpty);
        
        Enrollment existingEnrollment = currentEnrollments.stream()
                .filter(e -> e.getCourse().getId().equals(course.getId()))
                .findFirst()
                .orElse(null);

        if (existingEnrollment != null) {
            // Redirect to updateEnrollment if the student is already in the course
            System.out.println("Existing enrollment found. Still processing...");
            return convertToResponseDto(updateEnrollment(existingEnrollment, enrollmentDto.getLabSectionId(), currentEnrollments));
        }

        System.out.println("No existing enrollment found. Still processing...");

        // Check for schedule conflicts
        if (hasScheduleConflict(currentEnrollments, course, null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schedule conflict detected with course section.");
        }

        System.out.println("No schedule conflicts with course found. Still processing...");

        // Check course capacity
        if (course.getCurrentEnroll() >= course.getEnrollLimit()) {
            // Query the waitlist repository for a waitlist associated with the course
            Waitlist waitlist = waitlistRepository.findByCourseId(course.getId());
            System.out.println("Waitlist accquired. Still processing...");
            if (waitlist != null) {
                // Proceed with waitlist logic
                List<WaitlistEnrollment> waitlistEnrollments = waitlistEnrollmentRepository.findByWaitlistId(waitlist.getId());
                if (waitlistEnrollments.size() < waitlist.getWaitlistLimit()) {
                    // Add the student to the waitlist
                    WaitlistEnrollment waitlistEnrollment = new WaitlistEnrollment(
                            waitlistEnrollments.size() + 1, // Next position in the waitlist
                            waitlist,
                            student
                    );
                    waitlistEnrollmentRepository.save(waitlistEnrollment);
                    throw new ResponseStatusException(HttpStatus.OK, "Student added to the waitlist.");
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course is at capacity and the waitlist is full.");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course is at capacity and does not have a waitlist.");
            }
        }

        System.out.println("Course is not at capacity or waitlist enrollment successful/skipped. Still processing...");

        // Proceed with creating the enrollment
        LabSection labSection = null;

        // Fetch lab section if provided
        if (enrollmentDto.getLabSectionId() != null) {
            System.out.println("Lab section ID provided, beginning enrollment process...");
            labSection = labSectionRepository.findById(enrollmentDto.getLabSectionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab section not found"));

            // Check for lab section schedule conflicts
            if (hasScheduleConflict(currentEnrollments, null, labSection)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schedule conflict with lab section.");
            }

            // Check lab section capacity
            if (labSection.getCurrentEnroll() >= labSection.getCapacity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lab section is at capacity.");
            }
        } else {
            // Auto-enroll in a lab section if none is provided
            List<LabSection> labSections = labSectionRepository.findByCourseId(course.getId());
            for (LabSection section : labSections) {
                if (!hasScheduleConflict(currentEnrollments, course, section) && section.getCurrentEnroll() < section.getCapacity()) {
                    labSection = section;
                    break;
                }
            }
        }

        boolean labSectionFound = labSection != null;
        System.out.println("Lab section found and okay? "+ labSectionFound);

        // Create and save the enrollment
        Enrollment enrollment = new Enrollment(course, student, labSection);
        enrollment.setTerm(course.getTerm()); // Ensure term is set from the course
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // Update course and lab section enrollment counts
        course.setCurrentEnroll(course.getCurrentEnroll() + 1);
        courseRepository.save(course);

        if (labSection != null) {
            labSection.setCurrentEnroll(labSection.getCurrentEnroll() + 1);
            labSectionRepository.save(labSection);
        }

        return convertToResponseDto(savedEnrollment);
    }

    private Enrollment updateEnrollment(Enrollment existingEnrollment, Long labSectionId, List<Enrollment> currentEnrollments) {
        LabSection newLabSection = null;
        Course course = existingEnrollment.getCourse();

        // Fetch the new lab section if provided
        if (labSectionId != null) {
            newLabSection = labSectionRepository.findById(labSectionId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lab section not found"));

            // Check for schedule conflicts
            if (hasScheduleConflict(currentEnrollments, null, newLabSection)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schedule conflict with new lab section.");
            }

            // Check lab section capacity
            if (newLabSection.getCurrentEnroll() >= newLabSection.getCapacity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New lab section is at capacity.");
            }

            // Ensure the lab section corresponds to the course
            if (!newLabSection.getCourse().getId().equals(existingEnrollment.getCourse().getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lab section does not correspond to the course.");
            }
        }
        else {
            // Auto-enroll in a lab section if none is provided
            List<LabSection> labSections = labSectionRepository.findByCourseId(course.getId());
            for (LabSection section : labSections) {
                if (!hasScheduleConflict(currentEnrollments, course, section) && section.getCurrentEnroll() < section.getCapacity()) {
                    newLabSection = section;
                    break;
                }
            }
        }  
        // Update the lab section in the enrollment
        LabSection oldLabSection = existingEnrollment.getLabSection();
        existingEnrollment.setLabSection(newLabSection);
        Enrollment updatedEnrollment = enrollmentRepository.save(existingEnrollment);

        // Update lab section enrollment counts
        if (oldLabSection != null) {
            oldLabSection.setCurrentEnroll(oldLabSection.getCurrentEnroll() - 1);
            labSectionRepository.save(oldLabSection);
        }

        if (newLabSection != null) {
            newLabSection.setCurrentEnroll(newLabSection.getCurrentEnroll() + 1);
            labSectionRepository.save(newLabSection);
        }

        return updatedEnrollment;
    }

    @Override
    public List<EnrollmentResponseDto> getAllEnrollments(UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view all enrollments.");
        }

        return enrollmentRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponseDto> getEnrollmentsByCourseId(Long courseId, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (!courseRepository.existsById(courseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found.");
        }

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view enrollments for a course.");
        }

        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponseDto> getEnrollmentsByStudentId(Long studentId, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() == Role.STUDENT && !authenticatedUser.getStudentId().equals(studentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Students can only view their own enrollments.");
        }

        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public EnrollmentResponseDto getEnrollmentById(Long id, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

        if (authenticatedUser.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view specific enrollments.");
        }

        return convertToResponseDto(enrollment);
    }

    @Override
    public void deleteEnrollment(Long id, UserDetails currentUser) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));

        Course course = enrollment.getCourse();

        if (authenticatedUser.getRole() != Role.ADMIN && !authenticatedUser.getStudentId().equals(enrollment.getStudent().getStudentId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Students can only delete their own enrollments.");
        }

        enrollmentRepository.deleteById(id);

        if (course.getCurrentEnroll() > 0) {
            course.setCurrentEnroll(course.getCurrentEnroll() - 1);
            courseRepository.save(course);
        }
    }

    @Override
    public List<EnrollmentResponseDto> getCurrentEnrollmentByStudentId(UserDetails currentUser, Long studentId, String term) {
        User authenticatedUser = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (authenticatedUser.getRole() == Role.STUDENT && !authenticatedUser.getStudentId().equals(studentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Students can only view their own enrollments.");
        }

        String currentTerm = term;

        if (currentTerm == null) {
            currentTerm = switch (LocalDateTime.now().getMonth()) {
                case SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER ->
                    LocalDateTime.now().getYear() + 1 + "1";
                case JANUARY, FEBRUARY, MARCH, APRIL ->
                    LocalDateTime.now().getYear() + "2";
                default ->
                    "UNDETERMINED";
            };
        }

        // Only validate term format if it's not "UNDETERMINED"
        if (!currentTerm.equals("UNDETERMINED")) {
            // Check if the term ends with 1 or 2
            if (!currentTerm.matches("\\d{4}[12]")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid term format. Must be in the format YYYY1 or YYYY2.");
            }
        }

        return enrollmentRepository.findByTermAndStudentId(currentTerm, studentId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    // Helper method
    private boolean hasScheduleConflict(List<Enrollment> currentEnrollments, Course course, LabSection labSection) {
        System.out.println("Checking for schedule conflicts...");
        
        // First check for course conflicts
        if (course != null) {
            for (Enrollment enrollment : currentEnrollments) {
                // Skip if this is the same course (for updating enrollments)
                if (enrollment.getCourse().getId().equals(course.getId())) {
                    continue;
                }
                
                // Check if the days overlap
                if (enrollment.getCourse().getDays().equals(course.getDays())) {
                    // Check for time overlap between courses
                    if (!(enrollment.getCourse().getEndTime().isBefore(course.getStartTime()) ||
                          enrollment.getCourse().getStartTime().isAfter(course.getEndTime()))) {
                        return true;
                    }
                }
            }
        }
        
        // Then check for lab section conflicts if a lab section is provided
        if (labSection != null) {
            for (Enrollment enrollment : currentEnrollments) {
                // Skip if this is the same lab section (for updating enrollments)
                if (enrollment.getLabSection() != null && 
                    enrollment.getLabSection().getId().equals(labSection.getId())) {
                    continue;
                }
                
                // Check for conflicts with existing lab sections
                if (enrollment.getLabSection() != null) {
                    // Check if they're on the same day
                    if (enrollment.getLabSection().getDays() == labSection.getDays()) {
                        // Check for time overlap
                        if (!(enrollment.getLabSection().getEndTime().isBefore(labSection.getStartTime()) ||
                              enrollment.getLabSection().getStartTime().isAfter(labSection.getEndTime()))) {
                            return true;
                        }
                    }
                }
                
                // Check for conflicts with existing courses
                if (enrollment.getCourse().getDays() == labSection.getDays()) {
                    // Check for time overlap between course and lab section
                    if (!(enrollment.getCourse().getEndTime().isBefore(labSection.getStartTime()) ||
                          enrollment.getCourse().getStartTime().isAfter(labSection.getEndTime()))) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
}
