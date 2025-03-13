import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './ProfessorCoursePage.css'; // CSS 파일 추가

const ProfessorCoursePage = () => {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        const response = await axios.get('http://localhost:8090/api/courses/professor', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setCourses(response.data);
      } catch (error) {
        console.error('강의 목록 불러오기 실패:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchCourses();
  }, [token]);

  const handleCourseClick = (courseId) => {
    navigate(`/professor/course/${courseId}`);
  };

  if (loading) {
    return <div className="lms-loading">강의 목록을 불러오는 중...</div>;
  }

  return (
    <div className="lms-container">
      <h1 className="lms-title">📚 내 강의 목록</h1>
      <p className="lms-subtitle">현재 담당하고 계신 강의 목록입니다.</p>

      <div className="lms-course-list">
        {courses.length === 0 ? (
          <p className="lms-no-courses">현재 담당하고 계신 강의가 없습니다.</p>
        ) : (
          courses.map((course) => (
            <div key={course.id} className="lms-course-card" onClick={() => handleCourseClick(course.id)}>
              <div className="lms-course-header">
                <h2 className="lms-course-name">{course.name}</h2>
                <span className={`lms-course-status ${course.status.toLowerCase()}`}>
                  {course.status === 'OPEN' ? '신청 중' : course.status === 'COMPLETED' ? '승인됨' : '거절됨'}
                </span>
              </div>
              <p className="lms-course-type">📌 과목 유형: {course.courseType || '미지정'}</p>
              <p className="lms-course-professor">👨‍🏫 담당 교수: {course.professor || '미지정'}</p>
              <button 
                className="lms-enter-btn"
                onClick={(e) => {
                  e.stopPropagation();
                  handleCourseClick(course.id);
                }}
                disabled={course.status === 'OPEN' || course.status === 'CANCELLED'}
              >
                강의실 입장 
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default ProfessorCoursePage;
