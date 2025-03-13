package com.lms.project.LMS.DTO;

import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.Week;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 강의 정보 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String name;
    private Long professorId;
    private String professor;
    private String status;
    private String description;
    private String fileUrl;  // ⬅ 강의 계획서 파일 경로 추가
    private String subjectName; // ⬅ 과목명 추가
    private String courseType; // ⬅ 수업 종류 추가
    private List<WeekResponse> weeks;

    /**
     * Course 객체를 받아서 CourseResponse 생성
     */
    public CourseResponse(Course course) {
        this.id = course.getId();
        this.name = course.getName();
        this.description = course.getDescription();
        this.status = (course.getStatus() != null) ? course.getStatus().name() : "상태 없음";

        // 교수 정보 설정
        Member instructor = course.getMember();
        if (instructor != null && "PROFESSOR".equals(instructor.getRole())) {
            this.professor = instructor.getName();
            this.professorId = instructor.getId();
        } else {
            this.professor = "담당 교수가 지정되지 않았습니다.";
            this.professorId = null;
        }

        // 주차별 학습 내용 변환
        this.weeks = course.getWeeks().stream().map(WeekResponse::new).collect(Collectors.toList());

        // 강의 계획서 파일 경로 추가
        this.fileUrl = course.getSyllabusFilePath();
        
       
        this.courseType = course.getCourseType();
    }
}
