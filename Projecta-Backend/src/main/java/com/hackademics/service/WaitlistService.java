package com.hackademics.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.RequestDto.WaitlistDto;
import com.hackademics.dto.ResponseDto.WaitlistResponseDto;
import com.hackademics.dto.UpdateDto.WaitlistUpdateDto;

public interface WaitlistService {
    WaitlistResponseDto saveWaitlist(WaitlistDto waitlistDto, UserDetails currentUser);
    List<WaitlistResponseDto> getAllWaitlists(UserDetails currentUser);
    WaitlistResponseDto getWaitlistById(Long id, UserDetails currentUser);
    WaitlistResponseDto updateWaitlist(Long id, WaitlistUpdateDto waitlistUpdateDto, UserDetails currentUser);
    void deleteWaitlist(Long id, UserDetails currentUser);
}