package com.query_manager.controller;

import com.query_manager.entity.RegStudent;
import com.query_manager.repository.RegStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
public class RegForgotPasswordController {

    @Autowired
    private RegStudentRepository studentRepo;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Step 1: Show forgot password form
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "reg-forgot-password"; // ✅ updated file name
    }

    // Step 2: Handle forgot password form
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<RegStudent> optionalStudent = studentRepo.findByEmail(email);

        if (optionalStudent.isPresent()) {
            RegStudent student = optionalStudent.get();
            String token = UUID.randomUUID().toString();
            student.setResetToken(token);
            studentRepo.save(student);

            // Send reset link to email
            String resetUrl = "http://localhost:8080/reset-password?token=" + token;
            String subject = "Reset Your Password";
            String body = "Dear user,\n\nClick the following link to reset your password:\n" + resetUrl;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(student.getEmail());
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);

            model.addAttribute("message", "A password reset link has been sent to your email.");
        } else {
            model.addAttribute("error", "Email not found in our records.");
        }

        return "reg-forgot-password"; // ✅ updated file name
    }

    // Step 3: Show reset form with token
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<RegStudent> optionalStudent = studentRepo.findByResetToken(token);

        if (optionalStudent.isPresent()) {
            model.addAttribute("token", token);
            return "reg-reset-password"; // ✅ updated file name
        } else {
            model.addAttribute("error", "Invalid or expired token.");
            return "reg-forgot-password"; // ✅ updated file name
        }
    }

    // Step 4: Process new password submission
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("newPassword") String newPassword,
                                       Model model) {

        Optional<RegStudent> optionalStudent = studentRepo.findByResetToken(token);

        if (optionalStudent.isPresent()) {
            RegStudent student = optionalStudent.get();
            student.setPassword(passwordEncoder.encode(newPassword));
            student.setResetToken(null); // clear token
            studentRepo.save(student);

            model.addAttribute("message", "Password has been successfully reset. You can now login.");
            return "reg-login"; // ✅ your renamed login file
        } else {
            model.addAttribute("error", "Invalid or expired token.");
            return "reg-reset-password"; // ✅ updated file name
        }
    }
}
