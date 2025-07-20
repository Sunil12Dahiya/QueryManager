package com.query_manager.security;

import com.query_manager.entity.RegStudent;
import com.query_manager.repository.RegStudentRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service("studentDetailsDelegate")
public class RegStudentDetailsService implements UserDetailsService {

    private final RegStudentRepository studentRepo;

    public RegStudentDetailsService(RegStudentRepository studentRepo) {
        this.studentRepo = studentRepo;
    }

    private static final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private static final Map<String, LocalDateTime> lockoutTimestamps = new ConcurrentHashMap<>();
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 15;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("🌀 [TRACE] RegStudentDetailsService: loadUserByUsername called for: " + email);
        System.out.println("📥 [INFO] Login attempt for: " + email);

        if (isLocked(email)) {
            System.out.println("🔒 [LOCKED] Login blocked for: " + email);
            throw new LockedException("Too many failed login attempts. Please try again later.");
        }

        RegStudent student = studentRepo.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ [ERROR] No user found for email: " + email);
                    registerFailedAttempt(email);
                    return new UsernameNotFoundException("No user found with email: " + email);
                });

        if (!student.isActive()) {
            System.out.println("🚫 [INACTIVE] Student account is inactive: " + email);
            throw new DisabledException("Account not activated. Please verify your email.");
        }

        System.out.println("✅ [FOUND] Valid active user found: " + email);
        resetFailedAttempts(email);
        System.out.println("🛡️ [ROLE] Loaded role for user: " + student.getRole());

        return User.builder()
                .username(student.getEmail())
                .password(student.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + student.getRole().toUpperCase())))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    private void registerFailedAttempt(String email) {
        int attempts = failedAttempts.getOrDefault(email, 0) + 1;
        failedAttempts.put(email, attempts);
        System.out.println("❗ [FAILED LOGIN] Attempt " + attempts + " for: " + email);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            lockoutTimestamps.put(email, LocalDateTime.now());
            System.out.println("🔐 [LOCK INITIATED] Account locked due to too many failed attempts: " + email);
        }
    }

    private void resetFailedAttempts(String email) {
        if (failedAttempts.containsKey(email)) {
            System.out.println("🔁 [RESET] Clearing failed attempts for: " + email);
        }
        failedAttempts.remove(email);
        lockoutTimestamps.remove(email);
    }

    private boolean isLocked(String email) {
        if (!lockoutTimestamps.containsKey(email)) {
            return false;
        }

        LocalDateTime lockTime = lockoutTimestamps.get(email);
        if (lockTime.plusMinutes(LOCK_DURATION_MINUTES).isAfter(LocalDateTime.now())) {
            System.out.println("⏱️ [STILL LOCKED] Lock active for: " + email);
            return true;
        } else {
            System.out.println("🔓 [UNLOCKED] Lock duration expired for: " + email);
            failedAttempts.remove(email);
            lockoutTimestamps.remove(email);
            return false;
        }
    }
}
