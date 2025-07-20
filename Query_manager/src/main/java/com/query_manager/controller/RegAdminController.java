package com.query_manager.controller;

import com.query_manager.entity.RegStudent;
import com.query_manager.repository.RegStudentRepository;
import com.query_manager.service.RegStudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class RegAdminController {

    @Autowired
    private RegStudentRepository studentRepo;
    @Autowired
    private RegStudentService studentService; //
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/students")
    public String viewStudents(Model model) {
        model.addAttribute("students", studentRepo.findAll());
        return "reg-admin-student-list"; // ✅ renamed HTML
    }

    @GetMapping("/add-student")
    public String addStudentForm(Model model) {
        model.addAttribute("student", new RegStudent());
        return "reg-admin-add-student"; // ✅ renamed HTML
    }

    @PostMapping("/add-student")
    public String saveStudent(@ModelAttribute RegStudent student) {
        // Encode the password before saving
        String encodedPassword = passwordEncoder.encode(student.getPassword());
        student.setPassword(encodedPassword);

        // Set student as active since admin is creating the account
        student.setActive(true);

        studentRepo.save(student);
        return "redirect:/admin/students";
    }

    

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentRepo.deleteById(id);
        return "redirect:/admin/students";
    }
    @PostMapping("/students/upload")
    public String uploadStudents(@RequestParam("file") MultipartFile file) {
        studentService.saveStudentsFromExcel(file);
        return "redirect:/admin/students";
    }

}
