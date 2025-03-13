package com.lms.project.LMS.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {

	@Autowired
	private StudentService studentService; // StudentService 의존성 주입

	// 🔹 모든 학생 조회 (기능 유지)
	@GetMapping
	public ResponseEntity<List<Member>> getAllStudents() {
		List<Member> students = studentService.getAllStudents();
		return ResponseEntity.ok(students);
	}

	// 🔹 ID별 학생 조회 (기능 유지)
	@GetMapping("/{id}")
	public ResponseEntity<Member> getStudentById(@PathVariable Long id) {
		Member student = studentService.findStudentById(id);
		return student != null ? ResponseEntity.ok(student) : ResponseEntity.notFound().build();
	}

	// 🔹 학생 생성 (기능 유지)
	@PostMapping
	public ResponseEntity<Member> createStudent(@RequestBody Member student) {
		Member createdStudent = studentService.createStudent(student);
		return ResponseEntity.status(201).body(createdStudent);
	}

	// 🔹 학생 삭제 (기능 유지)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
		studentService.deleteStudent(id);
		return ResponseEntity.noContent().build();
	}
}
