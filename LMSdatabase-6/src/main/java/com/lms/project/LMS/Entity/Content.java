package com.lms.project.LMS.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

/**
 * 강의 콘텐츠 정보를 관리하는 엔티티 - 강의에 포함된 학습 자료 정보를 저장 - 파일 경로, 제목, 설명, 업로드 시간 등을 관리 - 특정
 * 주차(Week)와 연결 가능
 */
@Entity
@Getter
@Setter
public class Content {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 콘텐츠 고유 ID

	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	private Course course; // 콘텐츠가 속한 강의 (Course 테이블과 연결)

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinColumn(name = "week_id", nullable = true) // 주차(Week)와의 관계 추가
	private Week week; // 콘텐츠가 속한 주차
	
	  @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "video_id")
	    private Video video;
	  
	  
	  @OneToMany(mappedBy = "content", cascade = CascadeType.REMOVE, orphanRemoval = true)
	    private List<Attendance> attendances = new ArrayList<>();


	private String title; // 콘텐츠 제목
	private String description; // 콘텐츠 설명
	private String filePath; // 콘텐츠 파일 경로

	private String fileName; // 📌 **새로운 필드 추가 (파일명 저장용)** ✅

	private boolean isStatic; // 정적 콘텐츠 여부 (예: true는 변하지 않는 자료)
	private boolean isRestricted; // 접근 제한 여부 (예: true는 특정 사용자만 접근 가능)
	private boolean isActive = true; // 콘텐츠 활성 상태 (기본값: 활성)
	private int downloadCount = 0; // 다운로드 횟수

	@Column(name = "member_id", nullable = false)
	private Long memberId; // 콘텐츠를 업로드한 회원의 ID

	private LocalDateTime uploadedAt; // 콘텐츠 업로드 시간

	// 새로운 필드 추가: 파일 타입
	private String fileType; // 콘텐츠 파일 타입

	/**
	 * 업로드 날짜를 자동으로 설정하는 메서드 - 데이터가 새로 저장될 때(uploadedAt이 null이면), 현재 시간으로 설정됨
	 */
	@PrePersist
	public void prePersist() {
		if (uploadedAt == null) {
			uploadedAt = LocalDateTime.now();
		}
	}

	// ✅ 수정된 setFileName() 메서드
	public void setFileName(String fileName) {
		this.fileName = fileName; // 📌 fileName 필드에 저장 ✅
	}

	// getFileName() 메서드 추가
	public String getFileName() {
		return fileName;
	}

	// setFileType 메서드 추가
	public void setFileType(String contentType) {
		this.fileType = contentType; // 파일 타입을 설정
	}

	// getFileType 메서드 추가 (필요시)
	public String getFileType() {
		return fileType;
	}

	// setUploadDate 메서드 추가
	public void setUploadDate(LocalDateTime uploadDate) {
		this.uploadedAt = uploadDate; // 업로드 날짜 설정
	}

	// getUploadedAt 메서드 추가 (필요시)
	public LocalDateTime getUploadedAt() {
		return uploadedAt;
	}
}
