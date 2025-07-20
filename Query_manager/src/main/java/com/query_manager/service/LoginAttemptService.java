package com.query_manager.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    private final int MAX_ATTEMPT = 5;
    private final ConcurrentHashMap<String, Integer> attempts = new ConcurrentHashMap<>();

    public void loginFailed(String email) {
        int attemptsSoFar = attempts.getOrDefault(email, 0);
        attempts.put(email, attemptsSoFar + 1);
    }

    public void loginSucceeded(String email) {
        attempts.remove(email); // Reset count
    }

    public boolean isBlocked(String email) {
        return attempts.getOrDefault(email, 0) >= MAX_ATTEMPT;
    }

	public int getRemainingAttempts(String email) {
		// TODO Auto-generated method stub
		return 0;
	}
}
