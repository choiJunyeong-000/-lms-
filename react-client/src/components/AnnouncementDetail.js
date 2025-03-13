import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./AnnouncementDetail.css"; // ✅ 스타일 추가

const AnnouncementDetail = () => {
  const { courseId, announcementId } = useParams();
  const [announcement, setAnnouncement] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchAnnouncement = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8090/api/courses/${courseId}/announcements/${announcementId}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setAnnouncement(response.data);
      } catch (err) {
        setError("공지사항을 불러오는 데 실패했습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchAnnouncement();
  }, [courseId, announcementId, token]);

  if (loading) return <p>📡 로딩 중...</p>;
  if (error) return <p>❌ {error}</p>;

  return (
    <div className="announcement-detail">
      <h2>{announcement.title}</h2>
      <p>
        <strong>작성자:</strong> {announcement.authorName}  &nbsp;
        <strong>작성일:</strong> {new Date(announcement.createdAt).toLocaleDateString()}
        </p>
      <hr />
      <p>{announcement.content}</p>
      <button className="back-button" onClick={() => navigate(-1)}>목록</button>
    </div>
  );
};

export default AnnouncementDetail;