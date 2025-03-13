import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import "./ExamPage.css";

const ExamPage = () => {
  const { examId } = useParams();
  const [exam, setExam] = useState(null);
  const [answers, setAnswers] = useState({});
  const [loading, setLoading] = useState(true);
  const [selectedQuestionId, setSelectedQuestionId] = useState(null);
  const [submitted, setSubmitted] = useState(false); // 제출 상태 추가
  const [score, setScore] = useState(null); // 점수 상태 추가

  useEffect(() => {
    const fetchExam = async () => {
      try {
        const token = localStorage.getItem("token");
        const studentId = localStorage.getItem("studentId");
        if (!token || !studentId) {
          alert("로그인이 필요합니다.");
          return;
        }

        // 제출 상태 확인
        const submissionStatusResponse = await axios.get(`http://localhost:8090/api/exams/${examId}/questions/submission-status`, {
          params: { studentId },
          headers: { Authorization: `Bearer ${token}` },
        });
        setSubmitted(submissionStatusResponse.data);

        // 시험 문제 불러오기
        const examResponse = await axios.get(`http://localhost:8090/api/exams/${examId}/questions`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        console.log("🔥 응답 받은 exam 데이터:", examResponse.data);

        const examData = {
          ...examResponse.data,
          questions: examResponse.data,
        };
        setExam(examData);
      } catch (error) {
        console.error("시험 정보를 불러오는 중 오류 발생", error);
      } finally {
        setLoading(false);
      }
    };

    fetchExam();

    // 드래그 및 오른쪽 클릭 비활성화
    document.addEventListener("contextmenu", (e) => e.preventDefault());
    document.addEventListener("dragstart", (e) => e.preventDefault());

    return () => {
      document.removeEventListener("contextmenu", (e) => e.preventDefault());
      document.removeEventListener("dragstart", (e) => e.preventDefault());
    };
  }, [examId]);

  const handleChange = (questionId, answer) => {
    setAnswers({ ...answers, [questionId]: answer });
  };

  const handleSubmit = async () => {
    try {
      const token = localStorage.getItem("token");
      const studentId = localStorage.getItem("studentId");
      if (!token || !studentId) {
        alert("로그인이 필요합니다.");
        return;
      }

      const response = await axios.post(
        `http://localhost:8090/api/exams/${examId}/questions/submit`,
        { studentId, answers },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      alert("답안이 제출되었습니다.");
      console.log("제출한 답안:", response.data);
      setSubmitted(true); // 제출 상태를 true로 설정

      const gradingResponse = await axios.post(
        `http://localhost:8090/api/grade/exam`,
        { studentId, examId, answers },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      setScore(gradingResponse.data); // 점수 설정
    } catch (error) {
      console.error("답안을 제출하는 중 오류 발생:", error);
      alert("답안 제출 실패: " + error.message);
    }
  };

  if (loading) {
    return <p>시험 정보를 불러오는 중...</p>;
  }

  if (!exam || !exam.questions || exam.questions.length === 0) {
    return <p>현재 시험 문제가 없습니다.</p>;
  }

  const questions = exam.questions || [];
  const selectedQuestion = questions.find(q => q.id === selectedQuestionId);

  return (
    <div className="exam-container">
      <div className="exam-sidebar">
        <h3>문제 목록</h3>
        {questions.map((question,index) => (
          <button
            key={question.id}
            onClick={() => setSelectedQuestionId(question.id)}
            className={`question-button ${answers[question.id] ? 'correct' : ''}`}
            style={{
              backgroundColor: answers[question.id] ? '#d6d6d6' : '#ffffff',
              color: answers[question.id] ? '#000' : '#007bff',
            }}
          >
            {index+1}
          </button>
        ))}
      </div>

      <div className="exam-content">
        <h2 className="exam-header">{exam.title}</h2>
        <div className="question-container">
          {selectedQuestion ? (
            <>
              <p className="question-text">{selectedQuestion.questionText}</p>
              <div className="options-container">
                {selectedQuestion.type === "MULTIPLE_CHOICE" ? (
                  selectedQuestion.answers.map((option, index) => (
                    <label key={index} className="option-label">
                      <input
                        type="radio"
                        name={`question-${selectedQuestion.id}`}
                        value={option}
                        checked={answers[selectedQuestion.id] === option}
                        onChange={() => handleChange(selectedQuestion.id, option)}
                      />
                      {option}
                    </label>
                  ))
                ) : (
                  <textarea
                    className="essay-input"
                    placeholder="답변을 입력하세요."
                    value={answers[selectedQuestion.id] || ""}
                    onChange={(e) => handleChange(selectedQuestion.id, e.target.value)}
                  />
                )}
              </div>
            </>
          ) : (
            <p>문제를 선택하세요.</p>
          )}
        </div>
        <button onClick={handleSubmit} className="submit-button" disabled={submitted}>
          {submitted ? "제출 완료" : "제출"}
        </button>
        {score !== null && (
          <div className="score-container">
            <h3>점수: {score}</h3>
          </div>
        )}
      </div>
    </div>
  );
};

export default ExamPage;
