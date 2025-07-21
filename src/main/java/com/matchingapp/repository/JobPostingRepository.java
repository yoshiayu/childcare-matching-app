package com.matchingapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.matchingapp.entity.JobPosting;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
	List<JobPosting> findByStatus(String status);

	List<JobPosting> findByArea(String area);

	List<JobPosting> findByNurseryId(Long nurseryId);

	List<JobPosting> findByAreaContaining(String area);

	List<JobPosting> findByAreaContainingAndSalaryGreaterThanEqual(String area, Integer salary);

	List<JobPosting> findByAreaContainingAndSalaryGreaterThanEqualAndWorkTimeContaining(String area, Integer salary,
			String workTime);

	// Enhanced search methods
	@Query("""
				SELECT jp FROM JobPosting jp
				WHERE
				(:keyword IS NULL OR jp.title LIKE CONCAT('%', :keyword, '%')
					OR jp.description LIKE CONCAT('%', :keyword, '%')
					OR jp.nursery.name LIKE CONCAT('%', :keyword, '%')
				)
				AND (:area IS NULL OR jp.area = :area)
				AND (:salaryMin IS NULL OR jp.salary >= :salaryMin)
				AND (:philosophyKeyword IS NULL OR jp.nursery.philosophy LIKE CONCAT('%', :philosophyKeyword, '%'))
			""")
	Page<JobPosting> searchJobPostings(
			@Param("keyword") String keyword,
			@Param("area") String area,
			@Param("salaryMin") Integer salaryMin,
			@Param("philosophyKeyword") String philosophyKeyword,
			Pageable pageable);

	// Find by status with pagination
	Page<JobPosting> findByStatus(String status, Pageable pageable);
}
