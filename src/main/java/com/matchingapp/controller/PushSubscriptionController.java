package com.matchingapp.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.matchingapp.entity.User;
import com.matchingapp.service.PushNotificationService;
import com.matchingapp.service.UserService;

// @RestController
@RequestMapping("/push-subscriptions")
public class PushSubscriptionController {

	private final PushNotificationService pushNotificationService;
	private final UserService userService;

	public PushSubscriptionController(PushNotificationService pushNotificationService, UserService userService) {
		this.pushNotificationService = pushNotificationService;
		this.userService = userService;
	}

	@PostMapping("/subscribe")
	public void subscribe(
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam String endpoint,
			@RequestParam String p256dh,
			@RequestParam String auth) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		pushNotificationService.subscribe(currentUser, endpoint, p256dh, auth);
	}

	@PostMapping("/unsubscribe")
	public void unsubscribe(
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam String endpoint) {
		User currentUser = userService.getUserByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));
		pushNotificationService.unsubscribe(currentUser, endpoint);
	}

	@GetMapping("/vapid-public-key")
	public String getVapidPublicKey() {
		return pushNotificationService.getVapidPublicKey();
	}
}
