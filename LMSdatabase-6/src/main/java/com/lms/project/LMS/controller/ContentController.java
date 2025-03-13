package com.lms.project.LMS.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.project.LMS.Entity.Content;
import com.lms.project.LMS.Service.ContentService;

/**
 * 콘텐츠 관리를 위한 컨트롤러 - 특정 강의에 속한 콘텐츠를 조회할 수 있음 - 모든 콘텐츠를 조회할 수 있음
 */
@RestController
@RequestMapping("/contents")
@CrossOrigin(origins = "http://localhost:3000") // CORS 설정, 프론트엔드 연결
public class ContentController {

	private final ContentService contentService;

	@Autowired
	public ContentController(ContentService contentService) {
		this.contentService = contentService;
	}

	/**
	 * 특정 강의에 연결된 콘텐츠만 반환
	 * 
	 * @param courseId 강의 ID
	 * @return 해당 강의의 콘텐츠 목록
	 */
	@GetMapping
	public List<Content> getContentsByCourse(@RequestParam Long courseId) {
		return contentService.findByCourse(courseId); // courseId만 전달
	}

	/**
	 * 모든 콘텐츠 반환
	 * 
	 * @return 전체 콘텐츠 목록
	 */
	@GetMapping("/all")
	public List<Content> getAllContents() {
		return contentService.getAllContents(); // 모든 콘텐츠 반환
	}
}
