package com.matchingapp.service;

import com.matchingapp.entity.Interview;
import com.matchingapp.entity.Nursery;
import com.matchingapp.entity.User;
import com.matchingapp.repository.InterviewRepository;
import com.matchingapp.repository.NurseryRepository;
import com.matchingapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;
    private final NurseryRepository nurseryRepository;
    // private final PushNotificationService pushNotificationService; // Add this

    public InterviewService(InterviewRepository interviewRepository, UserRepository userRepository, NurseryRepository nurseryRepository /*, PushNotificationService pushNotificationService*/) {
        this.interviewRepository = interviewRepository;
        this.userRepository = userRepository;
        this.nurseryRepository = nurseryRepository;
        // this.pushNotificationService = pushNotificationService; // Initialize
    }

    @Transactional
    public Interview createInterviewRequest(Long nurseryId, Long userId, String message, LocalDate interviewDate) {
        Nursery nursery = nurseryRepository.findById(nurseryId)
                .orElseThrow(() -> new IllegalArgumentException("Nursery not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User (Nurse) not found"));

        // Check for duplicate request
        if (interviewRepository.findByNurseryAndUserAndInterviewDate(nursery, user, interviewDate).isPresent()) {
            throw new IllegalStateException("Interview request for this date already exists.");
        }

        Interview interview = new Interview();
        interview.setNursery(nursery);
        interview.setUser(user);
        interview.setMessage(message);
        interview.setInterviewDate(interviewDate);
        interview.setStatus("未承諾");

        Interview savedInterview = interviewRepository.save(interview);

        // Send push notification to the nursery
        // pushNotificationService.sendNotification(nursery.getEmail(), "新しい面談依頼が届きました！\n保育士: " + user.getName() + "\n希望日: " + interviewDate);

        return savedInterview;
    }

    @Transactional
    public void updateInterviewStatus(Long interviewId, String status) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new IllegalArgumentException("Interview request not found"));
        interview.setStatus(status);
        Interview savedInterview = interviewRepository.save(interview);

        // Send push notification to the nurse
        // pushNotificationService.sendNotification(savedInterview.getUser().getEmail(), "面談依頼のステータスが更新されました！\n保育園: " + savedInterview.getNursery().getName() + "\nステータス: " + status);
    }

    public List<Interview> getInterviewsForNurse(User nurse) {
        return interviewRepository.findByUser(nurse);
    }

    public List<Interview> getInterviewsForNursery(Nursery nursery) {
        return interviewRepository.findByNursery(nursery);
    }

    public Optional<Interview> getInterviewById(Long id) {
        return interviewRepository.findById(id);
    }
}