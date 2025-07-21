package com.matchingapp.service;

import com.matchingapp.entity.Favorite;
import com.matchingapp.entity.JobPosting;
import com.matchingapp.entity.User;
import com.matchingapp.repository.FavoriteRepository;
import com.matchingapp.repository.JobPostingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final JobPostingRepository jobPostingRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, JobPostingRepository jobPostingRepository) {
        this.favoriteRepository = favoriteRepository;
        this.jobPostingRepository = jobPostingRepository;
    }

    @Transactional
    public Favorite addFavorite(User user, Long jobPostingId) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("Job Posting not found"));

        if (favoriteRepository.findByUserAndJobPosting(user, jobPosting).isPresent()) {
            throw new IllegalStateException("Job Posting is already favorited by this user.");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setJobPosting(jobPosting);
        return favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(User user, Long jobPostingId) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("Job Posting not found"));

        Favorite favorite = favoriteRepository.findByUserAndJobPosting(user, jobPosting)
                .orElseThrow(() -> new IllegalStateException("Favorite not found."));

        favoriteRepository.delete(favorite);
    }

    public boolean isFavorited(User user, Long jobPostingId) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("Job Posting not found"));
        return favoriteRepository.findByUserAndJobPosting(user, jobPosting).isPresent();
    }

    public List<Favorite> getFavoriteJobPostingsByUser(User user) {
        return favoriteRepository.findByUser(user);
    }
}
