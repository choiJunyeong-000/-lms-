import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import './ExamDetail.css';

const ExamDetail = () => {
  const { examId } = useParams();
  const navigate = useNavigate();
  const [students, setStudents] = useState([]);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [essayAnswers, setEssayAnswers] = useState([]);
  const [scores, setScores] = useState({});
  const [loading, setLoading] = useState(true);
  const userRole = localStorage.getItem("role");

  useEffect(() => {
    const fetchStudents = async () => {
      try {
        const token = localStorage.getItem("token");
        if (!token) {
          alert("로그인이 필요합니다.");
          return;
        }

        const studentsResponse = await axios.get(
          `http://localhost:8090/api/exams/${examId}/submitted-students`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        setStudents(studentsResponse.data);
        console.log(studentsResponse.data);
      } catch (error) {
        console.error("학생 목록을 불러오는 중 오류 발생", error);
      } finally {
        setLoading(false);
      }
    };

    fetchStudents();
  }, [examId]);

  const fetchStudentAnswers = async (studentId) => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("로그인이 필요합니다.");
        return;
      }

      const response = await axios.get(
        `http://localhost:8090/api/essay-questions/exam/${examId}/answers`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      const filteredAnswers = response.data.filter(answer => answer.studentId === studentId);
      setEssayAnswers(filteredAnswers);
      setSelectedStudent(students.find(student => student.id === studentId));

      const initialScores = {};
      filteredAnswers.forEach(answer => {
        initialScores[answer.questionId] = answer.score || "";
      });
      setScores(initialScores);
    } catch (error) {
      console.error("학생 답변을 불러오는 중 오류 발생", error);
    }
  };

  const handleScoreChange = (questionId, value) => {
    setScores({ ...scores, [questionId]: value });
  };

  const handleSaveScores = async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("로그인이 필요합니다.");
        return;
      }

      const requests = Object.entries(scores).map(([questionId, score]) =>
        axios.put(
          `http://localhost:8090/api/essay-questions/score`,
          {
            studentId: selectedStudent.id,
            questionId: parseInt(questionId, 10),
            score: parseInt(score, 10),
          },
          { headers: { Authorization: `Bearer ${token}` } }
        )
      );

      await Promise.all(requests);
      alert("점수가 저장되었습니다.");
    } catch (error) {
      console.error("점수 저장 중 오류 발생", error);
      alert("점수 저장에 실패했습니다.");
    }
  };

  if (loading) {
    return <p>로딩 중...</p>;
  }

  return (
    <div className="exam-detail-container">
      <h1 className="exam-title">시험 상세 정보</h1>

      {userRole === "PROFESSOR" && (
        <div className="professor-section">
          <h2 className="student-list-title">학생 목록</h2>
          <ul className="student-list">
            {students.map(student => (
              <li
                key={student.id}
                onClick={() => fetchStudentAnswers(student.id)}
                className="student-item"
              >
                {student.name} - 총점: {student.score}
              </li>
            ))}
          </ul>

          {selectedStudent && (
            <div className="student-answers">
              <h2>{selectedStudent.name}의 서술형 답변</h2>
              {essayAnswers.length > 0 ? (
                <div className="answers-container">
                  {essayAnswers.map(answer => (
                    <div key={answer.questionId} className="answer-item">
                      <p><strong>문제:</strong> {answer.questionText}</p>
                      <p><strong>제출 답안:</strong> {answer.answerText}</p>
                      <input
                        type="number"
                        value={scores[answer.questionId]}
                        onChange={(e) => handleScoreChange(answer.questionId, e.target.value)}
                        placeholder="점수 입력"
                        className="score-input"
                      />
                    </div>
                  ))}
                  <button onClick={handleSaveScores} className="save-button">완료</button>
                </div>
              ) : (
                <p>서술형 답변이 없습니다.</p>
              )}
            </div>
          )}
        </div>
      )}

      {userRole === "STUDENT" && (
        <div className="student-section">
          <p>이 시험을 응시하려면 아래 버튼을 클릭하세요.</p>
          <button onClick={() => navigate(`/exam/${examId}/details`)} className="exam-button">응시하기</button>
        </div>
      )}
    </div>
  );
};

export default ExamDetail;
