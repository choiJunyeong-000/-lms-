import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import "./StudentDashboard.css";

function StudentDashboard() {
  const [enrollments, setEnrollments] = useState([]); 
  const [memberId, setMemberId] = useState(null);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [filterStatus, setFilterStatus] = useState("");

  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  // 사용자 정보 가져오기
  useEffect(() => {
    if (!token) {
      setError("로그인이 필요합니다.");
      return;
    }

    const fetchUserInfo = async () => {
      try {
        const response = await axios.get('http://localhost:8090/api/users/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setMemberId(response.data.id);
      } catch (error) {
        console.error("사용자 정보 가져오기 실패:", error);
        alert("사용자 정보를 불러올 수 없습니다. 다시 로그인해주세요.");
        navigate('/login');
      }
    };

    fetchUserInfo();
  }, [token, navigate]);

  // 수강 신청한 강의(Enrollment) 불러오기
  useEffect(() => {
    if (!memberId) return;

    const fetchEnrollments = async () => {
      try {
        const response = await axios.get(`http://localhost:8090/api/enrollments/member/${memberId}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setEnrollments(response.data);
      } catch (error) {
        console.error("수강 신청한 강의 불러오기 실패:", error);
        setError("수강 신청한 강의를 불러오는 중 오류가 발생했습니다.");
      }
    };

    fetchEnrollments();
  }, [memberId, token]);

  // 검색어와 상태 필터에 따른 강의 목록 필터링
  const filteredEnrollments = enrollments.filter((enrollment) => {
    const name = (enrollment.courseName || "").toLowerCase();
    const description = (enrollment.courseDescription || "").toLowerCase();
    const matchesSearch =
      name.includes(searchTerm.toLowerCase()) ||
      description.includes(searchTerm.toLowerCase());
    const matchesFilter = filterStatus ? enrollment.status === filterStatus : true;
    return matchesSearch && matchesFilter;
  });

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h2>내 수강 강의</h2>
      </div>
      
      <div className="search-container">
        <input
          type="text"
          placeholder="강좌 검색 (이름 또는 설명)"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
      </div>
      
      <div className="filter-container">
        <select
          value={filterStatus}
          onChange={(e) => setFilterStatus(e.target.value)}
          className="filter-select"
        >
          <option value="">전체 상태</option>
          <option value="PENDING">승인 대기</option>
          <option value="APPROVED">승인됨</option>
          <option value="REJECTED">거절됨</option>
        </select>
      </div>
      
      {error && <p className="error">{error}</p>}
      
      <table className="course-table">
        <thead>
          <tr>
            <th>강좌명</th>
            <th>승인 상태</th>
            <th>자세히 보기</th>
          </tr>
        </thead>
        <tbody>
          {filteredEnrollments.length > 0 ? (
            filteredEnrollments.map((enrollment) => (
              <tr key={enrollment.courseId}>
                <td>{enrollment.courseName}</td>
                <td>{enrollment.status}</td>
                <td>
                <Link
          to={enrollment.status === "PENDING" ? "#" : `/courses/${enrollment.courseId}`}
          className="detail-btn"
          onClick={(e) => {
            if (enrollment.status === "PENDING") {
              e.preventDefault();
              alert("아직 수락되지 않았습니다.");
            }
          }}
        >
          이동
        </Link>
      </td>
    </tr>
  ))
) : (
            <tr>
              <td colSpan="3" className="no-courses">
                등록된 강좌가 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default StudentDashboard;