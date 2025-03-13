package com.lms.project.LMS.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 파일 저장 및 삭제를 관리하는 서비스 클래스.
 */
@Service
public class FileStorageService {

    // 파일이 저장될 기본 디렉토리 설정 (외부 설정 값으로 변경 가능)
    private final Path fileStorageLocation;

    /**
     * 생성자: 파일 저장 디렉토리 설정
     *
     * @param uploadDir 파일이 저장될 기본 디렉토리 경로
     */
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            // 디렉토리가 없으면 생성
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("파일 저장 위치를 만들 수 없습니다.", ex);
        }
    }

    /**
     * 주어진 강의 ID와 함께 파일을 저장하는 메서드.
     *
     * @param file     저장할 파일
     * @param courseId 강의 ID (파일명에 포함됨)
     */
    public void saveFile(MultipartFile file, Long courseId) {
        try {
            // 저장할 파일 경로를 설정 (강의 ID를 포함한 파일명)
            String fileName = courseId + "-" + file.getOriginalFilename();
            Path targetLocation = fileStorageLocation.resolve(fileName).normalize();

            // 파일 저장
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("파일을 저장할 수 없습니다. 다시 시도해 주세요.", ex);
        }
    }

    /**
     * 특정 파일을 삭제하는 메서드.
     *
     * @param fileName 삭제할 파일의 이름
     */
    public void deleteFile(String fileName) {
        try {
            // 삭제할 파일의 경로 설정
            Path fileToDelete = fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(fileToDelete);
        } catch (IOException ex) {
            throw new RuntimeException("파일을 삭제할 수 없습니다. 다시 시도해 주세요.", ex);
        }
    }

    /**
     * 파일을 저장하고 URL을 반환하는 메서드.
     *
     * @param file 저장할 파일
     * @return 저장된 파일의 URL
     */
    public String storeFile(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // ✅ 파일 저장 경로 설정
            Path targetLocation = this.fileStorageLocation.resolve(fileName).normalize();
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // ✅ 저장된 파일 경로를 반환 (백슬래시를 슬래시로 변환)
            return targetLocation.toString().replace("\\", "/");
        } catch (IOException ex) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", ex);
        }
    }
}
