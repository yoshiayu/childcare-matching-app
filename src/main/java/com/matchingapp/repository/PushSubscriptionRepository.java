package com.matchingapp.repository;

import com.matchingapp.entity.PushSubscription;
import com.matchingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    Optional<PushSubscription> findByUserAndEndpoint(User user, String endpoint);
    List<PushSubscription> findByUser(User user);
}
