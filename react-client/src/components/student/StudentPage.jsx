import React from "react";
import { Outlet } from "react-router-dom";
import StudentSidebar from "./StudentSidebar";
import "./StudentPage.css";

const StudentPage = () => {
  return (
    <div className="admin-container">
      <StudentSidebar />
      <main className="admin-content-container">
        <Outlet />
      </main>
    </div>
  );
};

export default StudentPage;
