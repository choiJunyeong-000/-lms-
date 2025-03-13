import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";
import "./AnnouncementsList.css";

const AnnouncementsList = () => {
  const { courseId } = useParams();
  const [announcements, setAnnouncements] = useState([]);
  const [token, setToken] = useState("");
  const [user, setUser] = useState(null);

  useEffect(() => {
    const storedToken = localStorage.getItem("token");
    if (storedToken) {
      setToken(storedToken);
    }
  }, []);

  useEffect(() => {
    if (!token) return;

    const fetchUserAndAnnouncements = async () => {
      try {
        const userResponse = await axios.get("http://localhost:8090/api/users/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUser(userResponse.data);

        if (!courseId) return;

        const response = await axios.get(`http://localhost:8090/courses/${courseId}/announcements`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        setAnnouncements(response.data);
      } catch (error) {
        console.error("📢 공지사항 불러오기 실패:", error);
      }
    };

    fetchUserAndAnnouncements();
  }, [courseId, token]);

  const handleDelete = async (announcementId) => {
    if (!user || !courseId) return;

    const confirmDelete = window.confirm("정말로 이 공지사항을 삭제하시겠습니까?");
    if (!confirmDelete) return;

    try {
      await axios.delete(`http://localhost:8090/api/courses/${courseId}/announcements/${announcementId}`, {
        headers: { Authorization: `Bearer ${token}` },
        params: { memberId: user.id },
      });

      setAnnouncements((prev) => prev.filter((a) => a.id !== announcementId));
      alert("공지사항이 삭제되었습니다.");
    } catch (error) {
      console.error("❌ 공지사항 삭제 실패:", error);
      alert("공지사항 삭제에 실패했습니다.");
    }
  };

  return (
    <div className="announcements-container">
      <h2 className="announcements-title">📢 공지사항 목록</h2>

      <table className="announcements-table">
        <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
            {user?.role === "PROFESSOR" || user?.role === "ADMIN" ? (
              <>
                <th>수정</th>
                <th>삭제</th>
              </>
            ) : null}
          </tr>
        </thead>
        <tbody>
          {announcements.length > 0 ? (
            announcements.map((announcement, index) => (
              <tr key={announcement.id}>
                <td>{index + 1}</td>
                <td>
                  <Link to={`/courses/${courseId}/announcements/${announcement.id}`} className="announcement-link">
                    {announcement.title || "제목 없음"}
                  </Link>
                </td>
                <td>{announcement.authorName || "작성자 없음"}</td>
                <td>{announcement.createdAt ? new Date(announcement.createdAt).toLocaleDateString() : "날짜 없음"}</td>

                {/* 교수 또는 관리자만 수정/삭제 버튼 표시 */}
                {(user?.role === "PROFESSOR" || user?.role === "ADMIN") && (
                  <>
                    <td className="announcement-edit-cell">
                      <Link to={`/courses/${courseId}/announcements/${announcement.id}/edit`} className="announcement-edit-btn">
                        수정
                      </Link>
                    </td>
                    <td className="announcement-delete-cell">
                      <button className="announcement-delete-btn" onClick={() => handleDelete(announcement.id)}>
                        삭제
                      </button>
                    </td>
                  </>
                )}
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={user?.role === "PROFESSOR" || user?.role === "ADMIN" ? 6 : 4} style={{ textAlign: "center", padding: "20px" }}>
                📢 등록된 공지사항이 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default AnnouncementsList;
