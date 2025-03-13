import React, { useState, useEffect } from 'react';
import axios from 'axios';
import "./AdminCoursePage.css"; // CSS 파일 불러오기

const AdminCoursePage = () => {
  const [courses, setCourses] = useState([]);

  useEffect(() => {
    const fetchCourses = async () => {
      const token = localStorage.getItem('token');
      if (!token) {
        alert("로그인이 필요합니다.");
        return;
      }

      try {
        const response = await axios.get('http://localhost:8090/api/courses', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setCourses(response.data);
      } catch (error) {
        console.error("Error fetching courses", error);
        alert('강의 목록을 가져오는 데 실패했습니다.');
      }
    };
    fetchCourses();
  }, []);

  const handleAcceptCourse = async (courseId) => {
    const token = localStorage.getItem('token');
    if (!token) {
      alert("로그인이 필요합니다.");
      return;
    }

    try {
      const response = await axios.put(`http://localhost:8090/api/courses/${courseId}/approve`, null, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (response.status === 200) {
        alert("강의 수락 성공");
        setCourses(prevCourses =>
          prevCourses.map(course =>
            course.id === courseId ? { ...course, status: 'COMPLETED' } : course
          )
        );
      }
    } catch (error) {
      alert("강의 수락 중 오류 발생");
      console.error("Error accepting course", error);
    }
  };

  const handleRejectCourse = async (courseId) => {
    const token = localStorage.getItem('token');
    if (!token) {
      alert("로그인이 필요합니다.");
      return;
    }

    try {
      const response = await axios.put(`http://localhost:8090/api/courses/${courseId}/cancel`, null, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (response.status === 200) {
        alert("강의 거절 성공");
        setCourses(prevCourses =>
          prevCourses.map(course =>
            course.id === courseId ? { ...course, status: 'CANCELLED' } : course
          )
        );
      }
    } catch (error) {
      alert("강의 거절 중 오류 발생");
      console.error("Error rejecting course", error);
    }
  };

  const handleDownload = async (fileUrl, fileName) => {
    const token = localStorage.getItem('token');
    if (!token) {
        alert("로그인이 필요합니다.");
        return;
    }

    try {
        const encodedFileUrl = encodeURIComponent(fileUrl);
        const response = await axios.get(`http://localhost:8090/api/courses/download?fileUrl=${encodedFileUrl}`, {
            headers: { Authorization: `Bearer ${token}` },
            responseType: 'blob',
        });

        const blob = new Blob([response.data], { type: response.headers['content-type'] });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = fileName || "downloaded_file";
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    } catch (error) {
        console.error("파일 다운로드 중 오류 발생:", error);
        alert("파일 다운로드에 실패했습니다.");
    }
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case "COMPLETED":
        return <span className="status completed">승인</span>;
      case "CANCELLED":
        return <span className="status cancelled">거절</span>;
      default:
        return <span className="status pending">대기중</span>;
    }
  };

  return (
    <div className="admin-container">
  <h2>강좌 관리</h2> {/* Heading */}
  <table className="admin-table">
    <thead>
      <tr>
        <th>신청자(교수)</th>
        <th>강의명</th>
        <th>이수구분</th>
        <th>강의 계획서</th>
        <th>상태</th>
        <th>수락</th>
        <th>거절</th>
      </tr>
    </thead>
    <tbody>
      {courses.filter(course => course.professor!== "담당 교수가 지정되지 않았습니다.") // Filter out member_id === 1
      .map(course => (
        <tr key={course.id}>
          <td>{course.professor || "미지정"}</td>
          <td>{course.name}</td>
         
          <td>{course.courseType || "미지정"}</td>
          <td>
            {course.fileUrl ? (
              <button className="download-btn" onClick={() => handleDownload(course.fileUrl, course.fileName)}>파일</button>
            ) : (
              "없음"
            )}
          </td>
          <td>{getStatusLabel(course.status)}</td>
          <td>
            <button className="approve-btn" onClick={() => handleAcceptCourse(course.id)}>강의 수락</button>
          </td>
          <td>
            <button className="reject-btn" onClick={() => handleRejectCourse(course.id)}>강의 거절</button>
          </td>
        </tr>
      ))}
    </tbody>
  </table>
</div>

  );
};

export default AdminCoursePage;
