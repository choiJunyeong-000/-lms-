package com.lms.project.LMS.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lms.project.LMS.Entity.Content;
import com.lms.project.LMS.Repository.ContentRepository;

/**
 * 강의 콘텐츠 관리 서비스 클래스
 */
@Service
public class ContentService {

	private final ContentRepository contentRepository;
	private final FileStorageService fileStorageService; // 파일 저장/삭제 서비스 추가

	public ContentService(ContentRepository contentRepository, FileStorageService fileStorageService) {
		this.contentRepository = contentRepository;
		this.fileStorageService = fileStorageService;
	}

	// ✅ 모든 콘텐츠 조회
	public List<Content> getAllContents() {
		return contentRepository.findAll();
	}

	// ✅ 특정 강의의 콘텐츠 조회
	public List<Content> getContentsByCourseId(Long courseId) {
		return contentRepository.findByCourseId(courseId);
	}

	// ✅ 특정 제목을 포함하는 콘텐츠 검색
	public List<Content> getContentsByTitle(String title) {
		return contentRepository.findByTitleContaining(title);
	}

	// ✅ 특정 시간 범위의 콘텐츠 조회
	public List<Content> getContentsByUploadDate(LocalDateTime start, LocalDateTime end) {
		return contentRepository.findByUploadedAtBetween(start, end);
	}

	// ✅ 활성화된 콘텐츠 조회
	public List<Content> getActiveContents() {
		return contentRepository.findByIsActiveTrue();
	}

	// ✅ 콘텐츠 저장
	public Content saveContent(Content content) {
		return contentRepository.save(content);
	}

	// ✅ 주차 정보를 기반으로 콘텐츠 조회 (팀원 코드에서 가져옴)
	public List<Content> findByWeekId(Long weekId) {
		return contentRepository.findByWeekId(weekId);
	}

	// ✅ 파일 이름으로 콘텐츠 찾기 (팀원 코드에서 가져옴)
	public Optional<Content> findByFileName(String fileName) {
		return contentRepository.findByFileName(fileName);
	}

	// ✅ 콘텐츠 삭제 (파일도 삭제)
	public void deleteContent(Content content) {
		// 파일 삭제 처리
		fileStorageService.deleteFile(content.getFilePath()); // 파일 삭제

		// DB에서 Content 삭제
		contentRepository.delete(content);
	}

	// ✅ 특정 강의에 해당하는 콘텐츠 조회 (새로 추가된 메서드)
	public List<Content> findByCourse(Long courseId) {
		return contentRepository.findByCourseId(courseId); // ContentRepository에서 제공하는 메서드 호출
	}
	
	public Optional<Content> findById(Long contentId) {
	    return contentRepository.findById(contentId);
	}
}


