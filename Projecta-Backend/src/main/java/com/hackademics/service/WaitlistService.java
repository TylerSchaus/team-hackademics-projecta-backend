package com.hackademics.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.WaitlistDto;
import com.hackademics.dto.WaitlistUpdateDto;
import com.hackademics.dto.WaitlistResponseDto;

public interface WaitlistService {
    WaitlistResponseDto saveWaitlist(WaitlistDto waitlistDto, UserDetails currentUser);
    List<WaitlistResponseDto> getAllWaitlists(UserDetails currentUser);
    WaitlistResponseDto getWaitlistById(Long id, UserDetails currentUser);
    WaitlistResponseDto updateWaitlist(Long id, WaitlistUpdateDto waitlistUpdateDto, UserDetails currentUser);
    void deleteWaitlist(Long id, UserDetails currentUser);
}