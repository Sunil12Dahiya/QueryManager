package com.query_manager.controller;

import com.query_manager.entity.QueryType;
import com.query_manager.entity.RegStudent;
import com.query_manager.entity.Student;
import com.query_manager.entity.StudentQuery;
import com.query_manager.repository.RegStudentRepository;
import com.query_manager.service.StudentService;
import com.query_manager.service.StudentQueryService;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentQueryService queryService;
    @Autowired
    private RegStudentRepository regStudentRepository;

    @GetMapping("/submit-query")
    public String showForm(Model model, Principal principal) {
        // 1️⃣ Get logged-in student's email
        String email = principal.getName();

        // 2️⃣ Fetch student info from DB
        RegStudent student = regStudentRepository.findByEmail(email).get();

        // 3️⃣ Pass data to HTML
        model.addAttribute("student", student); // for auto-filling
        model.addAttribute("studentQuery", new StudentQuery()); // your actual form object
        model.addAttribute("types", QueryType.values());
        model.addAttribute("lectureNames", new String[]{}); // fill as needed
        model.addAttribute("batches", new String[]{});      // fill if needed

        return "query-form";
    }
    @PostMapping("/submit-query")
    public String submitQuery(@ModelAttribute StudentQuery studentQuery,
                              RedirectAttributes redirectAttributes,
                              Principal principal) {

        // 1. Get email of logged-in user
        String email = principal.getName();

        // 2. Fetch RegStudent using email
        RegStudent regStudent = regStudentRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Student not found with email: " + email));

        // 3. Create or get Student record
        Student student = studentService.saveOrGetStudentByEnrollment(regStudent.getEnrollmentNo(), regStudent);

        // 4. Set student to query
        studentQuery.setStudent(student);
        studentQuery.setStatus("PENDING");
        studentQuery.setSubmittedAt(LocalDateTime.now());

        // 5. Save query
        queryService.saveQuery(studentQuery);

        // 6. Redirect with success
        redirectAttributes.addFlashAttribute("message", "Query submitted successfully!");
        return "redirect:/query-success";
    }


    @GetMapping("/query-success")
    public String querySuccess() {
        return "query-success";
    }
    
    
       
        
    }


