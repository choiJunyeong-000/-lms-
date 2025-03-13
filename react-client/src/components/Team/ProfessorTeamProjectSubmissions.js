import React, { useEffect, useState } from 'react';
import axios from 'axios';
import "./ProfessorTeamProjectSubmissions.css";

const ProfessorTeamProjectSubmissions = () => {
  const [submissions, setSubmissions] = useState([]);
  const [filteredSubmissions, setFilteredSubmissions] = useState([]);
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const token = localStorage.getItem("token");
  // courseId는 강의 상세 페이지 등에서 저장해둔 값을 사용하거나 localStorage에서 가져옵니다.
  const courseId = localStorage.getItem("courseId");

  useEffect(() => {
    const fetchTeams = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8090/api/team?courseId=${courseId}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        console.log("해당 강의 팀 목록:", response.data);
        setTeams(response.data);
      } catch (err) {
        console.error("팀 목록 조회 실패:", err);
        setError("강의에 속한 팀 목록을 불러오는데 실패했습니다.");
      }
    };

    const fetchSubmissions = async () => {
      try {
        // teamId 없이 모든 제출 내역 조회 (백엔드에서 팀 ID 파라미터가 없으면 전체 제출 내역 반환)
        const response = await axios.get(
          'http://localhost:8090/api/team-project-submissions',
          { headers: { Authorization: `Bearer ${token}` } }
        );
        console.log("전체 제출 내역 응답 데이터:", response.data);
        setSubmissions(response.data);
      } catch (err) {
        console.error("제출 내역 조회 실패:", err);
        setError("팀 프로젝트 제출 내역을 불러오는데 실패했습니다.");
      }
    };

    const fetchData = async () => {
      if (token && courseId) {
        await Promise.all([fetchTeams(), fetchSubmissions()]);
      } else {
        setError("토큰 또는 강의 정보가 없습니다.");
      }
      setLoading(false);
    };

    fetchData();
  }, [token, courseId]);

  useEffect(() => {
    // teams 배열에서 해당 강의의 팀 id 목록 생성
    const teamIds = teams.map(team => team.id);
    // 전체 제출 내역에서 teamId가 teamIds에 포함된 제출만 필터링
    const filtered = submissions.filter(submission => teamIds.includes(submission.teamId));
    setFilteredSubmissions(filtered);
  }, [submissions, teams]);

  if (loading) return <div>팀 프로젝트 제출 내역을 불러오는 중...</div>;
  if (error) return <div style={{ color: 'red' }}>{error}</div>;

  return (
    <div>
      <h2>팀 프로젝트 제출 내역 (교수용)</h2>
      {filteredSubmissions.length > 0 ? (
        <table border="1" cellPadding="8" cellSpacing="0">
          <thead>
            <tr>
              <th>프로젝트명</th>
              <th>팀 이름</th>
              <th>제출 시간</th>
              <th>다운로드</th>
            </tr>
          </thead>
          <tbody>
            {filteredSubmissions.map((submission) => {
              // 백엔드에서 저장한 files 필드는 절대 경로 형태입니다.
              // 백슬래시(\)를 슬래시(/)로 변환하고 파일명만 추출합니다.
              const normalizedFileUrl = submission.files.replace(/\\/g, "/");
              const fileName = normalizedFileUrl.substring(normalizedFileUrl.lastIndexOf("/") + 1);
              return (
                <tr key={submission.id}>
                  <td>{submission.projectName}</td>
                  <td>{submission.teamName}</td>
                  <td>{new Date(submission.submittedAt).toLocaleString()}</td>
                  <td>
                    <a
                      href={`http://localhost:8090/api/files/${encodeURIComponent(fileName)}`}
                      download={fileName}
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      다운로드
                    </a>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      ) : (
        <p>해당 강의에 제출된 팀 프로젝트가 없습니다.</p>
      )}
    </div>
  );
};

export default ProfessorTeamProjectSubmissions;