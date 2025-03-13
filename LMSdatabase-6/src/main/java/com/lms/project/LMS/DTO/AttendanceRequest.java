package com.lms.project.LMS.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AttendanceRequest {
    private String studentId;  // String으로 수정
    private Long courseId;
    private Long videoId;
    private Long contentId;  // 추가된 필드
    private double watchedPercentage;

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public double getWatchedPercentage() {
        return watchedPercentage;
    }

    public void setWatchedPercentage(double watchedPercentage) {
        this.watchedPercentage = watchedPercentage;
    }

    public void validateForCourse() {
        if (courseId == null || watchedPercentage < 0 || watchedPercentage > 100) {
            throw new IllegalArgumentException("잘못된 출석 정보입니다.");
        }
    }
}
