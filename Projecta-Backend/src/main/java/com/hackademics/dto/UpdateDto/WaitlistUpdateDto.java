package com.hackademics.dto.UpdateDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class WaitlistUpdateDto {
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    public WaitlistUpdateDto() {
    }

    public WaitlistUpdateDto(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
} 