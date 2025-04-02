package com.hackademics.util;

import com.hackademics.dto.ResponseDto.AdminSummaryDto;
import com.hackademics.dto.ResponseDto.CourseResponseDto;
import com.hackademics.dto.ResponseDto.CourseSummaryDto;
import com.hackademics.dto.ResponseDto.EnrollmentResponseDto;
import com.hackademics.dto.ResponseDto.GradeResponseDto;
import com.hackademics.dto.ResponseDto.LabSectionResponseDto;
import com.hackademics.dto.ResponseDto.StudentSummaryDto;
import com.hackademics.dto.ResponseDto.SubjectResponseDto;
import com.hackademics.dto.ResponseDto.UserResponseDTO;
import com.hackademics.dto.ResponseDto.WaitlistEnrollmentResponseDto;
import com.hackademics.dto.ResponseDto.WaitlistRequestResponseDto;
import com.hackademics.dto.ResponseDto.WaitlistResponseDto;
import com.hackademics.dto.ResponseDto.WaitlistSummaryDto;
import com.hackademics.model.Course;
import com.hackademics.model.Enrollment;
import com.hackademics.model.Grade;
import com.hackademics.model.LabSection;
import com.hackademics.model.Subject;
import com.hackademics.model.User;
import com.hackademics.model.Waitlist;
import com.hackademics.model.WaitlistEnrollment;
import com.hackademics.model.WaitlistRequest;

public class ConvertToResponseDto {
    
    private ConvertToResponseDto() {
        // Private constructor to prevent instantiation
    }
    
    private static AdminSummaryDto convertAdminToAdminSummaryDto(User admin) {
        return new AdminSummaryDto(
            admin.getId(),
            admin.getFirstName(),
            admin.getLastName(),
            admin.getAdminId()
        );
    }

    private static StudentSummaryDto convertStudentToStudentSummaryDto(User student) {
        return new StudentSummaryDto(
            student.getId(),
            student.getFirstName(),
            student.getLastName(),
            student.getStudentId(),
            student.getMajor()
        );
    }

    public static SubjectResponseDto convertToSubjectResponseDto(Subject subject) {
        return new SubjectResponseDto(
            subject.getId(),
            subject.getSubjectName(),
            subject.getSubjectTag()
        );
    }

    private static CourseSummaryDto convertToCourseSummaryDto(Course course) {
        return new CourseSummaryDto(
            course.getId(),
            convertAdminToAdminSummaryDto(course.getAdmin()),
            convertToSubjectResponseDto(course.getSubject()),
            course.getCourseName(),
            course.getStartDate().toLocalDate(),
            course.getEndDate().toLocalDate(),
            course.getEnrollLimit(),
            course.getCurrentEnroll(),
            course.getCourseNumber(),
            course.getCourseTag(),
            course.getTerm(),
            course.getDays(),
            course.getStartTime(),
            course.getEndTime(),
            course.getNumLabSections()
        );
    }

    public static CourseResponseDto convertToCourseResponseDto(Course course, Waitlist waitlist) {
        AdminSummaryDto adminDto = convertAdminToAdminSummaryDto(course.getAdmin());
        SubjectResponseDto subjectDto = convertToSubjectResponseDto(course.getSubject());

        CourseResponseDto responseDto = new CourseResponseDto(
                course.getId(),
                adminDto,
                subjectDto,
                course.getCourseName(),
                course.getStartDate().toLocalDate(),
                course.getEndDate().toLocalDate(),
                course.getEnrollLimit(),
                course.getCurrentEnroll(),
                course.getCourseNumber(),
                course.getCourseTag(),
                course.getTerm(),
                course.getDays(),
                course.getStartTime(),
                course.getEndTime(),
                course.getNumLabSections()
        );

        // Handle waitlist logic directly here
        if (waitlist != null && course.getCurrentEnroll() >= course.getEnrollLimit() && Boolean.TRUE.equals(course.isWaitlistAvailable())) {
            if (waitlist.getWaitlistEnrollments().size() < waitlist.getWaitlistLimit()) {
                responseDto.setWaitlist(new WaitlistSummaryDto(
                    waitlist.getId(),
                    waitlist.getWaitlistLimit(),
                    waitlist.getWaitlistEnrollments().size()
                ));
            }
        }

        return responseDto;
    }

    public static WaitlistResponseDto convertWaitlistToResponseDto(Waitlist waitlist, Course course) {
        return new WaitlistResponseDto(
            waitlist.getId(),
            waitlist.getWaitlistLimit(),
            convertToCourseSummaryDto(course)
        );
    }

    public static GradeResponseDto convertToGradeResponseDto(Grade grade) {
        return new GradeResponseDto(
            grade.getId(),
            grade.getGrade(),
            convertStudentToStudentSummaryDto(grade.getStudent()),
            convertToCourseResponseDto(grade.getCourse(), null) // No waitlist needed for grade view
        );
    }

    public static LabSectionResponseDto convertToLabSectionResponseDto(LabSection labSection) {
        return new LabSectionResponseDto(
            labSection.getId(),
            labSection.getSectionId(),
            labSection.getCapacity(),
            labSection.getCurrentEnroll(),
            convertToCourseResponseDto(labSection.getCourse(), null), // No waitlist needed for lab section view
            labSection.getDays(),
            labSection.getStartTime(),
            labSection.getEndTime(),
            labSection.getStartDate().toLocalDate(),
            labSection.getEndDate().toLocalDate()
        );
    }

    public static UserResponseDTO convertToUserResponseDto(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole().toString(),
                user.getStudentId(),
                user.getEnrollStartDate() != null ? user.getEnrollStartDate().toLocalDate() : null,
                user.getExpectGraduationDate() != null ? user.getExpectGraduationDate().toLocalDate() : null,
                user.getAdminId(),
                user.getMajor()
        );
    }

    public static WaitlistEnrollmentResponseDto convertToWaitlistEnrollmentResponseDto(WaitlistEnrollment enrollment) {
        return new WaitlistEnrollmentResponseDto(
            enrollment.getId(),
            enrollment.getWaitlistPosition(),
            convertWaitlistToResponseDto(enrollment.getWaitlist(), enrollment.getWaitlist().getCourse()),
            convertStudentToStudentSummaryDto(enrollment.getStudent()),
            enrollment.getTerm()
        );
    }

    public static EnrollmentResponseDto convertToResponseDto(Enrollment enrollment) {
        CourseResponseDto courseDto = convertToCourseResponseDto(enrollment.getCourse(), null);
        StudentSummaryDto studentDto = convertStudentToStudentSummaryDto(enrollment.getStudent());
        LabSectionResponseDto labSectionDto = enrollment.getLabSection() != null
                ? convertToLabSectionResponseDto(enrollment.getLabSection()) : null;

        EnrollmentResponseDto responseDto = new EnrollmentResponseDto(
                enrollment.getId(),
                courseDto,
                studentDto,
                labSectionDto
        );

        return responseDto;
    }

    public static WaitlistRequestResponseDto convertToWaitlistRequestResponseDto(WaitlistRequest request) {
        return new WaitlistRequestResponseDto(
            request.getId(),
            new WaitlistSummaryDto(
                request.getWaitlist().getId(),
                request.getWaitlist().getWaitlistLimit(),
                request.getWaitlist().getWaitlistEnrollments().size()
            ),
            convertStudentToStudentSummaryDto(request.getStudent())
        );
    }


}
