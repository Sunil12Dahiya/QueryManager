package com.query_manager.service;

import com.query_manager.entity.StudentQuery;
import com.query_manager.repository.StudentQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentQueryService {

    @Autowired
    private StudentQueryRepository queryRepository;

    public StudentQuery saveQuery(StudentQuery query) {
        query.setSubmittedAt(LocalDateTime.now());
        query.setStatus("Pending");
        return queryRepository.save(query);
    }

    public List<StudentQuery> getAllQueries() {
        return queryRepository.findAll();
    }

    public StudentQuery getQueryById(Long id) {
        return queryRepository.findById(id).orElse(null);
    }
}
