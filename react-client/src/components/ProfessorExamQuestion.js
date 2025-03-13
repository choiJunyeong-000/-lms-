import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import "./ProfessorExamQuestion.css";

const ProfessorExamQuestion = () => {
  const { examId } = useParams();
  const [examTitle, setExamTitle] = useState("");
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [successMessage, setSuccessMessage] = useState("");
  const [selectedQuestionId, setSelectedQuestionId] = useState(null);

  const defaultNewQuestion = {
    questionText: "",
    answers: ["", "", "", ""],
    correctAnswer: "",
    score: "",
    type: "MULTIPLE_CHOICE",
  };

  const [newQuestion, setNewQuestion] = useState(defaultNewQuestion);

  // ✅ 시험 정보 가져오기
  const fetchExamInfo = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem("token");

      const response = await axios.get(`http://localhost:8090/api/exams/${examId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (response.data && response.data.title) {
        setExamTitle(response.data.title);
      } else {
        setExamTitle("제목 없음");
      }
    } catch (error) {
      console.error("❌ 시험 정보 불러오기 실패:", error.response?.data || error.message);
      setExamTitle("시험 정보를 불러올 수 없습니다.");
    } finally {
      setLoading(false);
    }
  };

  // ✅ 문제 목록 가져오기
  const fetchQuestions = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem("token");

      const response = await axios.get(`http://localhost:8090/api/exams/${examId}/questions`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (Array.isArray(response.data)) {
        setQuestions(response.data);
      }
    } catch (error) {
      console.error("❌ 문제 목록 불러오기 실패:", error.response?.data || error.message);
      setQuestions([]);
    } finally {
      setLoading(false);
    }
  };

  // ✅ 문제 선택하여 수정 폼에 로드
  const handleSelectQuestion = (question) => {
    setSelectedQuestionId(question.id);
    setNewQuestion({
      questionText: question.questionText,
      answers: question.answers || ["", "", "", ""],
      correctAnswer: question.correctAnswer || "",
      score: question.score || "",
      type: question.type || "MULTIPLE_CHOICE",
    });
  };

  // ✅ 문제 추가 및 수정
  const handleSaveQuestion = async () => {
    let formattedType = newQuestion.type;

    if (!newQuestion.questionText.trim()) {
      alert("문제 내용을 입력하세요.");
      return;
    }

    if (newQuestion.type === "MULTIPLE_CHOICE" && (newQuestion.answers.some(ans => !ans.trim()) || !newQuestion.correctAnswer)) {
      alert("객관식 문제의 4개 답안을 모두 입력하고 정답을 선택하세요.");
      return;
    }

    if (!newQuestion.score || isNaN(newQuestion.score) || newQuestion.score <= 0) {
      alert("유효한 점수를 입력하세요.");
      return;
    }

    try {
      const token = localStorage.getItem("token");

      if (selectedQuestionId) {
        // ✅ 기존 문제 수정
      

        await axios.put(`http://localhost:8090/api/exams/${examId}/questions/${selectedQuestionId}`, newQuestion, {
          headers: { Authorization: `Bearer ${token}` },
        });


        alert("문제가 수정되었습니다!"); // ✅ 수정 완료 알림 추가
      } else {
        // ✅ 새 문제 추가
       

        await axios.post(`http://localhost:8090/api/exams/${examId}/questions`, newQuestion, {
          headers: { Authorization: `Bearer ${token}` },
        });

     
        alert("문제가 추가되었습니다!");
      }

      // ✅ 문제 목록 갱신
      await fetchQuestions();

      // ✅ 입력 필드 초기화
      setSelectedQuestionId(null);
      setNewQuestion(defaultNewQuestion);

      setSuccessMessage("문제가 성공적으로 저장되었습니다!");
      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (error) {
      console.error("❌ 문제 저장 실패:", error.response?.data || error.message);
      alert("문제 저장에 실패했습니다. 다시 시도해주세요.");
    }
  };

  // ✅ 시험 등록 버튼 클릭 시 입력 폼 초기화
  const handleRegisterExam = () => {
    setSelectedQuestionId(null);
  
    const newQuestion = {
      ...defaultNewQuestion,
      number: questions.length + 1, // 현재 시험의 문제 개수 + 1
    };
  
    setNewQuestion(newQuestion);
  };
  
  

  useEffect(() => {
    fetchExamInfo();
    fetchQuestions();
  }, [examId]);

  return (
    <div className="exam-container2">
      {/* ✅ 왼쪽: 문제 목록 */}
      <div className="exam-sidebar">
        <h3>문제 목록</h3>
          {questions.map((question, index) => (
            <button key={question.id} onClick={() => handleSelectQuestion(question)} className="question-button">
            {index + 1}  {/* 1부터 시작하는 번호로 표시 */}
            </button>
            ))}
          <button onClick={handleRegisterExam} className="register-button">문제 추가</button>
      </div>


      {/* ✅ 오른쪽: 문제 추가/수정 폼 */}
      <div className="question-form">
        <h2>{selectedQuestionId ? "문제 수정" : "새 문제 추가"}</h2>

        {/* ✅ 문제 유형 선택 (제일 위로 이동) */}
        <div className="form-group">
          <label>문제 유형</label>
          <div className="question-type-toggle">
            <button 
              className={newQuestion.type === "MULTIPLE_CHOICE" ? "active" : ""}
              onClick={() => setNewQuestion({ ...newQuestion, type: "MULTIPLE_CHOICE", answers: ["", "", "", ""], correctAnswer: "" })}
            >
              객관식 (4지선다)
            </button>
            <button 
              className={newQuestion.type === "ESSAY" ? "active" : ""}
              onClick={() => setNewQuestion({ ...newQuestion, type: "ESSAY", answers: [], correctAnswer: "" })}
            >
              서술형
            </button>
          </div>
        </div>

        <div className="form-group">
          <label>문제</label>
          <input
            type="text"
            placeholder="문제 내용을 입력하세요."
            value={newQuestion.questionText}
            onChange={(e) => setNewQuestion({ ...newQuestion, questionText: e.target.value })}
          />
        </div>

        <div className="form-group">
          <label>배점</label>
          <input
            type="number"
            placeholder="배점을 입력하세요."
            value={newQuestion.score}
            onChange={(e) => setNewQuestion({ ...newQuestion, score: e.target.value })}
          />
        </div>

        {/* ✅ 객관식 문제일 경우만 답변 필드 표시 */}
        {newQuestion.type === "MULTIPLE_CHOICE" && (
          <>
            <div className="form-group">
              <label>객관식 답변</label>
              {newQuestion.answers.map((answer, index) => (
                <input
                  key={index}
                  type="text"
                  placeholder={`답변 ${index + 1}`}
                  value={answer}
                  onChange={(e) => {
                    const updatedAnswers = [...newQuestion.answers];
                    updatedAnswers[index] = e.target.value;
                    setNewQuestion({ ...newQuestion, answers: updatedAnswers });
                  }}
                />
              ))}
            </div>

            <div className="form-group">
              <label>정답</label>
              <select
                value={newQuestion.correctAnswer}
                onChange={(e) => setNewQuestion({ ...newQuestion, correctAnswer: e.target.value })}
              >
                <option value="">정답 선택</option>
                {newQuestion.answers.map((answer, index) => (
                  <option key={index} value={answer}>
                    {answer}
                  </option>
                ))}
              </select>
            </div>
          </>
        )}

        <button onClick={handleSaveQuestion} className="save-button">
          {selectedQuestionId ? "등록" : "등록"}
        </button>
      </div>
    </div>
  );
};

export default ProfessorExamQuestion;
