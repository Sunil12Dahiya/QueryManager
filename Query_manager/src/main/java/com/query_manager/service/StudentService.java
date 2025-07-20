package com.query_manager.service;

import com.query_manager.entity.RegStudent;
import com.query_manager.entity.Student;
import com.query_manager.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    /**
     * Fetch student by enrollment. If not present, create using RegStudent data.
     */
    public Student saveOrGetStudentByEnrollment(String enrollmentNumber, RegStudent regStudent) {
        Optional<Student> existing = studentRepository.findByEnrollmentNumber(enrollmentNumber);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Map RegStudent to Student
        Student newStudent = new Student();
        newStudent.setName(regStudent.getFullName());
        newStudent.setEnrollmentNumber(regStudent.getEnrollmentNo());
        newStudent.setBatch(regStudent.getBatch());
        newStudent.setSection(regStudent.getSection());

        return studentRepository.save(newStudent);
    }
}
