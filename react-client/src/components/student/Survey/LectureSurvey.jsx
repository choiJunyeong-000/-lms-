// LectureSurvey.jsx
import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import './LectureSurvey.css';
import { useNavigate } from 'react-router-dom';

function LectureSurvey({ onComplete }) {
  const navigate = useNavigate();
  const [survey, setSurvey] = useState([]);
  const [responses, setResponses] = useState({});
  const [submitted, setSubmitted] = useState(false);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [loading, setLoading] = useState(false);
  const [, setError] = useState("");
  const [enrolledCourses, setEnrolledCourses] = useState([]);
  const [memberId, setMemberId] = useState(null);
  const [selectedCourseId, setSelectedCourseId] = useState(null);
  const [surveyStarted, setSurveyStarted] = useState(false);
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) {
      setError("로그인이 필요합니다.");
      return;
    }

    const fetchUserInfo = async () => {
      try {
        const response = await axios.get('http://localhost:8090/api/users/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setMemberId(response.data.id);
      } catch (error) {
        console.error("사용자 정보 가져오기 실패:", error);
        alert("사용자 정보를 불러올 수 없습니다. 다시 로그인해주세요.");
        navigate('/login');
      }
    };

    fetchUserInfo();
  }, [token, navigate]);

  useEffect(() => {
    if (!memberId) return;

    const fetchEnrollments = async () => {
      try {
        const response = await axios.get(`http://localhost:8090/api/enrollments/member/${memberId}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setEnrolledCourses(response.data);
      } catch (error) {
        console.error("수강 신청한 강의 불러오기 실패:", error);
        setError("수강 신청한 강의를 불러오는 중 오류가 발생했습니다.");
      }
    };

    fetchEnrollments();
  }, [memberId, token]);

  const startSurvey = async (courseId) => {
    setSelectedCourseId(courseId);
    setSurveyStarted(true);
    setLoading(true);
    setError("");
    setResponses({});

    try {
      const response = await axios.get(`http://localhost:8090/api/surveys/lecture/${courseId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setSurvey(response.data);
      setCurrentQuestionIndex(0);
    } catch (error) {
      console.error("설문 데이터 요청 오류:", error);
      setError("설문 데이터를 불러오는 중 오류가 발생했습니다.");
    }
    setLoading(false);
  };

  const handleResponseChange = useCallback((questionId, value) => {
    setResponses(prev => {
      const updatedResponses = { ...prev, [questionId]: value };
      console.log(`📌 응답 저장됨 - 설문 ID: ${questionId}, 선택값: ${value}`);
      console.log("📌 현재 responses 상태:", updatedResponses);
      return updatedResponses;
    });
  }, []);

  const handleSubmit = async () => {
    if (!survey.every(q => responses[q.id] !== undefined)) {
      alert("모든 질문에 답변해야 합니다!");
      return;
    }

    // 중복 체크
    for (let [questionId] of Object.entries(responses)) {
      try {
        await axios.post(
          "http://localhost:8090/api/survey-responses/check",
          { surveyId: questionId, memberId: memberId },
          { headers: { Authorization: `Bearer ${token}` } }
        );
      } catch (error) {
        if (error.response && error.response.status === 400) {
          alert(error.response.data);
          return;
        } else {
          alert("중복 체크 중 오류가 발생했습니다.");
          return;
        }
      }
    }

    const requestData = {
      courseId: selectedCourseId,
      memberId: memberId,
      responses: Object.entries(responses).map(([questionId, response]) => ({
        surveyId: questionId,
        response: response
      }))
    };

    try {
      await axios.post("http://localhost:8090/api/survey-responses/submit", requestData, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setSubmitted(true);
      alert("설문이 성공적으로 제출되었습니다!");
      if (onComplete) onComplete();
    } catch (error) {
      alert("설문 제출 중 오류가 발생했습니다.");
    }
  };

  // onBack 대신 내부 상태 초기화 함수 handleBack 사용
  const handleBack = () => {
    setSurveyStarted(false);
    setSubmitted(false);
    setResponses({});
    setCurrentQuestionIndex(0);
  };

  return (
    <div className="survey-container">
      <h2>강의 설문</h2>
      {!surveyStarted ? (
        <div className="course-selection">
          {enrolledCourses.map(course => (
            <div key={course.courseId} className="course-item">
              <span>{course.courseName}</span>
              <button onClick={() => startSurvey(course.courseId)}>설문 시작</button>
            </div>
          ))}
        </div>
      ) : (
        <>
          {submitted ? (
            <>
              <p className="submit-result">설문이 제출되었습니다! 감사합니다.</p>
              <button onClick={handleBack}>목록으로 돌아가기</button>
            </>
          ) : (
            <>
              {loading ? <p>설문 데이터를 불러오는 중...</p> : (
                <div className="survey-item">
                  <p>{survey[currentQuestionIndex]?.title}</p>
                  {survey[currentQuestionIndex]?.options ? (
                    survey[currentQuestionIndex].options.split(',').map(option => (
                      <label key={option} className="survey-option">
                        <input
                          type="radio"
                          name={`question-${survey[currentQuestionIndex].id}`}
                          value={option}
                          checked={responses[survey[currentQuestionIndex].id] === option}
                          onChange={() => handleResponseChange(survey[currentQuestionIndex].id, option)}
                        />
                        {option}
                      </label>
                    ))
                  ) : (
                    <p>선택지가 없습니다.</p>
                  )}
                </div>
              )}

              <div className="survey-navigation">
                <button onClick={() => setCurrentQuestionIndex(prev => prev - 1)} disabled={currentQuestionIndex === 0}>
                  이전
                </button>
                <button onClick={() => setCurrentQuestionIndex(prev => prev + 1)} disabled={currentQuestionIndex === survey.length - 1}>
                  다음
                </button>
              </div>

              {currentQuestionIndex === survey.length - 1 && (
                <button onClick={handleSubmit}>제출</button>
              )}
            </>
          )}
        </>
      )}
    </div>
  );
}

export default LectureSurvey;
