package com.hackademics.Service;

import com.hackademics.model.Waitlist;
import com.hackademics.repository.WaitlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WaitlistServiceImpl implements WaitlistService {

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Override
    public Waitlist saveWaitlist(Waitlist waitlist) {
        return waitlistRepository.save(waitlist);
    }

    @Override
    public List<Waitlist> getAllWaitlists() {
        return waitlistRepository.findAll();
    }

    @Override
    public Waitlist getWaitlistById(Long id) {
        return waitlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waitlist not found with ID: " + id));
    }

    @Override
    public Waitlist updateWaitlist(Waitlist waitlist) {
        return waitlistRepository.save(waitlist);
    }

    @Override
    public void deleteWaitlist(Long id) {
        waitlistRepository.deleteById(id);
    }
}