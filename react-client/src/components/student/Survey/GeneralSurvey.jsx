import React, { useState, useEffect, useMemo } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function GeneralSurvey({ onComplete }) {
  const navigate = useNavigate();
  const [groupedSurveys, setGroupedSurveys] = useState({});
  const [selectedSurveyType, setSelectedSurveyType] = useState(null);
  const [selectedTimeGroup, setSelectedTimeGroup] = useState(null); // 여기서는 '타이틀 그룹'으로 사용
  const [responses, setResponses] = useState({});
  const [submitted, setSubmitted] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [memberId, setMemberId] = useState(null);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const token = useMemo(() => localStorage.getItem("token"), []);

  const surveyTypeNames = {
    "GENERAL_SURVEY": "정기 설문조사",
    "MEAL_SURVEY": "정기 급식 만족도",
    "ACADEMIC_SURVEY": "정기 학교 설문"
  };

  // 사용자 정보 가져오기
  useEffect(() => {
    if (!token) {
      setError("로그인이 필요합니다.");
      setLoading(false);
      return;
    }
    const fetchUserInfo = async () => {
      try {
        const response = await axios.get('http://localhost:8090/api/users/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setMemberId(response.data.id);
      } catch (error) {
        alert("사용자 정보를 불러올 수 없습니다. 다시 로그인해주세요.");
        navigate('/login');
      }
    };
    fetchUserInfo();
  }, [token, navigate]);

  // 설문 데이터 가져와서 타이틀별로 그룹화하기
  useEffect(() => {
    if (!token || !memberId) return;
    const fetchSurveys = async () => {
      try {
        const response = await axios.get('http://localhost:8090/api/surveys', {
          headers: { Authorization: `Bearer ${token}` }
        });
        console.log("survey data:", response.data);
        const grouped = response.data.reduce((acc, survey) => {
          // 특정 surveyType 제외 (필요시 조건 수정)
          if (survey.surveyType === "LECTURE_EVALUATION" || survey.surveyType === "TEAM_EVALUATION") {
            return acc;
          }
          if (!acc[survey.surveyType]) {
            acc[survey.surveyType] = {};
          }
          // 타이틀별로 그룹화 (시작시간 대신 survey.title 사용)
          const titleKey = survey.title || "미정";
          if (!acc[survey.surveyType][titleKey]) {
            acc[survey.surveyType][titleKey] = [];
          }
          acc[survey.surveyType][titleKey].push(survey);
          return acc;
        }, {});
        setGroupedSurveys(grouped);
        setLoading(false);
      } catch (error) {
        setError("설문 데이터를 불러오는 중 오류가 발생했습니다.");
        setLoading(false);
      }
    };
    fetchSurveys();
  }, [token, memberId]);

  // 설문 유형 선택 시
  const handleSelectSurveyType = (surveyType) => {
    setSelectedSurveyType(surveyType);
    setSelectedTimeGroup(null);
    setResponses({});
  };

  // 타이틀 그룹(설문 목록) 선택 시
  const handleSelectTimeGroup = (groupKey) => {
    setSelectedTimeGroup(groupKey);
    setResponses({});
    setCurrentQuestionIndex(0);
  };

  // 제출 처리 (선택한 그룹의 설문만 처리)
  const handleSubmit = async () => {
    const currentGroupSurveys = groupedSurveys[selectedSurveyType][selectedTimeGroup];
    if (Object.keys(responses).length !== currentGroupSurveys.length) {
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
      alert("✅ 설문이 성공적으로 제출되었습니다!");
      if (onComplete) onComplete();
    } catch (error) {
      alert("설문 제출 중 오류가 발생했습니다.");
    }
  };

  // 설문 유형 목록으로 돌아가기
  const handleBackToSurveyType = () => {
    if (onComplete) {
      onComplete();
    } else {
      setSelectedSurveyType(null);
      setSelectedTimeGroup(null);
      setResponses({});
    }
  };

  // 타이틀 그룹(설문 목록) 화면으로 돌아가기
  const handleBackToGroupList = () => {
    setSelectedTimeGroup(null);
    setResponses({});
  };

  if (loading) return <p>📡 설문 데이터를 불러오는 중...</p>;
  if (error) return <p className="error-message">{error}</p>;
  if (submitted) {
    return (
      <div className="survey-container">
        <p>🎉 설문이 제출되었습니다! 감사합니다.</p>
        <button onClick={handleBackToSurveyType}>목록으로 돌아가기</button>
      </div>
    );
  }

  // 설문 유형 선택 화면 (LectureSurvey와 유사한 스타일)
  if (!selectedSurveyType) {
    return (
      <div className="survey-container">
        <h2>설문 유형 선택</h2>
        <div className="course-selection">
          {Object.keys(groupedSurveys).map((type) => (
            <div key={type} className="course-item">
              <span>{surveyTypeNames[type] || type}</span>
              <button onClick={() => handleSelectSurveyType(type)}>설문 선택</button>
            </div>
          ))}
        </div>
      </div>
    );
  }

  // 선택한 설문 유형 내 타이틀 그룹(설문 목록) 화면 (LectureSurvey 스타일)
  if (selectedSurveyType && !selectedTimeGroup) {
    const groupKeys = Object.keys(groupedSurveys[selectedSurveyType]);
    return (
      <div className="survey-container">
        <h2>{surveyTypeNames[selectedSurveyType] || selectedSurveyType} 설문 목록</h2>
        <div className="course-selection">
          {groupKeys.map((groupKey) => (
            <div key={groupKey} className="course-item">
              <span>{groupKey} ({groupedSurveys[selectedSurveyType][groupKey].length}문항)</span>
              <button onClick={() => handleSelectTimeGroup(groupKey)}>해당 설문 시작</button>
            </div>
          ))}
        </div>
        <button onClick={handleBackToSurveyType}>설문 유형 목록으로 돌아가기</button>
      </div>
    );
  }

  // 선택한 타이틀 그룹의 설문 질문 화면 (LectureSurvey와 유사한 단일 질문 인터페이스)
  if (selectedSurveyType && selectedTimeGroup) {
    const currentGroupSurveys = groupedSurveys[selectedSurveyType][selectedTimeGroup];
    const currentQuestion = currentGroupSurveys[currentQuestionIndex];
    return (
      <div className="survey-container">
        <h2>{surveyTypeNames[selectedSurveyType] || selectedSurveyType} 설문</h2>
        <h3>{selectedTimeGroup}</h3>
        <div className="survey-item">
          <p>{currentQuestion.title}</p>
          <div>
            {currentQuestion.options.split(',').map(option => (
              <label key={option}>
                <input
                  type="radio"
                  name={`question-${currentQuestion.id}`}
                  value={option}
                  checked={responses[currentQuestion.id] === option}
                  onChange={() =>
                    setResponses(prev => ({ ...prev, [currentQuestion.id]: option }))
                  }
                />
                {option}
              </label>
            ))}
          </div>
        </div>
        <div className="survey-navigation">
          <button onClick={() => setCurrentQuestionIndex(prev => prev - 1)} disabled={currentQuestionIndex === 0}>
            이전
          </button>
          <button onClick={() => setCurrentQuestionIndex(prev => prev + 1)} disabled={currentQuestionIndex === currentGroupSurveys.length - 1}>
            다음
          </button>
        </div>
        {currentQuestionIndex === currentGroupSurveys.length - 1 && (
          <button onClick={handleSubmit}>제출</button>
        )}
        <button onClick={handleBackToGroupList}>목록으로 돌아가기</button>
      </div>
    );
  }

  return null;
}

export default GeneralSurvey;
