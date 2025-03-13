package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 파일 업로드 정보를 관리하는 엔티티
 */
@Entity
@Table(name = "uploaded_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 파일 고유 ID

	private String originalFilename; // 원본 파일명
	private String filePath; // 저장된 파일 경로
	private String fileType; // 파일 타입 (예: "image/png", "application/pdf" 등)

	// 생성자 (Lombok이 자동으로 생성하므로 별도로 필요 없음)
}
