package com.hackademics.dto.UpdateDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class GradeUpdateDto {
    @NotNull(message = "Grade is required")
    @Min(value = 0, message = "Grade must be between 0 and 100")
    @Max(value = 100, message = "Grade must be between 0 and 100")
    private Double grade;

    public GradeUpdateDto() {
    }

    public GradeUpdateDto(Double grade) {
        this.grade = grade;
    }

    public Double getGrade() {
        return grade;
    }
} 