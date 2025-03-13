import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Header.css'; // 헤더 스타일을 위한 CSS 파일

function Header({ isAuthenticated, userRole, handleLogout }) {
  const navigate = useNavigate();

  const handleTitleClick = () => {
    if (!isAuthenticated) {
      navigate("/"); // 로그인 페이지로 이동
    } else {
      switch (userRole) {
        case "ROLE_ADMIN":
          navigate("/admin"); // 관리자 페이지로 이동
          break;
        case "STUDENT":
          navigate("/student"); // 학생 페이지로 이동
          break;
        case "PROFESSOR":
          navigate("/professor"); // 교수 페이지로 이동
          break;
        default:
          navigate("/"); // 기본 페이지로 이동
          break;
      }
    }
  };

  const handleLogoClick = () => {
    handleTitleClick(); // 로고 클릭 시 타이틀 클릭 핸들러 호출
  };

  const onLogout = () => {
    handleLogout(); // 기존 로그아웃 기능 실행
    navigate("/"); // ✅ 로그아웃 후 기본 화면으로 이동
  };

  return (
    <header className="App-header">
      <div className="header-left">
        <img 
          src="https://www.cuk.edu/type/common/img/kor/info/coperUniv_logo17.png" 
          alt="로고" 
          className="header-logo" 
          onClick={handleLogoClick} // 로고 클릭 시 핸들러 호출
        />
        <span className="title-text" onClick={handleTitleClick}>
          우송대학교 LMS
        </span>
      </div>
      <div className="header-right">
        {isAuthenticated && userRole === "STUDENT" && (
          <span className="portal-text" onClick={() => navigate("/student")}>
            학생 포털
          </span>
        )}
        {isAuthenticated && userRole === "PROFESSOR" && (
          <span className="portal-text" onClick={() => navigate("/professor")}>
            교수 포털
          </span>
        )}
        {isAuthenticated && (
          <>
            <span className="portal-text" onClick={() => navigate("/profile")}>
              마이페이지
            </span>
            <span className="portal-text" onClick={onLogout}>
              로그아웃
            </span>
          </>
        )}
      </div>
    </header>
  );
}

export default Header;