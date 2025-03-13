import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./PortalSelection.css"; // CSS 파일 추가

function PortalSelection() {
  const navigate = useNavigate();
  const role = localStorage.getItem("role");

  useEffect(() => {
    // 로그인하지 않은 경우 로그인 페이지로 리디렉션
    const token = localStorage.getItem('token');
    if (!token) {
      navigate("/login");
      return;
    }

    // 역할에 따라 다른 페이지로 리디렉션
    if (role === "ROLE_ADMIN") {
      navigate("/admin"); // 관리자인 경우 바로 관리자 전용 페이지로 이동
    }
  }, [role, navigate]);

  return (
    <div className="portal-container">
      <div className="portal-images">
        <div className="portal-image-container">
          <img
            src="https://data.iros.go.kr/images/g440/portal/img_rgsData_01.png" // 포털 시스템 이미지 경로
            alt="포털 시스템"
            className="portal-image"
            onClick={() => navigate("/portal-home")}
          />
          <div className="portal-image-text">통합정보 시스템</div>
        </div>
        {role === "PROFESSOR" && (
          <div className="portal-image-container">
            <img
              src="https://st4.depositphotos.com/2547605/41498/v/450/depositphotos_414980524-stock-illustration-lms-learning-management-system-acronym.jpg" // 교수 페이지 이미지 경로
              alt="교수 페이지"
              className="portal-image"
              onClick={() => navigate("/professor")}
            />
            <div className="portal-image-text">LMS</div>
          </div>
        )}
        {role === "STUDENT" && (
          <div className="portal-image-container">
            <img
              src="https://newsroom-prd-data.s3.ap-northeast-2.amazonaws.com/wp-content/uploads/2020/04/%EC%98%A8%EB%9D%BC%EC%9D%B8%EA%B0%95%EC%9D%98_main.jpg" // 학생 페이지 이미지 경로
              alt="학생 페이지"
              className="portal-image"
              onClick={() => navigate("/student")}
            />
            <div className="portal-image-text">LMS</div>
          </div>
        )}
      </div>
    </div>
  );
}

export default PortalSelection;
