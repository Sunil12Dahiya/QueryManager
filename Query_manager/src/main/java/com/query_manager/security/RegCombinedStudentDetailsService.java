package com.query_manager.security;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegCombinedStudentDetailsService implements UserDetailsService {

    private final InMemoryUserDetailsManager inMemoryManager;
    private final UserDetailsService studentService;

    // ✅ Track failed login attempts
    private final Map<String, AttemptData> loginAttempts = new ConcurrentHashMap<>();
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_MINUTES = 15;

    public RegCombinedStudentDetailsService(InMemoryUserDetailsManager inMemoryManager,
                                            UserDetailsService studentService) {
        this.inMemoryManager = inMemoryManager;
        this.studentService = studentService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("[INFO] RegCombinedStudentDetailsService: Checking for username: " + username);

        // 🔒 Check lock
        if (isUserLocked(username)) {
            throw new LockedException("Too many failed login attempts. Try again later.");
        }

        try {
            UserDetails adminUser = inMemoryManager.loadUserByUsername(username);
            System.out.println("[SUCCESS] Found user in InMemoryUserDetailsManager (ADMIN): " + username);
            resetLoginAttempts(username);
            return adminUser;
        } catch (UsernameNotFoundException e) {
            System.out.println("[WARN] Not found in InMemoryUserDetailsManager. Checking StudentService...");
        }

        try {
            UserDetails studentUser = studentService.loadUserByUsername(username);
            System.out.println("[SUCCESS] Found user in RegStudentDetailsService (STUDENT): " + username);
            resetLoginAttempts(username);
            return studentUser;
        } catch (UsernameNotFoundException e) {
            System.out.println("[ERROR] User not found in both Admin and Student sources: " + username);
            increaseLoginAttempts(username); // ❗Increment failed count
            throw new UsernameNotFoundException("User not found: " + username);
        } catch (LockedException e) {
            // In case RegStudentDetailsService already threw LockedException
            throw e;
        }
    }

    private boolean isUserLocked(String username) {
        AttemptData attempt = loginAttempts.get(username);
        if (attempt == null) return false;

        if (attempt.failedAttempts >= MAX_FAILED_ATTEMPTS) {
            if (attempt.lockTime != null && LocalDateTime.now().isBefore(attempt.lockTime.plusMinutes(LOCK_TIME_MINUTES))) {
                return true;
            } else {
                loginAttempts.remove(username); // Reset after timeout
            }
        }
        return false;
    }

    private void increaseLoginAttempts(String username) {
        AttemptData attempt = loginAttempts.getOrDefault(username, new AttemptData());
        attempt.failedAttempts++;
        if (attempt.failedAttempts >= MAX_FAILED_ATTEMPTS) {
            attempt.lockTime = LocalDateTime.now();
            System.out.println("[LOCKED] User '" + username + "' is temporarily locked.");
        }
        loginAttempts.put(username, attempt);
    }

    private void resetLoginAttempts(String username) {
        loginAttempts.remove(username);
    }

    // 🔐 Class to store attempt info
    private static class AttemptData {
        int failedAttempts = 0;
        LocalDateTime lockTime;
    }
}
