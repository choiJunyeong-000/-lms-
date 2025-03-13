package com.lms.project.LMS.controller;

import com.lms.project.LMS.DTO.StudentAttendanceDTO;
import com.lms.project.LMS.Service.StudentAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-attendance")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class StudentAttendanceController {

    private final StudentAttendanceService studentAttendanceService;

    @GetMapping("/{courseId}/{studentId}") // ✅ memberId → studentId 변경
    public List<StudentAttendanceDTO> getStudentAttendance(
            @PathVariable Long courseId,
            @PathVariable Long studentId // ✅ memberId → studentId 변경
    ) {
        return studentAttendanceService.getStudentAttendance(courseId, studentId);
    }
}