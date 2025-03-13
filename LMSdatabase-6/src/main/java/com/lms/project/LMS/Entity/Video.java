package com.lms.project.LMS.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String videoUrl; // 비디오 URL

    @ManyToOne
    @JoinColumn(name = "course_id") // Video가 속한 강의 ID (Course 테이블의 id와 연결)
    private Course course; // 비디오가 속한 강의

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Course getCourse() {
        return course;  // Course 정보 가져오기
    }

    public void setCourse(Course course) {
        this.course = course;  // Course 설정
    }
    public Long getCourseId() {
        return course != null ? course.getId() : null;  // course가 null이 아니면 course의 id를 반환
    }
}
