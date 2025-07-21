package com.matchingapp.service;

import com.matchingapp.entity.Nursery;
import com.matchingapp.repository.NurseryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NurseryService {

    private final NurseryRepository nurseryRepository;

    public NurseryService(NurseryRepository nurseryRepository) {
        this.nurseryRepository = nurseryRepository;
    }

    public Optional<Nursery> getNurseryById(Long id) {
        return nurseryRepository.findById(id);
    }

    public Optional<Nursery> getNurseryByEmail(String email) {
        return nurseryRepository.findByEmail(email);
    }

    @Transactional
    public Nursery saveNurseryProfile(Nursery nursery) {
        nursery.setUpdatedAt(LocalDateTime.now());
        return nurseryRepository.save(nursery);
    }

    public List<Nursery> getAllNurseries() {
        return nurseryRepository.findAll();
    }
}