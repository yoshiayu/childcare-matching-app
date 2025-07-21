package com.matchingapp.repository;

import com.matchingapp.entity.Favorite;
import com.matchingapp.entity.User;
import com.matchingapp.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserAndJobPosting(User user, JobPosting jobPosting);
    List<Favorite> findByUser(User user);
}
