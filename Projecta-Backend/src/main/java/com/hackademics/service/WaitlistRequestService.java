package com.hackademics.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.RequestDto.WaitlistRequestDto;
import com.hackademics.dto.ResponseDto.WaitlistRequestResponseDto;

public interface WaitlistRequestService {
    WaitlistRequestResponseDto saveWaitlistRequest(WaitlistRequestDto waitlistRequestDto, UserDetails currentUser);
    List<WaitlistRequestResponseDto> getAllWaitlistRequests(UserDetails currentUser);
    void deleteWaitlistRequest(Long id, UserDetails currentUser);
}