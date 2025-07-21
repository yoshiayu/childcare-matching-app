package com.matchingapp.repository;

import com.matchingapp.entity.UserQualification;
import com.matchingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserQualificationRepository extends JpaRepository<UserQualification, Long> {
    List<UserQualification> findByUser(User user);
}
