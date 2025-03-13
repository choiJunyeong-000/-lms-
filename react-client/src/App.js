import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate} from "react-router-dom";

import MainPage from "./components/MainPage"; // 메인 페이지 추가
import Login from "./components/Login";
import UserFormPage from "./components/UserFormPage"; // 회원 추가 페이지
import UserListPage from "./components/UserListPage"; // 회원 목록 페이지
import UserEdit from "./components/UserEdit";
import StudentPage from "./components/student/StudentPage";
import ProfessorPage from "./components/ProfessorPage";
import PortalSelection from "./components/PortalSelection"; // 포털 시스템 선택 페이지 추가
import UserProfile from "./components/UserProfile"; // 마이페이지 추가
import Header from "./components/Header"; // 헤더 컴포넌트 추가
import AdminPage from "./components/AdminPage"; // AdminPage 추가
import MonitoringPage from "./components/MonitoringPage"; // 모니터링 페이지 추가
import KocwLectures from './components/KocwLectures';
import AdminCoursePage from './components/AdminCoursePage';
import ProfessorCourseRequest from './components/ProfessorCourseRequest'; // 강좌 신청 페이지 추가
import ProfessorCoursePage from "./components/ProfessorCoursePage";
import CourseDetailPage from './components/CourseDetailPage';
import KocwCoursePage from './components/KocwCoursePage';
import AssignmentDetail from "./components/AssignmentDetail";
import AssignmentGrading from "./components/AssignmentGrading";
import CourseQnA from "./components/CourseQnA";
import QnADetail from "./components/QnADetail";
import AnnouncementsList from "./components/AnnouncementsList";
import ContentViewer from "./components/ContentView";
import CreatePost from "./components/CreatePost";
import PostList from "./components/PostList";
import PostDetail from "./components/PostDetail";
import MessagePage from "./components/Message/MessagePage";
import StudentDashboard from "./components/student/StudentDashboard";
import ProfessorExamPage from "./components/ProfessorExamPage"; //  시험 관리 페이지 추가
import ProfessorGradingPage from "./components/ProfessorGradingPage"; // 점수 관리 페이지 추가
import ProfessorExamQuestion from "./components/ProfessorExamQuestion";
import AnnouncementDetail   from "./components/AnnouncementDetail"; // 공지사항 상세 페이지 추가
import AnnouncementEdit   from "./components/AnnouncementEdit"; // 공지사항 수정 페이지 추가
import PostEdit from "./components/PostEdit"; // 게시글 수정 페이지 추가
import ExamList from "./components/ExamList";
import ExamPage from "./components/ExamPage";
import CreateSurvey from "./components/survey/CreateSurvey";
import CreateTeamForm from "./components/Team/CreateTeamForm";
import CreateTeamProject from "./components/Team/CreateTeamProject";
import AddTeamMember from "./components/Team/AddTeamMember";
import ProfessorTeamProjectSubmissions from "./components/Team/ProfessorTeamProjectSubmissions";
import ExamDetail from "./components/ExamDetail";
// ✅ 추가된 학생 기능 페이지
import AdminSurveyResponses from "./components/survey/AdminSurveyResponses";
import LearningStatus from './components/student/LearningStatus/LearningStatus';

import TeamProject from './components/student/TeamProject/TeamProject';
import TeamProjectSubmit from './components/student/TeamProject/TeamProjectSubmit';
import TeamProjectHistory from './components/student/TeamProject/TeamProjectHistory';
import BoardPage from './components/student/Board/BoardPage';
import SurveyPage from './components/student/Survey/SurveyPage';
 

import LectureSurvey from './components/student/Survey/LectureSurvey'; 
import QuizPage from './components/student/Quiz/QuizPage'; 
import StudentEnroll from "./components/StudentEnroll"; // ✅ 수강신청 페이지 추가 ✅
import ProfessorEnrollments from "./components/ProfessorEnrollments"; // ✅ 교수 수강 승인 페이지 추가 ✅
import VideoDetailPage from "./components/VideoDetailPage";
import StudentManagePage from "./components/StudentManagePage";
import StudentAttendance from "./components/StudentAttendance";
import ExamSelection from "./components/student/Quiz/ExamSelection";
import "./App.css";

function App() {
  const [userRole, setUserRole] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);
  
  useEffect(() => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    if (token && role) {
      setIsAuthenticated(true);
      setUserRole(role);
    }
    setLoading(false);
  }, []);

  const handleLogin = () => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    if (token && role) {
      setIsAuthenticated(true);
      setUserRole(role);
    }
  };

  const handleLogout = (navigate) => {
    localStorage.clear();
    setIsAuthenticated(false);
    setUserRole(null);
  
  };
  

  if (loading) {
    return <div>로딩 중...</div>; // 로딩 중일 때 표시할 내용
  }

  return (
    <Router>
      <Routes>
      <Route path="/video/:contentId" element={<VideoDetailPage />} />
      <Route
      path="/*" 
      element={

  
      <div className="App">
         <Header isAuthenticated={isAuthenticated} userRole={userRole} handleLogout={handleLogout} />
        <main>
          <Routes>
            {/* 첫 시작 화면을 MainPage로 설정하되, 로그인 상태라면 포털 선택 페이지로 리디렉션 */}
            
            <Route
              path="/"
              element={isAuthenticated ? <Navigate to="/portal" /> : <MainPage />}
            />
            

            {/* 로그인 페이지 */}
            <Route path="/login" element={<Login onLogin={handleLogin} />} />

            {/* 포털 시스템 선택 페이지 */}
            <Route path="/portal" element={isAuthenticated ? <PortalSelection /> : <Navigate replace to="/" />} />

            {/* AdminPage */}
            <Route path="/admin" element={isAuthenticated && userRole === "ROLE_ADMIN" ? <AdminPage /> : <Navigate replace to="/" />}>
              <Route index element={<UserListPage />} /> {/* 기본으로 회원 목록 페이지 */}
              <Route path="users" element={<UserListPage />} />
              <Route path="add-user" element={<UserFormPage />} />
              <Route path="monitoring" element={<MonitoringPage />} /> {/* 모니터링 페이지 추가 */}
              <Route path="kocw-lectures" element={<KocwLectures />} /> {/* KOCW 강의 페이지 추가 */}
              <Route path="lecture-management" element={<AdminCoursePage />} /> {/* 강의 관리 페이지 추가 */}
              <Route path="addsurvey" element={<CreateSurvey />} />
              <Route path="surveyresponses" element={<AdminSurveyResponses/>} />
            </Route>
            <Route path="/edit/:id" element={isAuthenticated && userRole === "ROLE_ADMIN" ? <UserEdit /> : <Navigate replace to="/" />} />

            {/* ROLE_PROFESSOR인 경우 교수 페이지 이동 */}
            <Route path="/professor" element={isAuthenticated && userRole === "PROFESSOR" ? <ProfessorPage /> : <Navigate replace to="/" />}>
            <Route index element={<ProfessorCoursePage />} />
            <Route path="request-course" element={<ProfessorCourseRequest />} />
            <Route path="courses/:courseId" element={<ProfessorCoursePage />} />  {/* ✅ 경로 수정 */}
            <Route path="course/:courseId" element={<CourseDetailPage />} />
            <Route path="exams" element={<ProfessorExamPage />} /> {/* 📌 시험 관리 페이지 추가 */}
            <Route path="grading" element={<ProfessorGradingPage />} /> {/* ✅ 채점 페이지 추가 */}
            <Route path="exams/:examId/questions" element={<ProfessorExamQuestion />} />
            <Route path="addteam-member" element={<AddTeamMember />} /> 
            <Route path="addteam" element={<CreateTeamForm />} /> 
            <Route path="addteam-project" element={<CreateTeamProject />} />
            <Route path="team-project-submissions" element={<ProfessorTeamProjectSubmissions />} />
            <Route path="students" element={<StudentManagePage />} />
            <Route path="message"  element={<MessagePage />} /> 
            <Route 
              path="/professor/enrollments" 
              element={isAuthenticated && userRole === "PROFESSOR" ? <ProfessorEnrollments /> : <Navigate replace to="/" />} />
            
          </Route>

          <Route path="/courses/:courseId/announcements" element={<AnnouncementsList />} />
          <Route path="/courses/:courseId/announcements/:announcementId" element={<AnnouncementDetail />} />  {/* 공지사항 상세 페이지 추가 */}
          <Route path="/courses/:courseId/qna" element={<CourseQnA />} />  {/* 강좌 Q&A 경로 추가 */}
          <Route path="/courses/:courseId/qna/:qnaId" element={<QnADetail />} />   
          <Route path="/courses/:courseId/qna/:qnaId" element={<QnADetail />} />         
          <Route path="/courses/:courseId/announcements/:announcementId/edit" element={<AnnouncementEdit />} />
       
         {/* 📌 공지사항 목록 페이지로 수정 */}
         <Route path="api/courses/:courseId/weeks/:weekNumber/assignments/:assignmentId" element={<AssignmentDetail />} /> 
         <Route path="/course/:courseId/content-viewer" element={<ContentViewer />} />
         <Route path="/api/weekly-post/board/:boardId" element={<PostList />} />
         <Route path="/weekly-post/create/:boardId" element={<CreatePost />} />
  
         <Route path="/api/weekly-post/:postId/:boardId" element={<PostDetail />} /> 
         <Route path="/api/weekly-post/:postId/:boardId/edit" element={<PostEdit />} />
          <Route path="/courses/:courseId/exams" element={<ExamSelection />} />
         <Route path="/courses/:courseId/quiz/:examId" element={<QuizPage />} /> 
            {/* 학생 페이지 */}
            <Route path="/student" element={isAuthenticated && userRole === "STUDENT" ? <StudentPage /> : <Navigate replace to="/" />}>
            <Route index element={<StudentDashboard />} />  {/* 기본 페이지 */}
              <Route path="learning-status" element={<LearningStatus />} />   
              <Route path="message"  element={<MessagePage />} /> 
              <Route path="team-project" element={<TeamProject />} />
              <Route path="team-project/submit" element={<TeamProjectSubmit />} />
              <Route path="team-project/history" element={<TeamProjectHistory />} />
              
              <Route path="board" element={<BoardPage />} />
              <Route path="survey" element={<SurveyPage />} />
              
              
              <Route path="survey/lecture/:courseId" element={<LectureSurvey />} />
              <Route path="quiz/:courseId" element={<QuizPage />} />
              <Route path="/student/enroll" element={isAuthenticated && userRole === "STUDENT" ? <StudentEnroll /> : <Navigate replace to="/" />} />
              <Route path="/student/studentattendance/:courseId/:memberId" element={<StudentAttendance />} />
            </Route>
            <Route path="/student/kocw" element={<KocwCoursePage />} />
            <Route path="/examlist/:courseId" element={<ExamList />} />
            <Route path="/exam/:examId" element={<ExamDetail />} />
            <Route path="/exam/:examId/details" element={<ExamPage />} />
            {/* 마이페이지 */}
            <Route path="/profile" element={isAuthenticated ? <UserProfile /> : <Navigate replace to="/" />} />
            <Route path="courses/:courseId" element={<CourseDetailPage />} /> {/* 동적 경로 */}

            <Route path="api/courses/:courseId/weeks/:weekNumber/assignments/:assignmentId/grade" element={<AssignmentGrading />} />
            <Route path="/student/studentattendance" element={<StudentAttendance />} />
            <Route path="/examlist" element={<ExamList />} />

            {/* 잘못된 URL 접근 시 기본 페이지로 이동 */}
            <Route path="*" element={<Navigate replace to="/" />} />
          </Routes>
        </main>
      </div>
      }
      />
      </Routes>
    </Router>
  );
}

export default App;
