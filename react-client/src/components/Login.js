import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './Login.css';



function Login({ onLogin }) {
  const [credentials, setCredentials] = useState({ studentId: '', password: '' });
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCredentials({ ...credentials, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post("http://localhost:8090/api/users/login", credentials, { withCredentials: true });
  
      
  
      const token = response.data; // 서버에서 토큰만 반환하는 경우
      localStorage.setItem("token", token);
  
      // ✅ JWT에서 role 추출하는 함수 호출
      const role = extractRoleFromToken(token);
      const studentId = extractStudentIdFromToken(token);

      localStorage.setItem("role", role);
      localStorage.setItem("studentId", studentId);
  
      onLogin(role); // 로그인 성공 시 역할 정보 전달
      navigate('/portal'); // 로그인 성공 시 포털 시스템 선택 페이지로 이동
    } catch (error) {
      if (error.response && error.response.data) {
        alert(error.response.data); // 서버에서 반환한 오류 메시지 표시
      } else {
        console.error("Login failed:", error);
      }
    }
  };

  
  
  // ✅ JWT에서 role을 추출하는 함수 추가
  const extractRoleFromToken = (token) => {
    try {
      const payload = JSON.parse(atob(token.split(".")[1])); // JWT의 payload 부분 해석
      return payload.role; // ✅ role 값 반환
    } catch (error) {
      console.error("Error extracting role from token:", error);
      return null;
    }
  };

  // ✅ JWT에서 student_id를 추출하는 함수 추가
  const extractStudentIdFromToken = (token) => {
    try {
      const payload = JSON.parse(atob(token.split(".")[1])); // JWT payload 디코딩
     
      return payload.sub
    } catch (error) {
      console.error("Error extracting studentId from token:", error);
      return null;
    }
  };
  

  return (
    <div className="login-container">
      <div className="login-header">
        <img src="https://www.cuk.edu/type/common/img/kor/info/coperUniv_logo17.png" alt="우송대학교 로고" className="login-logo" />
        <h1 className="login-title">우송대학교</h1>
      </div>
      <div className="login-box">
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="studentId">아이디</label>
            <input
              type="text"
              id="studentId"
              name="studentId"
              value={credentials.studentId}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">비밀번호</label>
            <input
              type="password"
              id="password"
              name="password"
              value={credentials.password}
              onChange={handleChange}
              required
            />
          </div>
          <button type="submit" className="login-button">로그인</button>
        </form>
      </div>
    </div>
  );
}

export default Login;