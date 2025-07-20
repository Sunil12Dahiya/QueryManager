package com.query_manager.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Regstudents")
public class RegStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reset_token")
    private String resetToken;
    
    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String enrollmentNo;
    @Column(nullable = false)
    private String batch;

    @Column(nullable = false)
    private String section;
    private String password;

    private boolean active;

    @Column(name = "role")
    private String role;

  

    // ===== Constructors =====

    public RegStudent() {}

    public RegStudent(String fullName, String email, String enrollmentNo, String batch, String section) {
    	 this.fullName = fullName;
         this.batch = batch;
         this.section = section;
        this.email = email;
        this.enrollmentNo = enrollmentNo;
               this.active = false;
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEnrollmentNo() {
        return enrollmentNo;
    }
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    
    public void setEnrollmentNo(String enrollmentNo) {
        this.enrollmentNo = enrollmentNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    }
