package com.lms.project.LMS.controller;

import com.lms.project.LMS.DTO.StudentManageDTO;
import com.lms.project.LMS.Service.StudentManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-manage")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // React와 CORS 허용
public class StudentManageController {

    private final StudentManageService studentManageService;

    @GetMapping("/approved")
    public List<StudentManageDTO> getApprovedEnrollments(@RequestParam String professorStudentId) {
        return studentManageService.getApprovedEnrollments(professorStudentId);
    }
}
