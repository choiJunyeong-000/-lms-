package com.lms.project.LMS.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime; // 수정된 부분: LocalDateTime 사용
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lms.project.LMS.DTO.CourseResponse;
import com.lms.project.LMS.Entity.Announcement;
import com.lms.project.LMS.Entity.Assignment;
import com.lms.project.LMS.Entity.Content;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.CourseQnA;
import com.lms.project.LMS.Entity.Enrollment;
import com.lms.project.LMS.Entity.Feedback;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.VirtualLecture;
import com.lms.project.LMS.Entity.Week;
import com.lms.project.LMS.Enum.CourseStatus;
import com.lms.project.LMS.Repository.AnnouncementRepository;
import com.lms.project.LMS.Repository.AssignmentRepository;
import com.lms.project.LMS.Repository.ContentRepository;
import com.lms.project.LMS.Repository.CourseQnARepository;
import com.lms.project.LMS.Repository.CourseRepository;
import com.lms.project.LMS.Repository.EnrollmentRepository;
import com.lms.project.LMS.Repository.FeedbackRepository;
import com.lms.project.LMS.Repository.MemberRepository;
import com.lms.project.LMS.Repository.QnAAnswerRepository;
import com.lms.project.LMS.Repository.VirtualLectureRepository;
import com.lms.project.LMS.Repository.WeekRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CourseService {

	private final CourseRepository courseRepository;
	private final MemberRepository memberRepository;
	private final ContentRepository contentRepository;
	private final EnrollmentRepository enrollmentRepository;
	private final AssignmentRepository assignmentRepository;
	private final AnnouncementRepository announcementRepository;
	private final VirtualLectureRepository virtualLectureRepository;
	private final FeedbackRepository feedbackRepository;
	private final WeekRepository weekRepository;
	private final CourseQnARepository courseQnARepository;
	private final QnAAnswerRepository  qnaAAnswerRepository;

	public CourseService(CourseRepository courseRepository, MemberRepository memberRepository,
			ContentRepository contentRepository, EnrollmentRepository enrollmentRepository,
			AssignmentRepository assignmentRepository, AnnouncementRepository announcementRepository,
			VirtualLectureRepository virtualLectureRepository, FeedbackRepository feedbackRepository,
			WeekRepository weekRepository, CourseQnARepository courseQnARepository, QnAAnswerRepository qnaAAnswerRepository ) {
		this.courseRepository = courseRepository;
		this.memberRepository = memberRepository;
		this.contentRepository = contentRepository;
		this.enrollmentRepository = enrollmentRepository;
		this.assignmentRepository = assignmentRepository;
		this.announcementRepository = announcementRepository;
		this.virtualLectureRepository = virtualLectureRepository;
		this.feedbackRepository = feedbackRepository;
		this.weekRepository = weekRepository;
		this.courseQnARepository = courseQnARepository;
		this.qnaAAnswerRepository =  qnaAAnswerRepository;
	}

	// 📌 강의 콘텐츠 업로드
	@Transactional
	public void addContentToWeek(Long courseId, int weekNumber, MultipartFile file) throws IOException {
		Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다."));

		Week week = weekRepository.findByCourseId(courseId).stream().filter(w -> w.getWeekNumber() == weekNumber)
				.findFirst().orElseThrow(() -> new RuntimeException("해당 주차를 찾을 수 없습니다."));

		Content content = new Content();
		content.setCourse(course);
		content.setWeek(week);

		String originalFileName = file.getOriginalFilename();
		String uniqueFileName = UUID.randomUUID().toString() + "-" + originalFileName;

		String uploadDirectory = "C:/uploads"; // 서버 경로 설정
		File uploadDir = new File(uploadDirectory);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		String filePath = uploadDirectory + File.separator + uniqueFileName;
		content.setFileName(uniqueFileName);
		content.setFilePath(filePath);
		content.setFileType(file.getContentType());

		// 수정된 부분: LocalDateTime.now()로 변경
		content.setUploadDate(LocalDateTime.now()); // 업로드 날짜를 LocalDateTime.now()로 설정

		file.transferTo(new File(filePath));

		contentRepository.save(content);
	}

	// 📌 모든 강의 조회
	public List<Course> getAllCourses() {
		return courseRepository.findAll();
	}

	// 📌 특정 강의 조회
	public Optional<Course> findCourseById(Long id) {
		return courseRepository.findById(id);
	}

	// 📌 특정 교수의 강의 조회
	public List<Course> getCoursesByMemberId(Long memberId) {
		return courseRepository.findByMemberId(memberId);
	}

	// 📌 강의 등록 (교수만 가능)
	public Course createCourse(Long memberId, Course course, String courseType) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new EntityNotFoundException("Member not found"));

		// 수정된 부분: 문자열로 비교
		if (!member.getRole().equals("PROFESSOR")) { // 역할 비교를 문자열로 변경
			throw new IllegalArgumentException("Only professors can create courses.");
		}

		course.setMember(member);
		course.setStatus(CourseStatus.OPEN); // 강의 상태 설정
		course.setCourseType(courseType); // courseType 설정
		course.setProfessorStudentId(member.getStudentId()); // 교수 이름 설정
		return courseRepository.save(course);
	}

	// 📌 강의 저장
	public Course saveCourse(Course course) {
		return courseRepository.save(course);
	}

	// 📌 강의 삭제 (관련 데이터 삭제 포함)
	@Transactional
	public void deleteCourse(Long courseId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new RuntimeException("강의를 찾을 수 없습니다. (ID: " + courseId + ")"));

		// 강의 관련 데이터 삭제
		contentRepository.deleteAll(contentRepository.findByCourseId(courseId));
		enrollmentRepository.deleteAll(enrollmentRepository.findByCourseId(courseId));
		assignmentRepository.deleteAll(assignmentRepository.findByCourseId(courseId));
		announcementRepository.deleteAll(announcementRepository.findByCourseId(courseId));
		feedbackRepository.deleteAll(feedbackRepository.findByCourseId(courseId));
		virtualLectureRepository.deleteAll(virtualLectureRepository.findByCourseId(courseId));

		// 강의 삭제
		courseRepository.deleteById(courseId);
	}

	// 📌 특정 강의의 등록 정보 조회
	public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
		return enrollmentRepository.findByCourseId(courseId);
	}

	// 📌 특정 강의의 과제 목록 조회
	public List<Assignment> getAssignmentsByCourse(Long courseId) {
		return assignmentRepository.findByCourseId(courseId);
	}

	// 📌 특정 강의의 공지사항 목록 조회
	public List<Announcement> getAnnouncementsByCourse(Long courseId) {
		return announcementRepository.findByCourseId(courseId);
	}

	// 📌 특정 강의의 피드백 목록 조회
	public List<Feedback> getFeedbacksByCourse(Long courseId) {
		return feedbackRepository.findByCourseId(courseId);
	}

	// 📌 특정 강의의 가상 강의 목록 조회
	public List<VirtualLecture> getVirtualLecturesByCourse(Long courseId) {
		return virtualLectureRepository.findByCourseId(courseId);
	}

	// 📌 주차별 콘텐츠 조회
	public List<Content> getContentsByWeek(Long courseId, int weekNumber) {
		Week week = weekRepository.findByCourseId(courseId).stream().filter(w -> w.getWeekNumber() == weekNumber)
				.findFirst().orElseThrow(() -> new RuntimeException("해당 주차를 찾을 수 없습니다."));

		return contentRepository.findByWeekId(week.getId());
	}
	
	public List<Course> getCoursesByProfessor(String studentId) {
	    return courseRepository.findByMemberStudentId(studentId);
	}
	@Transactional
	public void deleteCourseQnAsByCourse(Course course) {
	    // 해당 course에 연결된 CourseQnA 리스트를 가져옵니다.
	    List<CourseQnA> qnas = courseQnARepository.findByCourse(course);

	    // 각각의 QnA에 대해 QnAAnswer 삭제
	    for (CourseQnA qna : qnas) {
	        qnaAAnswerRepository.deleteAllByCourseQnA(qna);
	    }

	    // QnA 삭제
	    courseQnARepository.deleteAllByCourse(course);
	}

	 // 강의 삭제 메서드
    public void deleteCourse(Course course) {
        courseRepository.delete(course);
    }
 
}
