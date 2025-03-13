import { useRef, useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import AddContentForm from "./AddContentForm";
import AddAnnouncement from "./AddAnnouncement";
import AddAssignment from "./AddAssignment";
import CreateBoard from "./CreateBoard";
import { Link } from "react-router-dom";
import "./CourseDetail.css";
import StudentSidebar from "./student/StudentSidebar";
const CourseDetailPage = () => {
  const { courseId } = useParams();
  const navigate = useNavigate();
  const [course, setCourse] = useState(null);
  const [weeks, setWeeks] = useState([]);
  const [contents, setContents] = useState({});
  const [announcements, setAnnouncements] = useState({});
  const [assignments, setAssignments] = useState({});
  const [boards, setBoards] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeWeek, setActiveWeek] = useState(null);
  const [activeGoalWeek, setActiveGoalWeek] = useState(null);
  const [editingDescription, setEditingDescription] = useState(false);
  const [newDescription, setNewDescription] = useState("");
  const [showBoardForm, setShowBoardForm] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [memberId, setMemberId] = useState(null);
  const token = localStorage.getItem("token");
  const [role, setRole] = useState(null);
  const [teamMenuOpen, setTeamMenuOpen] = useState(false);


  useEffect(() => {
    if (!courseId) return;
  
    const fetchCourseDetails = async () => {
      try {
        const courseResponse = await axios.get(
          `http://localhost:8090/api/courses/${courseId}`, 
          { headers: { Authorization: `Bearer ${token}` } }
        );
  
        setCourse(courseResponse.data);
        setNewDescription(courseResponse.data.description || "");
        localStorage.setItem("courseId", courseId);

        const storedMemberId = localStorage.getItem('studentId');
        if (storedMemberId) {
          setMemberId(storedMemberId);
        } else {
          const userResponse = await axios.get(
            'http://localhost:8090/api/users/me', 
            { headers: { Authorization: `Bearer ${token}` } }
          );
          setMemberId(userResponse.data.memberId);
        }
  
        const contentResponse = await axios.get(
          `http://localhost:8090/api/courses/${courseId}/weeks/contents`, 
          { headers: { Authorization: `Bearer ${token}` } }
        );
  
        setContents(contentResponse.data || {});
   
      } catch (error) {
        console.error("❌ 강의 상세 정보 불러오기 오류:", error);
      } finally {
        setLoading(false);
      }
    };
  
    fetchCourseDetails();
  }, [courseId, token]);

  useEffect(() => {
    const fetchUserRole = async () => {
      try {
        const response = await axios.get(
          'http://localhost:8090/api/users/me', 
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setRole(response.data.role);
      } catch (error) {
        console.error("❌ 사용자 역할 불러오기 오류:", error);
      }
    };
  
    if (token) {
      fetchUserRole();
    }
  }, [token]);

  useEffect(() => {
    if (!courseId || weeks.length === 0) return;

    const fetchAnnouncements = async () => {
      const announcementsData = {};

      for (let week of weeks) {
        try {
          // Consistent API path format with /api/ prefix
          const response = await axios.get(
            `http://localhost:8090/api/courses/${courseId}/weeks/${week.weekNumber}/announcements`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          announcementsData[week.weekNumber] = response.data || [];
        } catch (error) {
          announcementsData[week.weekNumber] = [];
        }
      }
      setAnnouncements(announcementsData);
    };

    fetchAnnouncements();
  }, [weeks, courseId, token]);

  useEffect(() => {
    if (!courseId) return;
    if (!token) {
      console.error("📌 토큰이 없습니다. 로그인 상태를 확인하세요.");
      return;
    }
  
    const fetchWeeksAndBoards = async () => {
      try {
        console.log("📌 API 요청 시작! 토큰:", token);
  
        // ✅ 주차 데이터 가져오기
        const weeksResponse = await axios.get(
          `http://localhost:8090/api/courses/${courseId}/weeks`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
  
        console.log("📌 주차 데이터 응답:", weeksResponse.data); // 🔍 응답 확인  
  
        const weeksData = weeksResponse.data || [];
        if (!Array.isArray(weeksData) || weeksData.length === 0) {
          console.warn("⚠️ 주차 데이터가 비어 있습니다.");
          return;
        }
        
        setWeeks(weeksData);
  
        // ✅ 주차별 게시판 데이터 가져오기
        const boardData = {};
        for (const week of weeksData) {
          if (!week.weekNumber) {
            console.warn(`⚠️ 주차 데이터에 weekNumber가 없습니다.`, week);
            continue;
          }
  
          try {
            console.log(`📌 ${week.weekNumber}주차 게시판 요청 시작!`);
            const response = await axios.get(
              `http://localhost:8090/weekly-board/course/${courseId}/week/${week.weekNumber}/boards`,
              { headers: { Authorization: `Bearer ${token}` } }
            );
  
            console.log(`📌 ${week.weekNumber}주차 게시판 응답:`, response.data); // 🔍 응답 확인  
            // 데이터 검증
            if (!response.data) {
              console.warn(`⚠️ ${week.weekNumber}주차 게시판 응답이 비어 있습니다.`);
              boardData[week.weekNumber] = [];
            } else {
              boardData[week.weekNumber] = response.data;
            }
  
          } catch (error) {
            console.error(`❌ ${week.weekNumber}주차 게시판 불러오기 실패:`, error);
            boardData[week.weekNumber] = [];
          }
        }
  
        setBoards(boardData);
        console.log("📌 최종 boardData 상태:", boardData); // 🔍 전체 데이터 확인  
  
      } catch (error) {
        console.error("❌ 주차별 데이터 불러오기 실패:", error);
      }
    };
  
    fetchWeeksAndBoards();
  }, [courseId, token]);
  
  useEffect(() => {
    console.log("📌 boards 상태 업데이트됨:", boards);
  }, [boards]);

  useEffect(() => {
    if (!courseId || weeks.length === 0) return;

    const fetchAssignments = async () => {
      const assignmentsData = {};
      for (let i = 0; i < weeks.length; i++) {
        const week = weeks[i];
        const weekNumber = week.weekNumber;

        if (!weekNumber) continue;

        try {
          const response = await axios.get(
            `http://localhost:8090/api/courses/${courseId}/weeks/${weekNumber}/assignments`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          
          assignmentsData[weekNumber] = response.data || [];
        } catch (error) {
          assignmentsData[weekNumber] = [];
        }
      }

      setAssignments(assignmentsData);
    };

    fetchAssignments();
  }, [weeks, courseId, token]);


  const toggleForm = (weekNumber) => {
    setActiveWeek((prevWeek) => (prevWeek === weekNumber ? null : weekNumber));
  };

  const toggleGoalForm = (weekNumber) => {
    setActiveGoalWeek((prevWeek) => (prevWeek === weekNumber ? null : weekNumber));
  };

  if (loading) {
    return <div className="loading">로딩 중...</div>;
  }

  return (
    <div className="course-detail-container">
      {role === 'STUDENT' && <StudentSidebar />} {/* 학생 사이드바 추가 */}
      <div className="course-header">
        <h2>{course?.name} - 강의실</h2> 
        <p className="professor-name">담당 교수: {course?.professor || "정보 없음"}</p>
      </div>

      {showBoardForm && <CreateBoard courseId={courseId} />}

      <div className="course-overview">
        <h3>강의 개요</h3>
        <div className="overview-buttons">
        <button onClick={() => navigate(`/courses/${courseId}/announcements`)}>📢 공지사항</button>
          <button onClick={() => navigate(`/courses/${courseId}/qna`)}>❓ 강좌 Q&A</button>
          <button onClick={() => navigate(`/examlist`)}>📝 시험</button>
          {role === 'STUDENT' && (
          <button onClick={() => navigate(`/student/studentattendance/${courseId}/${memberId}`)}> 📝 출석 현황
          </button>)}
          {role === 'PROFESSOR' && (
  <nav className="team-management-menu">
    <ul>
      <li>
        <div 
          onClick={() => setTeamMenuOpen(prev => !prev)} 
          style={{ cursor: "pointer", fontWeight: "bold", padding: "0.5rem 0" }}
        >
          팀원관리 {teamMenuOpen ? "▼" : "►"}
        </div>
        {teamMenuOpen && (
          <ul className="nested-menu" style={{ paddingLeft: "1rem" }}>
          
            <li>
              <Link
                to="/professor/addteam"
                state={{ courseId: course?.id, courseName: course?.name }}
              >
                팀 추가
              </Link>
            </li>
            <li>
              <Link
                to="/professor/addteam-project"
                state={{ courseId: course?.id, courseName: course?.name }}
              >
                팀프로젝트 추가
              </Link>
            </li>
            <li>
              <Link
                to="/professor/addteam-member"
                state={{ courseId: course?.id, courseName: course?.name }}
              >
                팀원 추가
              </Link>
            </li>
            <li>
            <Link to="/professor/team-project-submissions">
                  팀 프로젝트 제출 내역 보기
                  </Link>
            </li>
          </ul>
        )}
      </li>
    </ul>
  </nav>
)}


            
 
        </div>
      </div>

      <div className="week-activities">
        <h3>주차별 학습 활동</h3>
        {weeks.length > 0 ? (
          <div className="weeks-container">
            {weeks.map((week) => {
              const weekContents = contents?.[week.weekNumber] || [];
              const weekAnnouncements = announcements?.[week.weekNumber] || [];
              const weekAssignments = assignments?.[week.weekNumber] || [];
             
              return (
                <div key={week.weekNumber} className="week-item">
                  <div className="week-header">
                    <h4>{week.weekNumber}주차</h4>
                    {role === 'PROFESSOR' && (
                      <button onClick={() => toggleGoalForm(week.weekNumber)} className="settings-btn">⚙️</button>
                    )}
                  </div>

                  {activeGoalWeek === week.weekNumber && (
                    <div className="goal-form-container">
                      <AddAnnouncement courseId={courseId} weekNumber={week.weekNumber} />
                    </div>
                  )}

                        {weekAnnouncements.length > 0 && (
                                    <ul className="announcement-list">
                                      {weekAnnouncements.map((announcement, index) => (
                                        <li key={`week-${week.weekNumber}-announcement-${index}`} className="announcement-item">
                                        
                                          {announcement.content || "내용 없음"}
                                        </li>
                                      ))}
                                    </ul>
                                  )}

                  {weekContents.length > 0 && (
  <ul className="content-list">
    {weekContents.map((content, index) => {
      const fileExtension = content?.filePath?.split('.').pop().toLowerCase();
      const isVideo = ['mp4', 'webm', 'ogg'].includes(fileExtension);

      return (
        <li key={`week-${week.weekNumber}-content-${index}`} className="content-item">
          {isVideo ? (
            <button
              className="video-content-btn" // ✅ 스타일 적용
              onClick={() => {
                const contentString = JSON.stringify(content);
                sessionStorage.setItem("videoContent", contentString);

                const windowFeatures = "width=800,height=600,top=100,left=100,resizable=yes";
                window.open(`/video/${content.contentId}`, "_blank", windowFeatures);
              }}
            >
              🎥 비디오: {content?.fileName || "파일 이름 없음"}
            </button>
          ) : (
            <a href={content.filePath} target="_blank" rel="noopener noreferrer">
              {content?.fileName || "파일 이름 없음"}
            </a>
          )}
        </li>
      );
    })}
  </ul>
)}



              <div className="assignment-section">
                    {weekAssignments.length > 0 ? (
                      <ul className="assignment-list">
                        {weekAssignments.map((assignment, index) => {
                          if (!assignment.id) {
                            console.error(`과제 ${assignment.title}에 id가 없습니다.`);
                            return null;  // id가 없으면 렌더링하지 않음
                          }

                          return (
                            <li key={`week-${week.weekNumber}-assignment-${index}`} className="assignment-item">
                              {/* token을 쿼리 파라미터로 전달 */}
                              <Link to={`/api/courses/${courseId}/weeks/${week.weekNumber}/assignments/${assignment.id}?token=${token}`}>
                                <h4>📖 {assignment.title || "제목 없음"}</h4>
                              </Link>
                            </li>
                          );
                        })}
                      </ul>
                    ) : (
                      <p></p>
                    )}
                  </div>


     <div className = "board-list">                        
                  {boards[week.weekNumber] && boards[week.weekNumber].length > 0 && (
                    <ul>
                      {boards[week.weekNumber].map((board) => (
                        <li key={board.boardId}>
                          <Link to={`/api/weekly-post/board/${board.boardId}`} className="board-title">
                            📋 {board.title}
                          </Link>
                        </li>
                      ))}
                    </ul>
                  )}
                  </div>

                  <div className="week-buttons">
                    {role === 'PROFESSOR' && (
                      <button onClick={() => toggleForm(week.weekNumber)} className="toggle-btn">
                        {activeWeek === week.weekNumber ? "닫기" : "자료 및 활동 추가"}
                      </button>
                    )}
                  </div>

                  {activeWeek === week.weekNumber && (
                    <div className="form-container">
                      <AddContentForm courseId={courseId} weekNumber={week.weekNumber} token={token} />
                      <AddAssignment courseId={courseId} weekNumber={week.weekNumber} token={token} />
                      <CreateBoard courseId={courseId} weekNumber={week.weekNumber} token={token} />
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        ) : (
          <p>주차별 학습 활동이 없습니다.</p>
        )}
      </div>
    </div>  
  );
};

export default CourseDetailPage;