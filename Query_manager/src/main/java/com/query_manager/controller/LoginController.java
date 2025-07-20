package com.query_manager.controller;
import java.security.Principal;
import com.query_manager.entity.Announcement;
import com.query_manager.entity.RegStudent;
import com.query_manager.repository.AnnouncementRepository;
import com.query_manager.repository.RegStudentRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private AnnouncementRepository announcementRepository;
    @Autowired
    private RegStudentRepository regStudentRepository;
    
   
    @GetMapping("/reg/login")
    public String showLoginForm(Model model,
                                @RequestParam(value = "logout", required = false) String logout,
                                HttpServletRequest request) {

        // Get error message from failure handler
        Object errorMsg = request.getAttribute("errorMsg");
        if (errorMsg != null) {
            model.addAttribute("errorMsg", errorMsg);
        }

        // Logout message
        if (logout != null) {
            model.addAttribute("logoutMsg", "You have been logged out successfully.");
        }

        return "reg-login"; // Thymeleaf or JSP view
    }

    @GetMapping("/reg/student/home")
    public String studentHome(Model model, Principal principal) {
        // Get logged-in student's email
        String email = principal.getName();
        RegStudent student = regStudentRepository.findByEmail(email).orElse(null);

        if (student != null) {
            model.addAttribute("studentName", student.getFullName());
        } else {
            model.addAttribute("studentName", "Student");
        }
        // Add announcements and email to model
        List<Announcement> announcements = announcementRepository.findAll();
        model.addAttribute("announcements", announcements);
        model.addAttribute("email", email);

        return "reg-student-home"; // Your HTML file
    } // Add in LoginController or any suitable controller

    @PostMapping("/reg-login-error")
    public String showLoginError(HttpServletRequest request, Model model) {
        model.addAttribute("errorMsg", request.getAttribute("errorMsg"));
        return "login-error"; // If you want a dedicated page
    }

}
