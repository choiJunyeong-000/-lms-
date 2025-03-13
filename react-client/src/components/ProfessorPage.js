import React, { useState, useEffect } from 'react';
import { Link, Outlet, useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import CourseQnA from './CourseQnA'; // QnA 컴포넌트 import
import './ProfessorPage.css';

function ProfessorPage() {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [teamMenuOpen, setTeamMenuOpen] = useState(false); // 팀 메뉴 토글 상태 추가
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const fetchCoursesForProfessor = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          // 토큰이 없으면 로그인 페이지로 리디렉션
          navigate('/login');
          return;
        }
  
        const userResponse = await axios.get('http://localhost:8090/api/users/me', {
          headers: { Authorization: `Bearer ${token}` },
        });
  
        const memberId = userResponse.data.id;
  
        const coursesResponse = await axios.get('http://localhost:8090/api/courses', {
          headers: { Authorization: `Bearer ${token}` },
        });
  
        const professorCourses = coursesResponse.data.filter(course =>
          course.professorId === memberId && course.status === "COMPLETED"
        );
  
        if (professorCourses.length === 0) {
          // 강의가 없으면 알림 표시 또는 다른 페이지로 이동
          alert("현재 등록된 강의가 없습니다.");
          navigate('/professor/request-course'); // 예시: 강좌 신청 페이지로 리디렉션
          return;
        }
  
        setCourses(professorCourses);
      } catch (error) {
        console.error('강의 목록 조회 오류:', error);
      } finally {
        setLoading(false);
      }
    };
  
    fetchCoursesForProfessor();
  }, [navigate]);

  useEffect(() => {
    if (!loading && courses.length > 0 && location.pathname === "/professor") {
      navigate(`/professor/courses/${courses[0].id}`);
    }
  }, [loading, courses, navigate, location.pathname]);

  return (
    <div className="professor-container">
      <aside className="professor-sidebar">
        <div className="sidebar-toggle">
          <span className="arrow">&#9654;</span>
        </div>
        <h2 className="sidebar-title">메뉴</h2>
        <nav>
          <ul>
            <li>
              <Link to={`/professor/courses/me`} style={{ fontSize: '25px' }}>내 강의</Link>
              <ul className="nested-menu">
                {loading ? (
                  <li>로딩 중...</li>
                ) : (
                  courses.map((course) => (
                    <li key={course.id}>
                      <Link to={`/professor/course/${course.id}`}>{course.name}</Link>
                    </li>
                  ))
                )}
              </ul>
            </li>
            <li><Link to="/professor/request-course">강좌 신청</Link></li>
            <li><Link to="/professor/students">수강생 관리</Link></li>
            <li><Link to="/professor/exams">시험 관리</Link></li>
            {/* 팀 관련 메뉴를 하나의 토글 메뉴로 묶음 */}
            <li>
                       <Link to="/professor/message">쪽지</Link>
                     </li>
            <li><Link to="http://localhost:5080/">화상강의</Link></li>
            <li><Link to="/professor/enrollments">수강 신청 승인</Link></li>
          </ul>
        </nav>
      </aside>
      <div className="professor-content-container">
        <Outlet />
      </div>
    </div>
  );
}

export default ProfessorPage;
