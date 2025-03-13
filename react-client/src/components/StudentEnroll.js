import React, { useState, useEffect, useCallback } from 'react'; // useCallback을 import
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './StudentEnroll.css';

const StudentEnroll = () => {
  const [courses, setCourses] = useState([]);  
  const [enrolledCourses, setEnrolledCourses] = useState([]); // ✅ 신청한 강의 목록
  const [loading, setLoading] = useState(true);  
  const [error, setError] = useState(null);  
  const [memberId, setMemberId] = useState(null); // 🔥 studentId → memberId 변경
  const navigate = useNavigate();
  const token = localStorage.getItem('token');

  // 🔥 useCallback을 사용해서 fetchEnrollments 함수가 의존성에 추가되지 않도록 처리
  const fetchEnrollments = useCallback(async () => {
    if (!memberId) return;
    try {
      const response = await axios.get(`http://localhost:8090/api/enrollments/member/${memberId}`, { // 🔥 경로 수정
        headers: { Authorization: `Bearer ${token}` } // 🔥 백틱 사용
      });
      setEnrolledCourses(response.data); // ✅ 신청한 강의 목록 저장
      
    } catch (error) {
      console.error("❌ 수강 목록 불러오기 실패:", error);
    }
  }, [memberId, token]); // memberId와 token을 의존성 배열에 추가

  useEffect(() => {
    if (!token) {
      alert("로그인이 필요합니다.");
      navigate('/login');
      return;
    }

    const fetchUserInfo = async () => {
      try {
        const response = await axios.get('http://localhost:8090/api/users/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
    
        console.log("✅ 가져온 memberId:", response.data.id); // ✅ 가져온 값 확인
        setMemberId(response.data.id);
      } catch (error) {
        console.error("❌ 사용자 정보 가져오기 실패:", error);
        alert("사용자 정보를 불러올 수 없습니다. 다시 로그인해주세요.");
        navigate('/login');
      }
    };
    

    const fetchCourses = async () => {
      try {
        const response = await axios.get('http://localhost:8090/api/courses', {
          headers: { Authorization: `Bearer ${token}` } // 🔥 백틱 사용
        });
        setCourses(response.data);
      } catch (error) {
        setError("강의 목록을 불러오는 데 실패했습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchUserInfo();
    fetchCourses();
    fetchEnrollments(); // ✅ 수강 신청한 강의 가져오기
  }, [token, navigate, memberId, fetchEnrollments]); // ✅ 의존성 배열에 fetchEnrollments 추가

  const handleEnroll = async (courseId) => {
    console.log("🔥 신청 요청 - memberId:", memberId, "courseId:", courseId); // ✅ 콘솔 확인
  
    if (!token || !memberId) { 
      alert("로그인이 필요합니다.");
      navigate('/login');
      return;
    }
  
    try {
      await axios.post('http://localhost:8090/api/enrollments', {
        memberId: memberId, // memberId 값이 null이면 백엔드에서 오류 발생
        courseId: courseId
      }, {
        headers: { Authorization: `Bearer ${token}` }
      });
  
      alert("수강 신청 완료!");
      fetchEnrollments(); // ✅ 신청 후 다시 수강 목록 불러오기
    } catch (error) {
      console.error("❌ 수강 신청 실패:", error.response?.data || error.message);
      alert("수강 신청 실패: " + (error.response?.data || error.message));
    }
  };

  const handleCancelEnrollment = async (courseId) => {
    console.log("🚫 신청 취소 요청 - memberId:", memberId, "courseId:", courseId);
  
    if (!token || !memberId) {
      alert("로그인이 필요합니다.");
      navigate('/login');
      return;
    }
  
    try {
      await axios.delete(`http://localhost:8090/api/enrollments/member/${memberId}/course/${courseId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
  
      alert("수강 신청이 취소되었습니다.");
      fetchEnrollments(); // ✅ 최신 상태 반영
    } catch (error) {
      console.error("❌ 수강 신청 취소 실패:", error.response?.data || error.message);
      alert("수강 신청 취소 실패: " + (error.response?.data || error.message));
    }
  };


  // 수정된 getEnrollmentStatus 함수
  const getEnrollmentStatus = (courseId) => {
  if (!enrolledCourses || enrolledCourses.length === 0) {
    return null;
  }
  const enrollment = enrolledCourses.find(e => e?.courseId === courseId);
  
  return enrollment ? enrollment.status : null;
};
  if (loading) return <div className="loading">로딩 중...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    
    <div className="student-enroll-container">
      <h2>📚 수강 신청 페이지</h2>
      {courses.length === 0 ? (
        <p>수강 가능한 강의가 없습니다.</p>
      ) : (
        <ul className="course-list">
          {courses.map((course) => {
            const status = getEnrollmentStatus(course.id);
            
            return (
              <li key={course.id} className="course-item">
                <h3>{course.name}</h3>
              
                <p>🔖 상태: {course.status === 'COMPLETED' ? '신청 가능' : '마감됨'}</p>
                
                {status === "APPROVED" || status === "PENDING" ? (
  <>
    <p className={status === "APPROVED" ? "approved-text" : "pending-text"}>
      {status === "APPROVED" ? "✅ 수강 완료" : "⏳ 수강 신청 중"}
    </p>
    <button className="cancel-btn" onClick={() => handleCancelEnrollment(course.id)}>
      수강 신청 취소
    </button>
  </>
) : (
  course.status === 'COMPLETED' && (
    <button className="enroll-btn" onClick={() => handleEnroll(course.id)}>
      수강 신청
    </button>
  )
)}

              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
};

export default StudentEnroll;
