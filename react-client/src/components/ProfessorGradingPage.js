import React, { useState, useEffect } from "react";
import axios from "axios";

const ProfessorGradingPage = () => {
  const [exams, setExams] = useState([]);
  const [selectedExam, setSelectedExam] = useState(null);
  const [submissions, setSubmissions] = useState([]);
  const [grades, setGrades] = useState({});

  const token = localStorage.getItem("token");

  // ✅ 교수의 시험 목록 가져오기
  useEffect(() => {
    axios.get("http://localhost:8090/api/exams", {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(response => setExams(response.data))
    .catch(error => console.error("시험 목록 불러오기 실패:", error));
  }, []);

  // ✅ 특정 시험의 응시 기록 조회
  const fetchSubmissions = (examId) => {
    setSelectedExam(examId);
    axios.get(`http://localhost:8090/api/exams/${examId}/submissions`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(response => setSubmissions(response.data))
    .catch(error => console.error("응시 기록 불러오기 실패:", error));
  };

  // ✅ 채점 입력 핸들러
  const handleGradeChange = (submissionId, score) => {
    setGrades(prev => ({ ...prev, [submissionId]: score }));
  };

  // ✅ 채점 저장 API 호출
  const submitGrade = (submissionId) => {
    const score = grades[submissionId];
    if (!score) {
      alert("점수를 입력하세요!");
      return;
    }

    axios.post(`http://localhost:8090/api/exams/${selectedExam}/submissions/${submissionId}/grade`, { score }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(() => alert("채점 완료!"))
    .catch(error => console.error("채점 실패:", error));
  };

  return (
    <div className="grading-container">
      <h2>시험 채점 관리</h2>
      
      {/* 시험 목록 */}
      <h3>시험 목록</h3>
      <ul>
        {exams.map(exam => (
          <li key={exam.id} onClick={() => fetchSubmissions(exam.id)}>
            {exam.title} ({exam.date})
          </li>
        ))}
      </ul>

      {/* 학생 응시 기록 */}
      {selectedExam && (
        <div>
          <h3>시험 응시 기록</h3>
          <table>
            <thead>
              <tr>
                <th>학생 ID</th>
                <th>학생 이름</th>
                <th>제출된 답안</th>
                <th>점수 입력</th>
                <th>채점</th>
              </tr>
            </thead>
            <tbody>
              {submissions.map(sub => (
                <tr key={sub.id}>
                  <td>{sub.studentId}</td>
                  <td>{sub.studentName}</td>
                  <td>{sub.answer}</td>
                  <td>
                    <input
                      type="number"
                      value={grades[sub.id] || ""}
                      onChange={(e) => handleGradeChange(sub.id, e.target.value)}
                    />
                  </td>
                  <td>
                    <button onClick={() => submitGrade(sub.id)}>채점 완료</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default ProfessorGradingPage;
