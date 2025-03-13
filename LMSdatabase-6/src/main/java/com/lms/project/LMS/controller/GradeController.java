package com.lms.project.LMS.controller;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.project.LMS.Entity.Grade;
import com.lms.project.LMS.Entity.Submission;
import com.lms.project.LMS.Service.GradeService;
import com.lms.project.LMS.Service.GradingService;

// 성적 관련 HTTP 요청을 처리하는 컨트롤러 클래스
@RestController
@RequestMapping("/api/grades")
public class GradeController {

	@Autowired
	private GradeService gradeService; // GradeService 의존성 주입
	@Autowired
    private GradingService gradingService;
	// 모든 성적 정보를 조회하는 GET 요청 처리
	@GetMapping
	public ResponseEntity<List<Grade>> getAllGrades() {
		List<Grade> grades = gradeService.getAllGrades(); // 수정된 메서드명 적용
		return ResponseEntity.ok(grades); // 성적 목록 반환
	}

	// 성적 정보를 생성하는 POST 요청 처리
	@PostMapping
	public ResponseEntity<Grade> createGrade(@RequestBody Grade grade) {
		Grade createdGrade = gradeService.assignGrade(grade.getProfessor().getId(), grade); // 교수 ID를 받는 메서드로 수정
		return ResponseEntity.ok(createdGrade); // 생성된 성적 반환
	}

	// 성적 엑셀 다운로드 기능
	@GetMapping("/download")
	public ResponseEntity<byte[]> downloadGrades() {
		try (XSSFWorkbook workbook = new XSSFWorkbook();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Grades"); // 엑셀 시트 생성

			// 헤더 생성
			Row headerRow = sheet.createRow(0);
			headerRow.createCell(0).setCellValue("학생 ID");
			headerRow.createCell(1).setCellValue("이름");
			headerRow.createCell(2).setCellValue("성적");

			// 데이터 추가
			List<Grade> grades = gradeService.getAllGrades(); // 수정된 메서드명 적용
			for (int i = 0; i < grades.size(); i++) {
				Grade grade = grades.get(i);
				Row row = sheet.createRow(i + 1); // 각 성적에 대한 행 생성
				row.createCell(0).setCellValue(
						grade.getStudent().getId() != null ? grade.getStudent().getId().toString() : "N/A");
				row.createCell(1)
						.setCellValue(grade.getStudent().getName() != null ? grade.getStudent().getName() : "N/A");
				row.createCell(2).setCellValue(grade.getScore() != null ? grade.getScore() : 0); // 성적을 double로 처리
			}

			// 엑셀 파일을 바이트 배열로 변환
			workbook.write(outputStream);
			byte[] bytes = outputStream.toByteArray(); // 바이트 배열로 변환
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=grades.xlsx"); // 다운로드 헤더 설정

			return new ResponseEntity<>(bytes, headers, HttpStatus.OK); // 응답 반환
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 오류 발생 시 500 상태 반환
		}
	}

	// 성적 분포도 확인 기능
	@GetMapping("/distribution")
	public ResponseEntity<Map<String, Integer>> getGradeDistribution() {
		Map<String, Integer> distribution = new HashMap<>(); // 성적 분포를 저장할 맵
		List<Grade> grades = gradeService.getAllGrades(); // 수정된 메서드명 적용

		// 성적 분포 계산
		for (Grade grade : grades) {
			String letterGrade = getLetterGrade(grade.getScore()); // 문자 등급으로 변환
			distribution.put(letterGrade, distribution.getOrDefault(letterGrade, 0) + 1); // 분포 업데이트
		}
		return ResponseEntity.ok(distribution); // 분포 반환
	}

	// 성적을 문자 등급으로 변환하는 메서드
	private String getLetterGrade(Double score) {
		if (score == null)
			return "N/A"; // null 체크
		if (score >= 90)
			return "A";
		else if (score >= 80)
			return "B";
		else if (score >= 70)
			return "C";
		else if (score >= 60)
			return "D";
		else
			return "F"; // F 등급 반환
	}
	

    @PostMapping("/essay-score")
    public ResponseEntity<String> gradeEssayScores(@RequestBody Submission submission) {
        gradingService.saveEssayScores(submission);
        return ResponseEntity.ok("서술형 문제 점수가 성공적으로 입력되었습니다.");
    }
}
