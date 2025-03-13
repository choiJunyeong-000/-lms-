package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;


public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);
    List<Team> findByCourseId(Long courseId);
}
