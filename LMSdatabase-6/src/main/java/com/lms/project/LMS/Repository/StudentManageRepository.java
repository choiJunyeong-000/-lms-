package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentManageRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT e FROM Enrollment e WHERE e.status = 'APPROVED'")
    List<Enrollment> findApprovedEnrollments();
    
    @Query(value = "SELECT e.* " +
            "FROM enrollment e " +
            "JOIN course c ON e.course_id = c.id " +
            "JOIN member m ON c.member_id = m.id " +
            "WHERE e.status = 'APPROVED' " +
            "AND m.student_id = :professorStudentId", nativeQuery = true)
List<Enrollment> findApprovedEnrollmentsByProfessorStudentId(@Param("professorStudentId") String professorStudentId);

}
