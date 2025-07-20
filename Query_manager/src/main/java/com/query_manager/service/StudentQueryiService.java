package com.query_manager.service;

import com.query_manager.entity.StudentQuery;
import com.query_manager.repository.StudentQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentQueryiService {

    @Autowired
    private StudentQueryRepository studentQueryRepository;

    // ✅ Get all queries (for Admin)
    public List<StudentQuery> getAllQueries() {
        return studentQueryRepository.findAll();
    }

    // ✅ Filtered Queries (New Method for Filter & Sort)
    public List<StudentQuery> getFilteredQueries(String name, String batch, String section, String sortDir) {
        Sort sort = Sort.by("submittedAt");
        sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();
        return studentQueryRepository.findByFilters(name, batch, section, sort);
    }

    // ✅ Get queries by student enrollment number (for Student View)
    public List<StudentQuery> getQueriesByEnrollmentNumber(String enrollmentNumber) {
        return studentQueryRepository.findByStudentEnrollmentNumberIgnoreCase(enrollmentNumber);
    }

    // ✅ Optional: Get query by ID (e.g., for admin status update)
    public Optional<StudentQuery> getQueryById(Long id) {
        return studentQueryRepository.findById(id);
    }

    // ✅ Optional: Save or update query
    public StudentQuery saveQuery(StudentQuery query) {
        return studentQueryRepository.save(query);
    }

    // ✅ Update query status
    public boolean updateQueryStatus(Long id, String status) {
        Optional<StudentQuery> optionalQuery = studentQueryRepository.findById(id);
        if (optionalQuery.isPresent()) {
            StudentQuery query = optionalQuery.get();
            query.setStatus(status);
            studentQueryRepository.save(query);
            return true;
        }
        return false;
    }
}
