import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";
import "./CreateTeamProject.css";

function CreateTeamProject() {
  const { state } = useLocation();
  const { courseId, courseName } = state || {};

  const [teams, setTeams] = useState([]);
  const [selectedTeamId, setSelectedTeamId] = useState("");
  const [projectName, setProjectName] = useState("");
  const [deadline, setDeadline] = useState(""); // 자동 설정할 마감일
  const [message, setMessage] = useState("");
  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchTeams = async () => {
      try {
        const response = await axios.get(`http://localhost:8090/api/team?courseId=${courseId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const teamsData = Array.isArray(response.data) ? response.data : [];
        setTeams(teamsData);
      } catch (error) {
        console.error("팀 목록 불러오기 실패:", error);
      }
    };

    if (courseId) {
      fetchTeams();
    }
  }, [courseId, token]);

  // 팀 선택 시 해당 팀의 마감일 자동 입력
  const handleTeamChange = (e) => {
    const selectedId = e.target.value;
    setSelectedTeamId(selectedId);

    // 선택한 팀의 deadline을 찾아 자동 입력
    const selectedTeam = teams.find((team) => team.id.toString() === selectedId);
    if (selectedTeam) {
      setDeadline(selectedTeam.deadline || ""); // 마감일이 없으면 빈 값
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedTeamId || !projectName || !deadline) {
      setMessage("모든 필드를 입력하세요.");
      return;
    }

    try {
      const payload = {
        teamId: selectedTeamId,
        projectName,
        deadline, // 선택한 팀의 마감일을 그대로 사용
        courseId,
      };

      const response = await axios.post("http://localhost:8090/api/team-projects", payload, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setMessage("✅ 팀 프로젝트가 성공적으로 생성되었습니다!");
      setSelectedTeamId("");
      setProjectName("");
      setDeadline("");
    } catch (error) {
      console.error("❌ 팀 프로젝트 생성 실패:", error);
      setMessage("🚨 팀 프로젝트 생성 실패");
    }
  };

  return (
    <div>
      <h2>팀 프로젝트 생성 - 강의: {courseName || "강의 정보 없음"}</h2>
      {message && <p>{message}</p>}
      <form onSubmit={handleSubmit}>
        <div>
          <label>팀 선택: </label>
          <select value={selectedTeamId} onChange={handleTeamChange} required>
            <option value="">팀을 선택하세요</option>
            {teams.map((team) => (
              <option key={team.id} value={team.id}>
                {team.name}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label>프로젝트 이름: </label>
          <input type="text" value={projectName} onChange={(e) => setProjectName(e.target.value)} required />
        </div>
        <div>
          <label>마감일: </label>
          <input type="datetime-local" value={deadline} readOnly /> {/* 자동 설정된 마감일, 수정 불가 */}
        </div>
        <button type="submit">생성하기</button>
      </form>
    </div>
  );
}

export default CreateTeamProject;
