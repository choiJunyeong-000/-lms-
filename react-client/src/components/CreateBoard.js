import axios from "axios";
import React, { useState, useEffect } from "react";

const CreateBoard = ({ weekNumber, courseId, token }) => {
  const [title, setTitle] = useState("");
  const [memberId, setMemberId] = useState(null);

  // 사용자 정보를 가져오는 함수
  const fetchUserData = async () => {
    try {
      const response = await axios.get("http://localhost:8090/api/users/me", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setMemberId(response.data.id);
    } catch (error) {
      console.error("사용자 정보 조회 실패:", error);
    }
  };

  // 컴포넌트가 마운트될 때 사용자 정보 가져오기
  useEffect(() => {
    if (token) {
      fetchUserData();
    }
  }, [token]);

  const handleSubmit = async () => {
    if (!memberId) {
      alert("사용자 정보를 불러올 수 없습니다.");
      return;
    }

    try {
     

      const response = await axios.post(
        "http://localhost:8090/weekly-board/create",
        {
          title,
          weekNumber,
          courseId,
          memberId, // memberId 추가
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      alert("게시판이 생성되었습니다!");
    
    } catch (error) {
      console.error(
        "게시판 생성 실패:",
        error.response ? error.response.data : error.message
      );
      alert("게시판 생성에 실패했습니다.");
    }
  };

  return (
    <div>
      <h3>게시판 생성</h3>
      <input
        type="text"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        placeholder="게시판 제목"
      />
      <button onClick={handleSubmit}>저장</button>
    </div>
  );
};

export default CreateBoard;
