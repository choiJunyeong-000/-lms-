import React, { useState, useEffect } from "react";
import axios from "axios";

const AddAnnouncementForm = ({ courseId, weekNumber }) => {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [memberId, setMemberId] = useState(null); // 🔹 memberId 상태 추가
  const token = localStorage.getItem("token");

  // 🔹 로그인한 사용자 정보 가져오기
  useEffect(() => {
    if (token) {
      const fetchUserData = async () => {
        try {
          const userResponse = await axios.get("http://localhost:8090/api/users/me", {
            headers: { Authorization: `Bearer ${token}` },
          });
          setMemberId(userResponse.data.id); // ✅ 로그인한 사용자의 memberId 저장
        } catch (error) {
          console.error("사용자 정보 조회 실패:", error);
          setMessage("❌ 사용자 정보를 가져오는 데 실패했습니다.");
        }
      };

      fetchUserData();
    }
  }, [token]); // 🔹 token이 변경될 때 실행

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage("");

    if (!title.trim() || !content.trim()) {
      setMessage("⚠️ 제목과 내용을 입력해주세요.");
      setLoading(false);
      return;
    }

    if (!token) {
      setMessage("❌ 로그인 후 이용해주세요.");
      setLoading(false);
      return;
    }

    if (!memberId) {
      setMessage("❌ 사용자 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.");
      setLoading(false);
      return;
    }

    try {
      // 🔹 API 요청 (쿼리 파라미터로 memberId 추가)
      await axios.post(
        `http://localhost:8090/api/courses/${courseId}/weeks/${weekNumber}/announcements?memberId=${memberId}`,
        { title, content },
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setMessage("✅ 공지사항이 등록되었습니다.");
      setTitle("");
      setContent("");
    } catch (error) {
      console.error("📢 공지사항 업로드 실패:", error);
      setMessage("❌ 공지사항 추가 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: "500px", margin: "0 auto", padding: "20px", border: "1px solid #ccc", borderRadius: "10px", background: "#fff" }}>
      <h2>📢 공지사항 추가</h2>
      {message && <p style={{ color: message.startsWith("✅") ? "green" : "red" }}>{message}</p>}
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: "10px" }}>
          <label>제목</label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            style={{ width: "100%", padding: "8px", borderRadius: "5px", border: "1px solid #ccc" }}
          />
        </div>
        <div style={{ marginBottom: "10px" }}>
          <label>내용</label>
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            rows="4"
            style={{ width: "100%", padding: "8px", borderRadius: "5px", border: "1px solid #ccc" }}
          />
        </div>
        <button type="submit" disabled={loading || !memberId} style={{ width: "100%", padding: "10px", background: "#007bff", color: "#fff", border: "none", borderRadius: "5px", cursor: "pointer" }}>
          {loading ? "등록 중..." : "공지 추가"}
        </button>
      </form>
    </div>
  );
};

export default AddAnnouncementForm;