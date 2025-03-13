import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import "./AssignmentGrading.css";

const AssignmentGrading = () => {
  const { assignmentId } = useParams();
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const token = localStorage.getItem('token');

  useEffect(() => {
    const fetchSubmissions = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8090/api/submissions/${assignmentId}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        console.log("API 응답 데이터:", response.data);
        setSubmissions(response.data);
      } catch (err) {
        console.error('제출 내역 조회 실패:', err);
        setError('제출 내역을 불러오는데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchSubmissions();
  }, [assignmentId, token]);

  if (loading) return <div>제출 내역을 불러오는 중...</div>;
  if (error) return <div style={{ color: 'red' }}>{error}</div>;

  return (
    <div className="assignment-container">
      {/* 상단 제목 부분 */}
      <h2 className="assignment-header">과제 제출 내역 (과제 ID: {assignmentId})</h2>

      {submissions.length > 0 ? (
        <div className="assignment-list">
          {submissions.map((submission) => {
            // 백슬래시(\)를 슬래시(/)로 변환하여 경로를 정규화합니다.
            const normalizedFileUrl = submission.fileUrl.replace(/\\/g, "/");
            // 정규화된 경로에서 파일명만 추출합니다.
            const fileName = normalizedFileUrl.substring(
              normalizedFileUrl.lastIndexOf('/') + 1
            );

            return (
              <div className="assignment-item" key={submission.id}>
                <p>제출한 학생: {submission.studentId}</p>
                <p>파일명: {fileName}</p>
                <p>제출 시간: {new Date(submission.submittedAt).toLocaleString()}</p>
                
                {/* 파일 다운로드 버튼 */}
                <a
                  href={`http://localhost:8090/api/files/${encodeURIComponent(fileName)}`}
                  download={fileName}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <button>다운로드</button>
                </a>
              </div>
            );
          })}
        </div>
      ) : (
        <p>제출된 과제가 없습니다.</p>
      )}
    </div>
  );
};

export default AssignmentGrading;
