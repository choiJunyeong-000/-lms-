package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 출석 고유 ID

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)  // 정확한 필드 이름을 사용
    private Member member; // 출석을 기록한 학생

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // 출석이 속한 강의
    
    @ManyToOne
    @JoinColumn(name = "content_id", nullable = true)  // Content가 null일 수 있도록 수정
    private Content content; // 출석이 속한 강의 콘텐츠


    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video; // 영상 학습과 관련된 영상 (Video 테이블과 연결)

    private boolean present; // 출석 여부 (참석: true, 불참: false)

    private LocalDate attendanceDate; // 출석 날짜
    private LocalDateTime checkInTime; // 출석 체크 시간

    private double progress; // 학습 진행률 (0~100%)
    private boolean attended;
    private double watchedPercentage;
    
    public Attendance() {
        this.member = new Member();
        this.video = new Video();
    }
    public void checkAttendance() {
        this.present = progress >= 90; // 90% 이상 학습 시 출석 인정

        if (attendanceDate == null) {
            attendanceDate = LocalDate.now();  // 출석 날짜 자동 설정
        }

        if (checkInTime == null) {
            checkInTime = LocalDateTime.now(); // 출석 체크 시간 자동 설정
        }
    }

    @PrePersist
    public void prePersist() {
        if (progress == 0) {
            progress = 0; // 기본값 설정 (0% 학습으로 간주)
        }
        checkAttendance(); // 출석 여부 체크
    }
    
    public boolean isAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    public double getWatchedPercentage() {
        return watchedPercentage;
    }

    public void setWatchedPercentage(double watchedPercentage) {
        this.watchedPercentage = watchedPercentage;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }
}
