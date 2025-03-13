package com.lms.project.LMS.DTO;

import java.time.LocalDateTime;

import com.lms.project.LMS.Entity.Announcement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnouncementDto {
    private Long id;
    private String title;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;
    private Long memberId; 
    private Long courseId;
    private String courseName;  // 강의 이름
    private int weekNumber;  // 주차 번호

    public AnnouncementDto(Announcement announcement) {
        this.id = announcement.getId();
        this.title = announcement.getTitle();
        this.content = announcement.getContent();
        this.createdAt = announcement.getCreatedAt();
        this.authorName = announcement.getCreatedBy() != null ? announcement.getCreatedBy().getName() : "알 수 없음";
        this.courseName = announcement.getCourse() != null ? announcement.getCourse().getName() : "알 수 없음";  // 강의명
        this.weekNumber = announcement.getWeek() != null ? announcement.getWeek().getWeekNumber() : 0;  // 주차 번호
        this.courseId = (announcement.getCourse() != null) ? announcement.getCourse().getId() : null;
    }

    // 기본 생성자 (필수)
    public AnnouncementDto() {}
}
