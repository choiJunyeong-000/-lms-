import React, { useState, useEffect, useMemo } from 'react';
import axios from 'axios';
// ⬇️ CSS Modules로 import
import styles from './TeamProjectHistory.module.css';

function TeamProjectHistory({ selectedTeamId }) {
  const [studentInfo, setStudentInfo] = useState(null);
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const token = localStorage.getItem("token");

  // 전달받은 팀 ID를 기준으로 제출 내역 필터링
  const filteredSubmissions = useMemo(() => {
    return Array.isArray(submissions)
      ? submissions.filter(submission => submission.teamId === selectedTeamId)
      : [];
  }, [submissions, selectedTeamId]);
  
  useEffect(() => {
    if (!token || !selectedTeamId) {
      setError("로그인 또는 팀 선택이 필요합니다.");
      setLoading(false);
      return;
    }

    const fetchStudentData = async () => {
      try {
        // 학생 정보 불러오기
        const userResponse = await axios.get("http://localhost:8090/api/users/me", {
          headers: { Authorization: `Bearer ${token}` }
        });
        if (!userResponse.data || !userResponse.data.id) {
          setError("학생 정보를 찾을 수 없습니다.");
          return;
        }
        setStudentInfo(userResponse.data);

        // 선택한 팀의 제출 내역 불러오기
        const submissionsResponse = await axios.get("http://localhost:8090/api/team-project-submissions", {
          headers: { Authorization: `Bearer ${token}` },
          params: { teamId: selectedTeamId }
        });
        
        if (!submissionsResponse.data || !Array.isArray(submissionsResponse.data)) {
          setError("이 팀에서 제출한 프로젝트가 없습니다.");
          return;
        }
        setSubmissions(submissionsResponse.data);
      } catch (error) {
        setError("데이터를 불러오는 중 오류가 발생했습니다.");
      } finally {
        setLoading(false);
      }
    };

    fetchStudentData();
  }, [token, selectedTeamId]);

  if (loading) {
    return (
      <div className={styles.teamProjectHistory}>
        <h2>팀 프로젝트 제출 내역</h2>
        <p>데이터 불러오는 중...</p>
      </div>
    );
  }

  return (
    <div className={styles.teamProjectHistory}>
      <h2>팀 프로젝트 제출 내역</h2>
      {error ? (
        <p className={styles.errorMessage}>{error}</p>
      ) : filteredSubmissions.length === 0 ? (
        <p>제출된 프로젝트가 없습니다.</p>
      ) : (
        <table className={styles.historyTable}>
          <thead>
            <tr>
              <th>팀명</th>
              <th>제출 파일</th>
              <th>제출 시간</th>
            </tr>
          </thead>
          <tbody>
            {filteredSubmissions.map((submission, index) => (
              <tr key={`${submission.id}-${index}`}>
                <td>{submission.teamName || '팀명 미지정'}</td>
                <td>
                  {submission.files ? (
                    <a href={`/uploads/${submission.files}`} download>
                      {submission.files.split(/[\\/]/).pop()}
                    </a>
                  ) : (
                    <span>파일 없음</span>
                  )}
                </td>
                <td>{new Date(submission.submittedAt).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default TeamProjectHistory;
