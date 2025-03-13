import React, { useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom'; // Link 임포트 추가
import './MainPage.css';

function MainPage() {
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      navigate("/portal-selection"); // 로그인 상태라면 PortalSelection 페이지로 이동
    }
  }, [navigate]);

  return (
    <div className="home-container">
      <h1>우송대학교 LMS</h1>
      <p>자립(自立), 단정(端正), 독행(篤行)</p>
      <div className="home-buttons">
        <Link to="/login" className="btn">Login</Link> {/* Link 사용 */}
      </div>
    </div>
  );
}

export default MainPage;
