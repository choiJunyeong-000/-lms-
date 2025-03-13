import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./AnnouncementEdit.css";
const AnnouncementEdit = () => {
  const { courseId, announcementId } = useParams();
  const navigate = useNavigate();

  /** ✅ 상태 변수 */
  const [token, setToken] = useState("");
  const [memberId, setMemberId] = useState(null);
  const [role, setRole] = useState("");
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  /** ✅ 로컬 스토리지에서 토큰 가져오기 */
  useEffect(() => {
    const storedToken = localStorage.getItem("token");
    if (storedToken) {
      setToken(storedToken);
    }
  }, []);

  /** ✅ 사용자 정보 가져오기 */
  useEffect(() => {
    if (!token) return;

    const fetchUserData = async () => {
      try {
        const response = await axios.get("http://localhost:8090/api/users/me", {
          headers: { Authorization: `Bearer ${token}` },
        });

        
        setMemberId(response.data.id);
        setRole(response.data.role);
      } catch (error) {
        console.error("❌ 사용자 정보 조회 실패:", error.response?.data || error);
      }
    };

    fetchUserData();
  }, [token]);

  /** ✅ 공지사항 데이터 불러오기 */
  useEffect(() => {
    if (!token) return; // ❌ memberId 체크 제거

    const fetchAnnouncement = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8090/api/courses/${courseId}/announcements/${announcementId}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );

       
        setTitle(response.data.title);
        setContent(response.data.content);
      } catch (error) {
        console.error("❌ 공지사항 불러오기 실패:", error.response?.data || error);
      }
    };

    fetchAnnouncement();
  }, [courseId, announcementId, token]);

  /** ✅ 공지사항 수정 요청 */
  const handleUpdate = async () => {
 
    
    if (!token || !memberId) {
      alert("❌ 사용자 인증이 필요합니다. 다시 로그인해주세요.");
      return;
    }

    if (role !== "PROFESSOR" && role !== "ADMIN") {
      alert("❌ 권한이 없습니다.");
      return;
    }

    try {
      const requestBody = {
        memberId: memberId, // ✅ 확실히 포함
        title,
        content,
      };

      await axios.put(
        `http://localhost:8090/api/courses/${courseId}/announcements/${announcementId}/edit`,
        requestBody,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      alert("✅ 공지사항이 수정되었습니다.");
      navigate(`/courses/${courseId}/announcements`);
    } catch (error) {
      console.error("❌ 공지사항 수정 실패:", error.response?.data || error);
      alert("❌ 공지사항 수정에 실패했습니다.");
    }
  };

  return (
    <div className="edit-container">
      <h2>📢 공지사항 수정</h2>
      <input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="제목" />
      <textarea value={content} onChange={(e) => setContent(e.target.value)} placeholder="내용"></textarea>
      <button onClick={handleUpdate} disabled={!memberId || !token || (role !== "PROFESSOR" && role !== "ADMIN")}>
        수정 완료
      </button>
      <button onClick={() => navigate(-1)}>취소</button>
    </div>
  );
};

export default AnnouncementEdit;
