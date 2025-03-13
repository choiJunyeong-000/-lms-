import React, { useState, useEffect } from "react";
import "./CreateSurvey.css";

const CreateSurvey = () => {
  const token = localStorage.getItem("token");
  
  // 전역 제목: 모든 설문의 title에 공통 적용 (description은 개별 입력)
  const [globalTitle, setGlobalTitle] = useState("");
  
  // 개별 설문: description과 선택지만 입력 (title은 전역 제목 사용)
  const [surveyList, setSurveyList] = useState([{ description: "", options: [""] }]);
  
  // 전역 설정 (모든 설문에 동일하게 적용)
  const [globalSurveyType, setGlobalSurveyType] = useState("");
  const [globalEndDays, setGlobalEndDays] = useState("");
  const [globalSelectedCourseId, setGlobalSelectedCourseId] = useState("");
  const [courses, setCourses] = useState([]);
  
  // 제출 시 공통 시작 시간 (설문 시작 시간)
  const [commonStartDate] = useState(new Date());
  
  // 개별 설문 설명 변경 처리
  const handleSurveyChange = (index, field, value) => {
    const newSurveyList = [...surveyList];
    newSurveyList[index][field] = value;
    setSurveyList(newSurveyList);
  };

  // 개별 설문 선택지 변경 처리
  const handleOptionChange = (index, optIndex, value) => {
    const newSurveyList = [...surveyList];
    newSurveyList[index].options[optIndex] = value;
    setSurveyList(newSurveyList);
  };

  const addOption = (index) => {
    const newSurveyList = [...surveyList];
    newSurveyList[index].options.push("");
    setSurveyList(newSurveyList);
  };

  const removeOption = (index, optIndex) => {
    const newSurveyList = [...surveyList];
    if (newSurveyList[index].options.length > 1) {
      newSurveyList[index].options.splice(optIndex, 1);
      setSurveyList(newSurveyList);
    }
  };

  const addSurveyForm = () => {
    setSurveyList([...surveyList, { description: "", options: [""] }]);
  };

  // 전역 유형이 LECTURE_EVALUATION인 경우 강의 목록 한 번만 불러오기
  useEffect(() => {
    if (globalSurveyType === "LECTURE_EVALUATION" && courses.length === 0) {
      const fetchCourses = async () => {
        try {
          const response = await fetch("http://localhost:8090/api/courses", {
            headers: {
              "Content-Type": "application/json",
              Authorization: token ? `Bearer ${token}` : "",
            },
          });
          if (response.ok) {
            const data = await response.json();
            setCourses(data);
          } else {
            console.error("강의 목록을 불러오지 못했습니다.");
          }
        } catch (error) {
          console.error("강의 목록 요청 중 오류:", error);
        }
      };
      fetchCourses();
    }
  }, [globalSurveyType, courses, token]);

  // 종료 날짜 계산: 공통 시작 시간 + 종료 기간(일)
  const computeEndDate = (start, days) => {
    if (!start || !days) return null;
    const dt = new Date(start);
    dt.setDate(dt.getDate() + Number(days));
    return dt;
  };

  // ISO 8601 형식으로 변환 (밀리초 제거)
  const formatDateTime = (date) => {
    return date ? date.toISOString().split(".")[0] : null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // 전역 필드 유효성 검사
    if (!globalSurveyType) {
      alert("설문 유형을 선택해주세요.");
      return;
    }
    if (!globalEndDays) {
      alert("종료 기간을 선택해주세요.");
      return;
    }
    if (globalSurveyType === "LECTURE_EVALUATION" && !globalSelectedCourseId) {
      alert("강의 평가 설문인 경우, 강의를 반드시 선택해야 합니다.");
      return;
    }
    if (!globalTitle) {
      alert("공통 설문 제목을 입력해주세요.");
      return;
    }

    const commonStartDateTime = commonStartDate;
    for (const survey of surveyList) {
      const computedEndDate = computeEndDate(commonStartDateTime, globalEndDays);
      // title은 전역 제목, description은 개별 입력값 사용
      const surveyData = {
        title: globalTitle,
        description: survey.description,
        options: survey.options.join(", "),
        isActive: true,
        surveyType: globalSurveyType,
        startDate: formatDateTime(commonStartDateTime),
        endDate: formatDateTime(computedEndDate),
        courseId: globalSurveyType === "LECTURE_EVALUATION" ? Number(globalSelectedCourseId) : null,
      };

      try {
        const response = await fetch("http://localhost:8090/api/surveys/addsurvey", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: token ? `Bearer ${token}` : "",
          },
          body: JSON.stringify(surveyData),
        });
        if (!response.ok) {
          throw new Error("설문 추가 실패");
        }
      } catch (error) {
        console.error("설문 추가 중 오류 발생:", error);
        alert("설문 추가 중 오류가 발생했습니다.");
        return;
      }
    }
    
    alert("모든 설문이 성공적으로 추가되었습니다!");
    // 제출 후 상태 초기화
    setSurveyList([{ description: "", options: [""] }]);
    setGlobalSurveyType("");
    setGlobalEndDays("");
    setGlobalSelectedCourseId("");
    setGlobalTitle("");
  };

  return (
    <div className="create-survey-container">
      <h2>여러 설문 추가</h2>
      <form onSubmit={handleSubmit}>
        {/* 전역(공통) 질문 영역 */}
        <div className="common-questions">
          <div className="form-group">
            <label>공통 설문 제목:</label>
            <input
              type="text"
              value={globalTitle}
              onChange={(e) => setGlobalTitle(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label>설문 유형:</label>
            <select
              value={globalSurveyType}
              onChange={(e) => setGlobalSurveyType(e.target.value)}
              required
            >
              <option value="" disabled>
                -- 설문 유형 선택 --
              </option>
              <option value="MEAL_SURVEY">급식 만족도</option>
              <option value="ACADEMIC_SURVEY">학교 운영</option>
              <option value="LECTURE_EVALUATION">강의 평가</option>
              <option value="GENERAL_SURVEY">일반 설문</option>
            </select>
          </div>
          {globalSurveyType === "LECTURE_EVALUATION" && (
            <div className="form-group">
              <label>강의 선택:</label>
              <select
                value={globalSelectedCourseId}
                onChange={(e) => setGlobalSelectedCourseId(e.target.value)}
                required
              >
                <option value="" disabled>
                  -- 강의를 선택하세요 --
                </option>
                {courses.map((course) => (
                  <option key={course.id} value={course.id}>
                    {course.name}
                  </option>
                ))}
              </select>
            </div>
          )}
          <div className="form-group">
            <label>종료 기간 (시작일로부터 몇 일 후):</label>
            <select
              value={globalEndDays}
              onChange={(e) => setGlobalEndDays(e.target.value)}
              required
            >
              <option value="" disabled>
                -- 종료 기간 선택 --
              </option>
              {Array.from({ length: 14 }, (_, i) => i + 1).map((day) => (
                <option key={day} value={day}>
                  {day}일 후
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* 개별 질문 영역 */}
        <div className="individual-questions">
          {surveyList.map((survey, index) => (
            <div key={index} className="survey-form">
              <h3>설문 {index + 1}</h3>
              <div className="form-group">
                <label>설문 설명:</label>
                <textarea
                  value={survey.description}
                  onChange={(e) => handleSurveyChange(index, "description", e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <label>설문 선택지:</label>
                <div className="options-container">
                  {survey.options.map((option, optIndex) => (
                    <div key={optIndex} className="option-row">
                      <input
                        type="text"
                        value={option}
                        onChange={(e) =>
                          handleOptionChange(index, optIndex, e.target.value)
                        }
                        required
                        className="option-input"
                      />
                      <button
                        type="button"
                        className="remove-btn"
                        onClick={() => removeOption(index, optIndex)}
                      >
                        제거
                      </button>
                    </div>
                  ))}
                  <div className="option-buttons">
                    <button 
                      type="button" 
                      className="add-option-btn"
                      onClick={() => addOption(index)}
                    >
                      선택지 추가
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
          <button type="button" className="add-survey-btn" onClick={addSurveyForm}>
            설문 폼 추가
          </button>
        </div>

        <button type="submit" className="submit-btn">
          모든 설문 생성
        </button>
      </form>
    </div>
  );
};

export default CreateSurvey;