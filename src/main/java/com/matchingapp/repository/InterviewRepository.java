package com.matchingapp.repository;

import com.matchingapp.entity.Interview;
import com.matchingapp.entity.User;
import com.matchingapp.entity.Nursery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByUser(User user);
    List<Interview> findByNursery(Nursery nursery);
    Optional<Interview> findByNurseryAndUserAndInterviewDate(Nursery nursery, User user, java.time.LocalDate interviewDate);
}
