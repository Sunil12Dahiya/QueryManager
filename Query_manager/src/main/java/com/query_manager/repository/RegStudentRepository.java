package com.query_manager.repository;

import com.query_manager.entity.RegStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RegStudentRepository extends JpaRepository<RegStudent, Long> {
    Optional<RegStudent> findByEmail(String email);
    Optional<RegStudent> findByResetToken(String resetToken);

}
