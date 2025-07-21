package com.matchingapp.service;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.matchingapp.entity.PushSubscription;
import com.matchingapp.entity.User;
import com.matchingapp.repository.PushSubscriptionRepository;
import com.matchingapp.repository.UserRepository;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

// @Service
public class PushNotificationService {

	private final PushSubscriptionRepository pushSubscriptionRepository;
	private final UserRepository userRepository;
	private final PushService pushService;

	@Value("${webpush.vapid.publicKey}")
	private String vapidPublicKey;

	public PushNotificationService(PushSubscriptionRepository pushSubscriptionRepository,
			UserRepository userRepository,
			@Value("${webpush.vapid.publicKey}") String vapidPublicKey,
			@Value("${webpush.vapid.privateKey}") String vapidPrivateKey,
			@Value("${webpush.vapid.subject}") String vapidSubject) {
		this.pushSubscriptionRepository = pushSubscriptionRepository;
		this.userRepository = userRepository;

		Security.addProvider(new BouncyCastleProvider());
		try {
		    this.pushService = new PushService(vapidPublicKey, vapidPrivateKey, vapidSubject);
		} catch (java.security.GeneralSecurityException e) {
		    throw new RuntimeException(e);
		}
	}

	public void subscribe(User user, String endpoint, String p256dh, String auth) {
		PushSubscription subscription = pushSubscriptionRepository.findByUserAndEndpoint(user, endpoint)
				.orElse(new PushSubscription());
		subscription.setUser(user);
		subscription.setEndpoint(endpoint);
		subscription.setP256dh(p256dh);
		subscription.setAuth(auth);
		pushSubscriptionRepository.save(subscription);
	}

	public void unsubscribe(User user, String endpoint) {
		pushSubscriptionRepository.findByUserAndEndpoint(user, endpoint)
				.ifPresent(pushSubscriptionRepository::delete);
	}

	public void sendNotification(String userEmail, String message) {
		userRepository.findByEmail(userEmail).ifPresent(user -> {
			pushSubscriptionRepository.findByUser(user).forEach(subscription -> {
				try {
					JSONObject jsonMessage = new JSONObject();
					jsonMessage.put("message", message);

					Notification notification = new Notification(
							subscription.getEndpoint(),
							subscription.getP256dh(),
							subscription.getAuth(),
							jsonMessage.toString().getBytes());
					pushService.send(notification);
				} catch (Exception e) {
					System.err
							.println("Failed to send push notification to " + user.getEmail() + ": " + e.getMessage());
				}
			});
		});
	}

	public String getVapidPublicKey() {
		return vapidPublicKey;
	}
}
