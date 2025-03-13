import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './ProfessorCourseRequest.css';

function ProfessorCourseRequest() {
  const [course, setCourse] = useState({
    Name: '', // 여기서 Name 추가
    division: '',
    courseType: "전필",
    professorName: '',
    professorAffiliation: '',
    professorOffice: '',
    professorPhone: '',
    teachingMethods: '',
    courseOverview: '',
    learningGoals: '',
    textbook: '',
    evaluationMethods: '',
    evaluationMethodsPercentage: '',
    weeklyPlan: ''
  });

  const [file, setFile] = useState(null);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const getUserInfoFromServer = async () => {
      const token = localStorage.getItem('token');
      if (token) {
        try {
          const response = await axios.get('http://localhost:8090/api/users/me', {
            headers: { Authorization: `Bearer ${token}` }
          });
          setUser(response.data);
          setLoading(false);
        } catch (error) {
          alert('사용자 정보를 가져오는 데 실패했습니다.');
          navigate('/login');
        }
      } else {
        alert('로그인이 필요합니다.');
        navigate('/login');
      }
    };
    getUserInfoFromServer();
  }, [navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCourse(prev => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('token');
    if (!token) {
      alert('로그인이 필요합니다.');
      navigate('/login');
      return;
    }

    const formData = new FormData();
    Object.keys(course).forEach(key => {
      formData.append(key, course[key]);
    });
    if (file) {
      formData.append('file', file);
    }

    try {
      await axios.post('http://localhost:8090/api/courses/request', formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'multipart/form-data'
        }
      });
      alert('강의가 신청되었습니다.');
      navigate('/professor/courses');
    } catch (error) {
      console.error('강의 신청 실패:', error);
      alert('강의 신청에 실패했습니다.');
    }
  };

  if (loading) {
    return <div>사용자 정보를 불러오는 중...</div>;
  }

  return (
    <form className="course-form" onSubmit={handleSubmit}>
      <h2>강의 신청</h2>
      {user && (
        <div>
          <p>사용자명: {user.name}</p>
          <p>역할: {user.role}</p>
        </div>
      )}
      <label>과목명: <input type="text" name="Name" value={course.Name} onChange={handleChange} required /></label>

      <label>이수구분:
        <select name="courseType" value={course.courseType} onChange={handleChange} required>
          <option value="전필">전필</option>
          <option value="전선">전선</option>
          <option value="교양">교양</option>
          <option value="교필">교필</option>
        </select>
      </label>
      <label>파일 첨부: <input type="file" onChange={handleFileChange} /></label>
      <button type="submit">신청</button>
    </form>
  );
}

export default ProfessorCourseRequest;