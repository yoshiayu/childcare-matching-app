package com.matchingapp.repository;

import com.matchingapp.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByTargetOrderByCreatedAtDesc(String target);
}
