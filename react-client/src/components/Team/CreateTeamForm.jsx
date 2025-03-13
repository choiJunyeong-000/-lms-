import React, { useState } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";
import "./CreateTeamForm.css";

function CreateTeamForm() {
  const { state } = useLocation();
  const courseId = state?.courseId || localStorage.getItem("courseId");
  const courseName = state?.courseName || "강의 정보 없음";

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [deadline, setDeadline] = useState("");
  const [message, setMessage] = useState("");
  const token = localStorage.getItem("token");

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!courseId) {
      setMessage("강의 정보가 없습니다. 올바른 courseId를 확인해주세요.");
      return;
    }

    try {
      const response = await axios.post(
        "http://localhost:8090/api/teams",
        {
          name,
          description,
          deadline,
          courseId,
        },
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      // 팀 마감일을 localStorage에 저장
      localStorage.setItem("teamDeadline", deadline);

      setMessage("✅ 팀이 성공적으로 생성되었습니다!");
      console.log("📌 팀 생성 완료:", response.data);
    } catch (error) {
      setMessage("🚨 팀 생성 실패!");
      console.error("🚨 오류 발생:", error);
    }
  };

  return (
    <div>
      <h2>팀 생성 - 강의: {courseName}</h2>
      {message && <p>{message}</p>}
      <form onSubmit={handleSubmit}>
        <label>
          팀 이름:
          <input value={name} onChange={(e) => setName(e.target.value)} required />
        </label>
        <br />
        <label>
          팀 설명:
          <input value={description} onChange={(e) => setDescription(e.target.value)} required />
        </label>
        <br />
        <label>
          마감일:
          <input type="datetime-local" value={deadline} onChange={(e) => setDeadline(e.target.value)} required />
        </label>
        <br />
        <button type="submit">팀 생성</button>
      </form>
    </div>
  );
}

export default CreateTeamForm;
