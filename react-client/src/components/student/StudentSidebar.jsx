import React from "react";
import { Link } from "react-router-dom";
import "./StudentSidebar.css";

function StudentSidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-toggle">
        <span className="arrow">&#9654;</span> {/* 오른쪽 화살표 */}
      </div>
      <h2 className="sidebar-title">메뉴</h2>
      <nav>
        <ul>
          
          <li>
            <Link to="/student">학습 현황</Link>
          </li>
          <li>
            <Link to="/student/enroll">수강 신청</Link>
          </li>
          <li>
            <Link to="/student/team-project">팀 프로젝트</Link>
          </li>
       
          <li>
            <Link to="/student/survey">설문 및 평가</Link>
          </li>
          <li>
            <Link to="/student/message">쪽지</Link>
          </li>
          <li>
            <Link to="/student/kocw">kocw</Link>
          </li>
         
          <li><Link to="http://localhost:5080/">화상강의</Link> {/* 강의 관리 항목 추가 */}
          </li>

        </ul>
      </nav>
    </aside>
  );
}

export default StudentSidebar;
