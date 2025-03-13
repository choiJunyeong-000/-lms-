import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
// ⬇️ CSS Modules로 import
import styles from './TeamProjectSubmit.module.css';

function TeamProjectSubmit({ selectedTeamId }) {
  const [files, setFiles] = useState([]);
  const [deadline, setDeadline] = useState(null);
  const [teamName, setTeamName] = useState("알 수 없는 팀");
  const [teamId, setTeamId] = useState(null);
  const [teamProjectId, setTeamProjectId] = useState(null);
  const [deadlinePassed, setDeadlinePassed] = useState(false);
  const [feedbackMessage, setFeedbackMessage] = useState("");
  const [isReady, setIsReady] = useState(false);
  const token = localStorage.getItem("token");

  // 선택한 팀 ID를 이용해서 팀 정보 및 프로젝트 ID 불러오기
  useEffect(() => {
    if (!token || !selectedTeamId) {
      setFeedbackMessage("로그인 또는 팀 선택이 필요합니다.");
      return;
    }

    const fetchTeamInfo = async () => {
      try {
        setTeamId(selectedTeamId);

        const projectsResponse = await axios.get(
          `http://localhost:8090/api/team-projects?teamId=${selectedTeamId}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );

        if (!projectsResponse.data || projectsResponse.data.length === 0) {
          setFeedbackMessage("이 팀에 등록된 프로젝트가 없습니다.");
          return;
        }

        const teamProject = projectsResponse.data[0];
        setTeamProjectId(teamProject.teamProjectId);
        setTeamName(teamProject.projectName || "알 수 없는 팀");
        setDeadline(new Date(teamProject.deadline));
        setIsReady(true);
      } catch (error) {
        setFeedbackMessage("팀 정보를 불러오는 중 오류가 발생했습니다.");
      }
    };

    fetchTeamInfo();
  }, [token, selectedTeamId]);

  // 마감일 체크
  useEffect(() => {
    if (deadline && new Date() > deadline) {
      setDeadlinePassed(true);
    } else {
      setDeadlinePassed(false);
    }
  }, [deadline]);

  const handleFileChange = useCallback((e) => {
    setFiles(Array.from(e.target.files));
  }, []);

  const handleRemoveFile = useCallback((index) => {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  }, []);

  // 제출하기
  const handleSubmit = useCallback(async () => {
    if (!teamId || !teamProjectId) {
      setFeedbackMessage("팀 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.");
      return;
    }
    if (files.length === 0) {
      setFeedbackMessage("파일을 업로드하세요!");
      return;
    }
    if (deadlinePassed) {
      setFeedbackMessage("제출 기한이 지났습니다.");
      return;
    }

    const formData = new FormData();
    formData.append("file", files[0]);

    try {
      await axios.post(
        `http://localhost:8090/api/team-project-submissions/${teamProjectId}/${teamId}/submit`,
        formData,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setFeedbackMessage("프로젝트가 성공적으로 제출되었습니다!");
      setFiles([]);
    } catch (error) {
      setFeedbackMessage("제출 중 오류가 발생했습니다.");
    }
    setTimeout(() => setFeedbackMessage(""), 3000);
  }, [files, deadlinePassed, teamId, teamProjectId, token]);

  return (
    <div className={styles.teamProjectSubmit}>
      <h2>팀 프로젝트 제출 ({teamName})</h2>
      {feedbackMessage && (
        <div className={styles.feedbackMessage} role="alert">
          {feedbackMessage}
        </div>
      )}
      {deadlinePassed ? (
        <p className={styles.deadlineMessage}>제출 기한이 지났습니다.</p>
      ) : (
        <>
          <label htmlFor="file-upload" className={styles.visuallyHidden}>
            파일 업로드
          </label>
          <input
            id="file-upload"
            type="file"
            onChange={handleFileChange}
            accept=".zip,.pdf,.docx,.pptx"
            multiple
            disabled={!isReady || deadlinePassed}
          />
          <ul>
            {files.map((file, index) => (
              <li key={index}>
                {file.name}{' '}
                <button onClick={() => handleRemoveFile(index)}>삭제</button>
              </li>
            ))}
          </ul>
          <button onClick={handleSubmit} disabled={!isReady || deadlinePassed}>
            제출하기
          </button>
        </>
      )}
    </div>
  );
}

export default TeamProjectSubmit;
