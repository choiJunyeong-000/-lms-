package com.lms.project.LMS.DTO;

import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Member;

public class ExamResponse {
    private Long id;
    private String title;
    private String description;
    private String examDate;
    private Double totalPoints;  // Changed from Integer to Double
    private String examType;
    private Member member;
    private Course course;
    
    // 기본 생성자
    public ExamResponse() {
    }
    
    // Getter 및 Setter 메서드
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getExamDate() {
        return examDate;
    }
    
    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }
    
    public Double getTotalPoints() {
        return totalPoints;
    }
    
    public void setTotalPoints(Double totalPoints) {
        this.totalPoints = totalPoints;
    }
    
    public String getExamType() {
        return examType;
    }
    
    public void setExamType(String examType) {
        this.examType = examType;
    }
    
    public Member getMember() {
        return member;
    }
    
    public void setMember(Member member) {
        this.member = member;
    }
    
    public Course getCourse() {
        return course;
    }
    
    public void setCourse(Course course) {
        this.course = course;
    }
}