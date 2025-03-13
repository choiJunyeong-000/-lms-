import React from 'react';
import { Link, Outlet } from 'react-router-dom';
import './AdminPage.css'; // AdminPage 스타일을 위한 CSS 파일

function AdminPage() {
  return (
    <div className="admin-container">
      <aside className="admin-sidebar">
        <div className="sidebar-toggle">
          <span className="arrow">&#9654;</span> {/* 오른쪽 화살표 */}
        </div>
        <h2 className="sidebar-title">메뉴</h2>
        <nav>
          <ul>
            <li>
              <Link to="/admin/users">회원 목록</Link>
            </li>
            <li>
              <Link to="/admin/add-user">회원 추가</Link>
            </li>
            <li>
              <Link to="/admin/monitoring">모니터링</Link> {/* 모니터링 항목 추가 */}
            </li>
            <li>
              <Link to="/admin/kocw-lectures">KOCW 강의</Link> {/* KOCW 강의 항목 추가 */}
            </li>
            <li>
              <Link to="/admin/lecture-management">강좌 관리</Link> {/* 강의 관리 항목 추가 */}
            </li>
            <li>
              <Link to="/admin/addsurvey">설문추가</Link> {/* 강의 관리 항목 추가 */}
            </li>
            <li>
              <Link to="/admin/surveyresponses">설문확인</Link> {/* 강의 관리 항목 추가 */}
            </li>
          </ul>
        </nav>
      </aside>
      <div className="admin-content-container">
        <Outlet />
      </div>
    </div>
  );
}

export default AdminPage;