package com.hackademics.service;

import java.util.List;

import com.hackademics.model.Waitlist;

public interface WaitlistService {
    Waitlist saveWaitlist(Waitlist waitlist);
    List<Waitlist> getAllWaitlists();
    Waitlist getWaitlistById(Long id);
    Waitlist updateWaitlist(Waitlist waitlist);
    void deleteWaitlist(Long id);
}