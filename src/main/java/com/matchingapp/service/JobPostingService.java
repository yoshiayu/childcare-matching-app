package com.matchingapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchingapp.entity.JobPosting;
import com.matchingapp.entity.Nursery;
import com.matchingapp.repository.JobPostingRepository;
import com.matchingapp.repository.NurseryRepository;

@Service
public class JobPostingService {

	private final JobPostingRepository jobPostingRepository;
	private final NurseryRepository nurseryRepository;

	public JobPostingService(JobPostingRepository jobPostingRepository, NurseryRepository nurseryRepository) {
		this.jobPostingRepository = jobPostingRepository;
		this.nurseryRepository = nurseryRepository;
	}

	/**
	 * 🔍 検索条件をフルで指定して絞り込み + ソート付きで取得
	 */
	public Page<JobPosting> searchJobPostings(String keyword, String area, Integer salaryMin, String philosophyKeyword,
			String sortBy, String sortOrder, int page, int size) {
		Sort sort = Sort.by(Sort.Direction.ASC, "id"); // Default sort
		if (sortBy != null && !sortBy.isEmpty()) {
			if ("salary".equals(sortBy)) {
				sort = Sort.by(Sort.Direction.fromString(sortOrder), "salary");
			} else if ("area".equals(sortBy)) {
				sort = Sort.by(Sort.Direction.fromString(sortOrder), "area");
			} else if ("createdAt".equals(sortBy)) {
				sort = Sort.by(Sort.Direction.fromString(sortOrder), "createdAt");
			}
		}
		Pageable pageable = PageRequest.of(page, size, sort);
		return jobPostingRepository.searchJobPostings(keyword, area, salaryMin, philosophyKeyword, pageable);
	}

	/**
	 * ✅ Controller から呼ばれるバージョン（area, salaryMin, workTime だけで検索）
	 */
	public List<JobPosting> searchJobPostings(String area, Integer salaryMin, String workTime) {
		// 条件に合わせて、独自のRepositoryメソッドを呼び出す（例: findByAreaAndSalaryGreaterThanEqualAndWorkTime）
		if (area == null && salaryMin == null && workTime == null) {
			return jobPostingRepository.findAll();
		} else if (area != null && salaryMin == null && workTime == null) {
			return jobPostingRepository.findByAreaContaining(area);
		} else if (area != null && salaryMin != null && workTime == null) {
			return jobPostingRepository.findByAreaContainingAndSalaryGreaterThanEqual(area, salaryMin);
		} else if (area != null && salaryMin != null && workTime != null) {
			return jobPostingRepository.findByAreaContainingAndSalaryGreaterThanEqualAndWorkTimeContaining(area,
					salaryMin, workTime);
		} else {
			// 必要に応じて追加条件
			return jobPostingRepository.findAll(); // フォールバック
		}
	}

	public Optional<JobPosting> getJobPostingById(Long id) {
		return jobPostingRepository.findById(id);
	}

	@Transactional
	public JobPosting createOrUpdateJobPosting(Long nurseryId, String title, String description, String area,
			Integer salary, String workTime, String status) {
		Nursery nursery = nurseryRepository.findById(nurseryId)
				.orElseThrow(() -> new IllegalArgumentException("Nursery not found"));

		JobPosting jobPosting = new JobPosting();
		jobPosting.setNursery(nursery);
		jobPosting.setTitle(title);
		jobPosting.setDescription(description);
		jobPosting.setArea(area);
		jobPosting.setSalary(salary);
		jobPosting.setWorkTime(workTime);
		jobPosting.setStatus(status);
		jobPosting.setUpdatedAt(LocalDateTime.now());

		return jobPostingRepository.save(jobPosting);
	}

	@Transactional
	public void updateJobPostingStatus(Long id, String status) {
		JobPosting jobPosting = jobPostingRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Job Posting not found"));
		jobPosting.setStatus(status);
		jobPosting.setUpdatedAt(LocalDateTime.now());
		jobPostingRepository.save(jobPosting);
	}

	public List<JobPosting> getJobPostingsByNursery(Nursery nursery) {
		return jobPostingRepository.findByNurseryId(nursery.getId());
	}

	public List<JobPosting> getAllJobPostings() {
		return jobPostingRepository.findAll();
	}
}
