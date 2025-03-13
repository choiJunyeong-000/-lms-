import React, { useState, useEffect } from 'react';
import axios from 'axios';
import TeamProjectSubmit from './TeamProjectSubmit';
import TeamProjectHistory from './TeamProjectHistory';
import styles from './TeamProject.module.css'; // <-- CSS Modules import

const TeamProject = () => {
  const [activeTab, setActiveTab] = useState('');
  const [studentInfo, setStudentInfo] = useState(null);
  const [teamProjects, setTeamProjects] = useState([]);
  const [teamMembers, setTeamMembers] = useState([]);
  const [studentTeamIds, setStudentTeamIds] = useState([]);
  const [selectedTeamId, setSelectedTeamId] = useState(null);
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) return;

    const fetchData = async () => {
      try {
        const [userResponse, teamMembersResponse] = await Promise.all([
          axios.get("http://localhost:8090/api/users/me", { headers: { Authorization: `Bearer ${token}` } }),
          axios.get("http://localhost:8090/api/team-members", { headers: { Authorization: `Bearer ${token}` } })
        ]);

        const student = userResponse.data;
        setStudentInfo(student);

        // 학생이 속한 모든 팀 ID
        const studentTeams = teamMembersResponse.data
          .filter(member => member.member_id === student.id)
          .map(member => member.team_id);
        setStudentTeamIds(studentTeams);

        // 초기 선택 팀
        if (studentTeams.length > 0) {
          setSelectedTeamId(studentTeams[0]);
        }

        setTeamMembers(teamMembersResponse.data);
      } catch (error) {
        console.error("데이터 로딩 실패:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [token]);

  useEffect(() => {
    if (!token || !selectedTeamId) return;

    const fetchProjects = async () => {
      try {
        const projectsResponse = await axios.get("http://localhost:8090/api/team-projects", {
          headers: { Authorization: `Bearer ${token}` },
          params: { teamId: selectedTeamId },
        });
        setTeamProjects(projectsResponse.data);
      } catch (error) {
        console.error("팀 프로젝트 불러오기 실패:", error);
      }
    };

    fetchProjects();
  }, [token, selectedTeamId]);

  useEffect(() => {
    if (!token) return;

    const fetchTeams = async () => {
      try {
        const teamsResponse = await axios.get("http://localhost:8090/api/team", {
          headers: { Authorization: `Bearer ${token}` }
        });
        setTeams(teamsResponse.data);
      } catch (error) {
        console.error("팀 목록 불러오기 실패:", error);
      }
    };

    fetchTeams();
  }, [token]);

  if (loading) {
    return <div>데이터 불러오는 중...</div>;
  }

  const getTeamNameById = (teamId) => {
    const teamData = teams.find(team => team.id === teamId);
    return teamData ? teamData.name : `팀 ${teamId}`;
  };

  const selectedTeamName = getTeamNameById(selectedTeamId);

  return (
    <div className={styles.teamProjectContainer}>
      <aside className={styles.teamProjectSidebar}>
        <h3>내 팀 목록</h3>
        <ul>
          {studentTeamIds.length > 0 ? (
            studentTeamIds.map((teamId) => (
              <li
                key={teamId}
                className={teamId === selectedTeamId ? styles.active : ''}
                onClick={() => setSelectedTeamId(teamId)}
              >
                {getTeamNameById(teamId)}
              </li>
            ))
          ) : (
            <li>등록된 팀이 없습니다.</li>
          )}
        </ul>
      </aside>

      <main className={styles.teamProjectContent}>
        <div className={styles.teamProjectList}>
          <h2>
            {studentInfo?.name}님의 팀 프로젝트 ({selectedTeamName})
          </h2>
          {teamProjects.length > 0 ? (
            teamProjects.map((project) => (
              <div key={project.team_id} className={styles.teamProjectCard}>
                <h3>{project.projectName}</h3>
                <p>
                  마감일: <strong>{new Date(project.deadline).toLocaleDateString()}</strong>
                </p>
              </div>
            ))
          ) : (
            <p>등록된 팀 프로젝트가 없습니다.</p>
          )}

          <h3>전체 팀원</h3>
          <ul className={styles.teamProjectMembers}>
            {teamMembers
              .filter(member => member.team_id === selectedTeamId)
              .map((member) => (
                <li key={member.id}>
                  {member.member?.name || member.name}
                  {(member.role === '팀장' || member.roll === '팀장') && ' (팀장)'}
                </li>
              ))
            }
          </ul>
        </div>

        {/* 조건부 컴포넌트 */}
        {activeTab === 'submit' && <TeamProjectSubmit selectedTeamId={selectedTeamId} />}
        {activeTab === 'history' && <TeamProjectHistory selectedTeamId={selectedTeamId} />}

        <div className={styles.teamProjectTabs}>
          <button
            className={activeTab === 'submit' ? styles.active : ''}
            onClick={() => setActiveTab(activeTab === 'submit' ? '' : 'submit')}
          >
            제출
          </button>
          <button
            className={activeTab === 'history' ? styles.active : ''}
            onClick={() => setActiveTab(activeTab === 'history' ? '' : 'history')}
          >
            제출 내역
          </button>
        </div>
      </main>
    </div>
  );
};

export default TeamProject;
