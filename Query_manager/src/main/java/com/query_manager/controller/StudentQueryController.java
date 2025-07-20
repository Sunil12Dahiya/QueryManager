package com.query_manager.controller;

import com.query_manager.entity.RegStudent;
import com.query_manager.entity.StudentQuery;
import com.query_manager.repository.RegStudentRepository;
import com.query_manager.service.StudentQueryiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/queries/student")
public class StudentQueryController {

    @Autowired
    private StudentQueryiService studentQueryiService;

    @Autowired
    private RegStudentRepository regStudentRepository;

    // ✅ Automatically show queries for logged-in student
    @GetMapping("/status")
    public String showStudentQueries(Model model, Principal principal) {
        String email = principal.getName();
        RegStudent regStudent = regStudentRepository.findByEmail(email).orElse(null);


        if (regStudent == null) {
            model.addAttribute("error", "Student not found.");
            return "student-query-status-view";
        }

        List<StudentQuery> studentQueries = studentQueryiService.getQueriesByEnrollmentNumber(regStudent.getEnrollmentNo());

        model.addAttribute("queries", studentQueries);
        return "student-query-status-view"; // ✅ Use your updated HTML page
    }
}
