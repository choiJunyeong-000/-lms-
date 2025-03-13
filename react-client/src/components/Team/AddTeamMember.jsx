import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";
import "./AddTeamMember.css";

function AddTeamMember() {
  const { state } = useLocation();
  const { courseId, courseName } = state || {};

  const [teams, setTeams] = useState([]);
  const [selectedTeam, setSelectedTeam] = useState("");
  const [members, setMembers] = useState([]);
  const [selectedMember, setSelectedMember] = useState("");
  const [role, setRole] = useState("팀원");
  const [message, setMessage] = useState("");
  const [memberId, setMemberId] = useState(null);
  // 현재 선택된 팀의 기존 팀원 상태
  const [currentTeamMembers, setCurrentTeamMembers] = useState([]);
  const token = localStorage.getItem("token");

  // 🔹 현재 로그인한 사용자 정보 가져오기 (memberId 확인)
  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await axios.get("http://localhost:8090/api/users/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        console.log("현재 로그인한 memberId:", response.data.id);
        setMemberId(response.data.id);
      } catch (error) {
        console.error("사용자 정보 가져오기 실패:", error);
      }
    };

    fetchUserInfo();
  }, [token]);

  // 🔹 강의 ID 기반 팀 목록 불러오기
  useEffect(() => {
    if (!courseId) return;

    const fetchTeams = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8090/api/team?courseId=${courseId}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );

        if (Array.isArray(response.data)) {
          setTeams(response.data);
          if (response.data.length > 0) {
            setSelectedTeam(response.data[0].id);
          }
        } else {
          setMessage("팀 데이터를 올바르게 불러오지 못했습니다.");
        }
      } catch (err) {
        console.error("팀 정보 불러오기 실패:", err);
        setMessage("팀 정보를 가져오는 중 오류 발생.");
      }
    };

    fetchTeams();
  }, [courseId, token]);

  // 🔹 수강 신청한 학생 목록 불러오기 (courseId 기반)
  useEffect(() => {
    if (!courseId) {
      console.warn("⚠️ courseId가 없음, fetchMembers 실행 안 함.");
      return;
    }

    const fetchMembers = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8090/api/enrollments/course/${courseId}/students`,
          { headers: { Authorization: `Bearer ${token}` } }
        );

        console.log("📌 수강 신청한 학생 목록:", response.data);
        setMembers(response.data);

        if (response.data.length > 0 && response.data[0].memberId) {
          setSelectedMember(response.data[0].memberId);
        }
      } catch (err) {
        console.error("❌ 수강 학생 목록 불러오기 실패:", err);
      }
    };

    fetchMembers();
  }, [courseId, token]);

  // 🔹 선택된 팀의 현재 팀원 목록 불러오기
  useEffect(() => {
    if (!selectedTeam) return;
    const fetchTeamMembers = async () => {
      try {
        const response = await axios.get("http://localhost:8090/api/team-members", {
          headers: { Authorization: `Bearer ${token}` },
          params: { teamId: selectedTeam },
        });
        console.log("현재 팀원 목록:", response.data);
        setCurrentTeamMembers(response.data);
      } catch (error) {
        console.error("팀원 정보 불러오기 실패:", error);
      }
    };

    fetchTeamMembers();
  }, [selectedTeam, token]);

  // 🔹 팀원 추가 요청 (팀장 중복 및 동일 멤버 중복 추가 방지)
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedTeam) {
      setMessage("팀이 선택되지 않았습니다.");
      return;
    }

    /// 현재 팀원 목록에서, member_id와 selectedMember(숫자 변환)를 비교
if (currentTeamMembers.some(m => m.member_id === Number(selectedMember))) {
  setMessage("이미 해당 팀에 등록된 멤버입니다.");
  return;
}

    // 역할이 팀장인 경우, 해당 팀에 팀장이 있는지 체크
    if (
      role === "팀장" &&
      currentTeamMembers.some(member => member.role === "팀장" || member.roll === "팀장")
    ) {
      setMessage("이미 팀에 팀장이 등록되어 있습니다.");
      return;
    }

    try {
      const payload = {
        teamId: selectedTeam,
        memberId: selectedMember,
        role,
      };

      console.log("📤 팀원 추가 요청 데이터:", payload);

      await axios.post("http://localhost:8090/api/team-members", payload, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setMessage("✅ 팀원이 성공적으로 추가되었습니다!");
      // 추가 후, 현재 팀원 목록 갱신
      const updatedResponse = await axios.get("http://localhost:8090/api/team-members", {
        headers: { Authorization: `Bearer ${token}` },
        params: { teamId: selectedTeam },
      });
      setCurrentTeamMembers(updatedResponse.data);
    } catch (error) {
      console.error("❌ 팀원 추가 실패:", error);
      setMessage("팀원 추가 실패!");
    }
  };

  return (
    <div>
      <h2>팀원 추가 - 강의: {courseName || "강의 정보 없음"}</h2>
      {message && <p>{message}</p>}
      <form onSubmit={handleSubmit}>
        <label>
          팀 선택:
          <select
            value={selectedTeam}
            onChange={(e) => setSelectedTeam(e.target.value)}
            required
          >
            {teams.length === 0 ? (
              <option value="">팀 없음</option>
            ) : (
              teams.map((team) => (
                <option key={team.id} value={team.id}>
                  {team.name}
                </option>
              ))
            )}
          </select>
        </label>
        <br />
        <label>
          멤버 선택:
          <select
            value={selectedMember}
            onChange={(e) => setSelectedMember(e.target.value)}
            required
          >
            {members.length === 0 ? (
              <option value="">학생 없음</option>
            ) : (
              members.map((member) => (
                <option key={member.memberId} value={member.memberId}>
                  {member.memberName}
                </option>
              ))
            )}
          </select>
        </label>
        <br />
        <label>
          역할:
          <select value={role} onChange={(e) => setRole(e.target.value)}>
            <option value="팀원">팀원</option>
            <option value="팀장">팀장</option>
          </select>
        </label>
        <br />
        <button type="submit" disabled={!selectedTeam || members.length === 0}>
          팀원 추가
        </button>
      </form>
    </div>
  );
}

export default AddTeamMember;
