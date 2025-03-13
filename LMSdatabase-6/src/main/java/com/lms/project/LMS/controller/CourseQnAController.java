package com.lms.project.LMS.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lms.project.LMS.DTO.CourseQnADto;
import com.lms.project.LMS.DTO.QnAAnswerRequest;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.CourseQnA;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.QnAAnswer;
import com.lms.project.LMS.Service.CourseQnAService;
import com.lms.project.LMS.Service.CourseService;
import com.lms.project.LMS.Service.MemberService;
import com.lms.project.LMS.Service.QnAAnswerService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses/{courseId}/qna")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class CourseQnAController {

    private final CourseQnAService courseQnAService;
    private final CourseService courseService;
    private final MemberService memberService;
    private final QnAAnswerService qnAAnswerService;

    @GetMapping
    public ResponseEntity<Page<CourseQnADto>> getCourseQnAs(@PathVariable Long courseId,
                                                            @RequestParam int page,
                                                            @RequestParam int size) {
        Page<CourseQnA> qnaPage = courseQnAService.getCourseQnAs(courseId, page, size);
        Page<CourseQnADto> dtoPage = qnaPage.map(CourseQnADto::new);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{qnaId}")
    public ResponseEntity<CourseQnADto> getCourseQnA(@PathVariable Long qnaId) {
        CourseQnADto qnaDto = courseQnAService.getQnADetail(qnaId);
        return ResponseEntity.ok(qnaDto);
    }

    @GetMapping("/{qnaId}/answers")
    public ResponseEntity<List<QnAAnswerRequest>> getAnswers(@PathVariable Long qnaId) {
        List<QnAAnswer> answers = qnAAnswerService.getAnswersByQnaId(qnaId);
        List<QnAAnswerRequest> response = answers.stream()
            .map(QnAAnswerRequest::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createCourseQnA(@PathVariable Long courseId,
                                             @RequestHeader("memberId") Long memberId,
                                             @RequestBody CourseQnA qna) {
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (!"STUDENT".equalsIgnoreCase(member.getRole()) && !"PROFESSOR".equalsIgnoreCase(member.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("학생 또는 교수만 질문을 작성할 수 있습니다.");
        }

        Course course = courseService.findCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("강좌를 찾을 수 없습니다."));

        qna.setCourse(course);
        qna.setMember(member);
        qna.setCreatedAt(LocalDateTime.now());
        qna.setUpdatedAt(LocalDateTime.now());

        CourseQnA savedQnA = courseQnAService.saveCourseQnA(qna);
        return ResponseEntity.ok(savedQnA);
    }

    @PostMapping("/{qnaId}/answers")
    public ResponseEntity<QnAAnswerRequest> answerCourseQnA(@PathVariable Long qnaId,
                                                             @RequestHeader("memberId") Long memberId,
                                                             @RequestBody QnAAnswerRequest request) { // DTO로 받기
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        CourseQnA qna = courseQnAService.getCourseQnA(qnaId)
                .orElseThrow(() -> new IllegalArgumentException("QnA를 찾을 수 없습니다."));

        QnAAnswer answer = new QnAAnswer();
        answer.setCourseQnA(qna);
        answer.setAuthor(member);
        answer.setContent(request.getContent().trim());  // JSON에서 content 가져오기
        answer.setCreatedAt(LocalDateTime.now());

        QnAAnswer savedAnswer = qnAAnswerService.saveQnAAnswer(answer);
        return ResponseEntity.ok(new QnAAnswerRequest(savedAnswer)); // 저장된 답변 반환
    }
    @DeleteMapping("/{qnaId}/answers/{answerId}")
    public ResponseEntity<?> deleteQnAAnswer(@PathVariable Long qnaId,
                                             @PathVariable Long answerId,
                                             @RequestHeader("memberId") Long memberId) {
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        QnAAnswer answer = qnAAnswerService.getAnswerById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다."));

        if (!answer.getAuthor().getId().equals(member.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("작성자만 삭제할 수 있습니다.");
        }

        qnAAnswerService.deleteQnAAnswer(answerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{qnaId}/answers/{answerId}")
    public ResponseEntity<QnAAnswerRequest> updateQnAAnswer(@PathVariable Long qnaId,
                                                            @PathVariable Long answerId,
                                                            @RequestHeader("memberId") Long memberId,
                                                            @RequestBody String content) {
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        QnAAnswer answer = qnAAnswerService.getAnswerById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다."));

        if (!answer.getAuthor().getId().equals(member.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new QnAAnswerRequest("작성자만 수정할 수 있습니다."));
        }

        answer.setContent(content.trim());
        QnAAnswer updatedAnswer = qnAAnswerService.saveQnAAnswer(answer);

        return ResponseEntity.ok(new QnAAnswerRequest(updatedAnswer));
    }
}
