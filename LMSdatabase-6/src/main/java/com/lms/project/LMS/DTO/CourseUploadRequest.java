package com.lms.project.LMS.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 강의 업로드 요청 DTO - JSON 형식의 강의 데이터와 함께 파일(비디오)을 업로드할 때 사용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseUploadRequest {
	private String courseJson; // 강의 정보를 담은 JSON 데이터
	private MultipartFile video; // 업로드할 비디오 파일
}
