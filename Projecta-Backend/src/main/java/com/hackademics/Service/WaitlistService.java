package com.hackademics.Service;

import com.hackademics.model.Waitlist;
import java.util.List;

public interface WaitlistService {
    Waitlist saveWaitlist(Waitlist waitlist);
    List<Waitlist> getAllWaitlists();
    Waitlist getWaitlistById(Long id);
    Waitlist updateWaitlist(Waitlist waitlist);
    void deleteWaitlist(Long id);
}
