package com.query_manager.repository;

import com.query_manager.entity.StudentQuery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentQueryRepository extends JpaRepository<StudentQuery, Long> {

    // ✅ Get all queries for a specific student
    List<StudentQuery> findByStudentEnrollmentNumberIgnoreCase(String enrollmentNumber);

    // ✅ Filter queries dynamically
    @Query("SELECT q FROM StudentQuery q " +
           "WHERE (:name IS NULL OR LOWER(q.student.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:batch IS NULL OR q.student.batch = :batch) " +
           "AND (:section IS NULL OR LOWER(q.student.section) = LOWER(:section))")
    List<StudentQuery> findByFilters(
        @Param("name") String name,
        @Param("batch") String batch,
        @Param("section") String section,
        Sort sort
    );
}
