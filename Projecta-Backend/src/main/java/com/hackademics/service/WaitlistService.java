package com.hackademics.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.hackademics.dto.RequestDto.WaitlistDto;
import com.hackademics.dto.ResponseDto.WaitlistResponseDto;
import com.hackademics.dto.UpdateDto.WaitlistUpdateDto;
import com.hackademics.model.Waitlist;

public interface WaitlistService {
    WaitlistResponseDto convertToResponseDto(Waitlist waitlist);
    WaitlistResponseDto saveWaitlist(WaitlistDto waitlistDto, UserDetails currentUser);
    List<WaitlistResponseDto> getAllWaitlists(UserDetails currentUser);
    WaitlistResponseDto getWaitlistById(Long id, UserDetails currentUser);
    WaitlistResponseDto updateWaitlist(Long id, WaitlistUpdateDto waitlistUpdateDto, UserDetails currentUser);
    void deleteWaitlist(Long id, UserDetails currentUser);
}