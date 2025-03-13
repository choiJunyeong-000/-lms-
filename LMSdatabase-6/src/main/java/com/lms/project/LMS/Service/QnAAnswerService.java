package com.lms.project.LMS.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.project.LMS.Entity.QnAAnswer;
import com.lms.project.LMS.Repository.QnAAnswerRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QnAAnswerService {

    private final QnAAnswerRepository qnAAnswerRepository;

    
    @Transactional
    public List<QnAAnswer> getAnswersByQnaId(Long qnaId) {
    	 return qnAAnswerRepository.findAllByCourseQnAId(qnaId); 
    }
    @Transactional
    public Optional<QnAAnswer> getAnswerById(Long answerId) {
        return qnAAnswerRepository.findById(answerId);
    }
    @Transactional
    public QnAAnswer saveQnAAnswer(QnAAnswer answer) {
        return qnAAnswerRepository.save(answer);
    }

    public void deleteQnAAnswer(Long answerId) {
        qnAAnswerRepository.deleteById(answerId);
    }

}