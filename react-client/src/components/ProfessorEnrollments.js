import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './ProfessorEnrollments.css';

const ProfessorEnrollments = () => {
  const [pendingEnrollments, setPendingEnrollments] = useState([]);
  const [courses, setCourses] = useState([]);
  const [courseId, setCourseId] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  const [memberId, setMemberId] = useState(null); // 교수 memberId

  useEffect(() => {
    if (!token) {
      alert("로그인이 필요합니다.");
      navigate('/login');
      return;
    }

    // localStorage에서 memberId 가져오기 (없으면 백엔드에서 불러오기)
    const storedMemberId = localStorage.getItem('memberId'); // 저장된 memberId 가져오기
    if (storedMemberId) {
      setMemberId(storedMemberId); // 저장된 memberId 사용
    } else {
      fetchMemberId(); // 백엔드에서 memberId를 가져오는 함수
    }

    fetchCourses(); // 강의 목록 불러오기
  }, [token, navigate]);

  useEffect(() => {
    // 교수의 ID 추출 (JWT 토큰에서)
    const parsedToken = token ? JSON.parse(atob(token.split('.')[1])) : {};
    setMemberId(parsedToken.username); // 교수 ID를 set

    // 강의 목록 가져오기
    fetchCourses();
  }, [token]);

  useEffect(() => {
    // courseId가 있으면 해당 강의의 수험생 목록을 불러옴
    if (courseId) {
  
      fetchPendingEnrollments(courseId);
    } 
    // 교수의 ID가 있을 때, 교수의 수험생 목록을 불러옴
    else if (memberId) {
   
      fetchPendingEnrollments(memberId);
    }
  }, [courseId, memberId]);

  // 백엔드에서 memberId 가져오기
  const fetchMemberId = async () => {
    try {
      const response = await axios.get('http://localhost:8090/api/users/me', {
        headers: { Authorization: `Bearer ${token}` }
      });
      const fetchedMemberId = response.data.id;
      setMemberId(fetchedMemberId);
      localStorage.setItem('memberId', fetchedMemberId); // memberId를 localStorage에 저장
    } catch (error) {
      console.error("사용자 정보 가져오기 실패:", error);
      alert("사용자 정보를 불러오지 못했습니다. 다시 로그인해주세요.");
      navigate('/login');
    }
  };

  useEffect(() => {
    if (courseId) {
   
      fetchPendingEnrollments(courseId); // 강의를 선택했으면 해당 강의의 수험생 목록을 불러옴
    } else if (memberId) {
   
      fetchPendingEnrollments(memberId); // 교수의 ID로 수험생 목록을 불러옴
    }
  }, [courseId, memberId]);

  const fetchCourses = async () => {
    try {
      const response = await axios.get('http://localhost:8090/api/courses', {
        headers: { Authorization: `Bearer ${token}` }
      });
    
      setCourses(response.data);
    } catch (error) {
      console.error("강의 목록 불러오기 실패:", error);
    }
  };

  const fetchPendingEnrollments = async (id) => {
    try {
      const url = courseId
        ? `http://localhost:8090/api/enrollments/course/${courseId}/pending-enrollments`
        : `http://localhost:8090/api/enrollments/member/${id}/pending-enrollments`;

     

      const response = await axios.get(url, {
        headers: { Authorization: `Bearer ${token}` }
      });

      
      setPendingEnrollments(response.data);
    } catch (error) {
      console.error("수험생 목록 불러오기 실패:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleApproval = async (enrollmentId, status) => {
    try {
      // 승인/거절 상태에 맞게 API 호출
      const response = await axios.put(`http://localhost:8090/api/enrollments/${enrollmentId}/${status === "approve" ? "accept" : "reject"}`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      });

      // 상태에 따른 알림
      alert(`수강 신청 ${status === "approve" ? "승인" : "거절"} 완료!`);

      // 승인/거절된 신청 항목 제거
      setPendingEnrollments(pendingEnrollments.filter(e => e.id !== enrollmentId));
    } catch (error) {
      alert(`수강 신청 ${status === "approve" ? "승인" : "거절"} 실패`);
      console.error("Error in approval/rejection", error);
    }
  };

const getStatusLabel = (status) => {
  switch (status) {
    case "APPROVED":
      return <span className="status approved">승인</span>;
    case "REJECTED":
      return <span className="status rejected">거절</span>;
    default:
      return <span className="status pending">대기중</span>;
  }
};
 // 자신의 강의만 필터링
 const filteredCourses = courses.filter(course => course.professorId === memberId);
 const selectedCourse = courses.find(course => course.id === Number(courseId));
 return (
  <div className="professor-enrollments-container">
    <h2>📋 수강 신청 승인 페이지</h2>

    <select onChange={(e) => setCourseId(e.target.value)} value={courseId || ""}>
      <option value="">강의를 선택하세요</option>
      {filteredCourses.map(course => (
        <option key={course.id} value={course.id}>
          {course.name}
        </option>
      ))}
    </select>

    {pendingEnrollments.length === 0 ? (
      <p>승인 대기 중인 신청이 없습니다.</p>
    ) : (
      <ul className="enrollment-list">
        {pendingEnrollments.map((enrollment) => (
          <li key={enrollment.id} className="enrollment-item">
            <p>👤 학생: {enrollment.studentId ? enrollment.studentId : '정보 없음'}</p>
            <p>📚 강의: {selectedCourse ? selectedCourse.name : "정보 없음"}</p>
            <p>상태: {getStatusLabel(enrollment.status)}</p>
            <button onClick={() => handleApproval(enrollment.id, "approve")}>✅ 승인</button>
            <button onClick={() => handleApproval(enrollment.id, "reject")}>❌ 거절</button>
          </li>
        ))}
      </ul>
    )}
  </div>
);
}


export default ProfessorEnrollments;
