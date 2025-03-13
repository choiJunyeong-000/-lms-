package com.lms.project.LMS.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.project.LMS.Entity.UploadedFile;

/**
 * ✅ 업로드된 파일 데이터를 관리하는 리포지토리 - 기본 CRUD 기능 제공 - 추가적인 쿼리 메서드를 정의할 수 있음
 */
@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
	// 필요 시 추가적인 쿼리 메서드 정의 가능
}
