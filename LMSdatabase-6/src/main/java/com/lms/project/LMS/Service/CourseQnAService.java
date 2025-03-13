package com.lms.project.LMS.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.project.LMS.DTO.CourseQnADto;
import com.lms.project.LMS.Entity.CourseQnA;
import com.lms.project.LMS.Repository.CourseQnARepository;

import java.util.Optional;
import jakarta.persistence.EntityNotFoundException;
@Service
@RequiredArgsConstructor
public class CourseQnAService {
    private final CourseQnARepository courseQnARepository;
    
    @Transactional
    public Page<CourseQnA> getCourseQnAs(Long courseId, int page, int size) {
        // 해당 강좌에 대한 Q&A 목록을 페이징하여 반환
        Pageable pageable = PageRequest.of(page, size);
        return courseQnARepository.findByCourseId(courseId, pageable);
    }
    @Transactional
    public Optional<CourseQnA> getCourseQnA(Long id) {
        return courseQnARepository.findById(id);
    }

    @Transactional
    public CourseQnA saveCourseQnA(CourseQnA qna) {
        return courseQnARepository.save(qna);
    }
    
    @Transactional
    public CourseQnADto getQnADetail(Long qnaId) {
        CourseQnA qna = courseQnARepository.findById(qnaId)
                .orElseThrow(() -> new EntityNotFoundException("QnA not found"));
        return new CourseQnADto(qna);
    }
    // Method to delete QnA by courseId
    public void deleteQnAByCourseId(Long courseId) {
        courseQnARepository.deleteByCourseId(courseId);
    }

}
