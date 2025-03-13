package com.lms.project.LMS.Service;

import com.lms.project.LMS.Entity.Assignment;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.CourseQnA;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.Professor;
import com.lms.project.LMS.Entity.QnAAnswer;
import com.lms.project.LMS.Entity.Student;
import com.lms.project.LMS.Entity.Week;
import com.lms.project.LMS.Entity.WeeklyBoard;
import com.lms.project.LMS.Repository.AnnouncementRepository;
import com.lms.project.LMS.Repository.AssignmentRepository;
import com.lms.project.LMS.Repository.ContentRepository;
import com.lms.project.LMS.Repository.CourseQnARepository;
import com.lms.project.LMS.Repository.CourseRepository;
import com.lms.project.LMS.Repository.MemberRepository;
import com.lms.project.LMS.Repository.ProfessorRepository;
import com.lms.project.LMS.Repository.QnAAnswerRepository;
import com.lms.project.LMS.Repository.StudentRepository;
import com.lms.project.LMS.Repository.WeekRepository;
import com.lms.project.LMS.Repository.WeeklyBoardRepository;
import com.lms.project.LMS.constants.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	 private final QnAAnswerRepository qnaAAnswerRepository;
	 private final CourseRepository courseRepository; // ✅ 추가
	 private final CourseQnARepository courseQnARepository; // 🔥 QnA 삭제를 위해 필요
	 private final WeekRepository weekRepository;
	 private final  WeeklyBoardRepository weeklyBoardRepository;
	 private final AssignmentRepository assignmentRepository;
	 private final AnnouncementRepository announcementRepository;
	 private final ContentRepository contentRepository;
	 private final StudentRepository studentRepository;
	 private final ProfessorRepository professorRepository;
	 
	@Autowired
	public MemberService(MemberRepository memberRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, QnAAnswerRepository qnAAnswerRepository, CourseRepository courseRepository , CourseQnARepository courseQnARepository,
			WeekRepository weekRepository, WeeklyBoardRepository weeklyBoardRepository, AssignmentRepository assignmentRepository, AnnouncementRepository announcementRepository, ContentRepository contentRepository, StudentRepository studentRepository, ProfessorRepository professorRepository) {
		this.memberRepository = memberRepository;
		this.jwtUtil = jwtUtil;
		this.passwordEncoder = passwordEncoder;
		this.qnaAAnswerRepository = qnAAnswerRepository;
		this.courseRepository = courseRepository;
		this.courseQnARepository = courseQnARepository;
		this.weekRepository = weekRepository;
		this.weeklyBoardRepository = weeklyBoardRepository;
		this.assignmentRepository = assignmentRepository;
		this.announcementRepository = announcementRepository;
		this.contentRepository = contentRepository;
		this.studentRepository = studentRepository;
		this.professorRepository = professorRepository;
	}

	// ✅ 회원 저장 (이메일이 빈 문자열이면 null로 변환 후 저장)
		public Member saveUser(Member user) {
		    // 이메일이 빈 문자열이면 null로 변경
		    if (user.getEmail() != null && user.getEmail().isEmpty()) {
		        user.setEmail(null);
		    }

		    // 비밀번호 암호화 후 저장
		    user.setPassword(passwordEncoder.encode(user.getPassword()));
		    Member savedUser = memberRepository.save(user);
		    
		 // ✅ 역할에 따라 student 또는 professor 테이블에도 저장
		       if (user.getRole().equalsIgnoreCase("STUDENT")) {
		           Student student = new Student();
		           student.setMember(savedUser);
		           studentRepository.save(student); // ✅ student 테이블에 저장
		           System.out.println("✅ 학생 저장 완료: " + savedUser.getName());
		       } else if (user.getRole().equalsIgnoreCase("PROFESSOR")) {
		           Professor professor = new Professor();
		           professor.setMember(savedUser);
		           professorRepository.save(professor); // ✅ professor 테이블에 저장
		           System.out.println("✅ 교수 저장 완료: " + savedUser.getName());
		       }

		       return savedUser;
		}


	// ✅ 모든 회원 조회
	public List<Member> findAllUsers() {
		return memberRepository.findAll();
	}

	// ✅ ID로 회원 조회 (예외 처리 추가)
	public Member findUserById(Long id) {
		return memberRepository.findById(id).orElseThrow(() -> new RuntimeException("해당 ID의 회원을 찾을 수 없습니다."));
	}

	// ✅ studentId(학번)으로 회원 조회
	public Member findUserByStudentId(String studentId) {
		return memberRepository.findByStudentId(studentId)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
	}

	// ✅ 로그인 (studentId, 비밀번호 확인 후 JWT 토큰 발급)
		public String login(String studentId, String rawPassword) {
			Member user = memberRepository.findByStudentId(studentId)
					.orElseThrow(() -> new RuntimeException("아이디를 확인해 주세요."));

			if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
				throw new RuntimeException("비밀번호를 확인해 주세요.");
			}

			String role = user.getRole();
			return jwtUtil.generateToken(user.getStudentId(), role);
		}

	// ✅ ID로 회원 정보 업데이트 (관리자만 가능)
	@PreAuthorize("hasRole('ADMIN')")
	public Member updateUser(Long id, Member updatedUser) {
		return memberRepository.findById(id).map(user -> {
			user.setName(updatedUser.getName());
			user.setStudentId(updatedUser.getStudentId());

			if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
				user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
			}

			user.setRole(updatedUser.getRole());
			user.setBirthDate(updatedUser.getBirthDate());
			user.setEmail(updatedUser.getEmail());
			return memberRepository.save(user);
		}).orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
	}

	// ✅ studentId(학번)으로 회원 정보 업데이트
	public Member updateUserByStudentId(String studentId, Member updatedUser) {
		Member user = memberRepository.findByStudentId(studentId)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

		user.setName(updatedUser.getName());
		user.setEmail(updatedUser.getEmail());

		if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
		}

		if (updatedUser.getBirthDate() != null) {
			user.setBirthDate(updatedUser.getBirthDate());
		}

		return memberRepository.save(user);
	}

	// ✅ 이메일 존재 여부 확인
	public boolean checkEmailExists(String email) {
		return memberRepository.existsByEmail(email);
	}
	
	// ✅ 회원 삭제
	@Transactional
	public void deleteUser(Long id) {
	    Member member = memberRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Member not found"));

	    List<Course> courses = courseRepository.findByMember(member);

	    for (Course course : courses) {
	        List<Week> weeks = weekRepository.findByCourseId(course.getId());

	        for (Week week : weeks) {
	            // ✅ Content 먼저 삭제
	            contentRepository.deleteAll(contentRepository.findAllByWeekId(week.getId()));

	            // ✅ 관련 엔티티 삭제
	            assignmentRepository.deleteAll(assignmentRepository.findAllByWeekId(week.getId()));
	            announcementRepository.deleteAll(announcementRepository.findAllByWeekId(week.getId()));
	            weeklyBoardRepository.deleteAll(weeklyBoardRepository.findAllByWeekId(week.getId()));

	            // ✅ Course의 weeks 리스트에서 제거
	            course.getWeeks().remove(week);

	            // ✅ Week 삭제
	            weekRepository.delete(week);
	        }

	        // ✅ Course 관련 데이터 삭제
	        List<CourseQnA> qnas = courseQnARepository.findByCourse(course);
	        for (CourseQnA qna : qnas) {
	            qnaAAnswerRepository.deleteAllByCourseQnA(qna);
	        }
	        courseQnARepository.deleteAllByCourse(course);

	        // ✅ 강의 삭제
	        courseRepository.delete(course);
	    }

	    // ✅ 최종적으로 Member 삭제
	    memberRepository.delete(member);
	}
	// ✅ ID로 회원 조회 (Optional 반환, 안정적인 예외 처리)
	public Optional<Member> findById(Long id) {
		return memberRepository.findById(id);
	}
	public Optional<Member> findByname(String name) {
        return memberRepository.findByname(name);
    }
	
}
