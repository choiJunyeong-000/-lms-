import React, { useEffect, useState } from "react";
import axios from "axios";
import { Bar } from "react-chartjs-2";
import styles from "./AdminSurveyResponses.module.css";

// 강의 평가 설문인 경우와 일반 설문인 경우의 키를 구분하는 헬퍼 함수
const getKey = (survey) =>
  survey.surveyType === "LECTURE_EVALUATION"
    ? `${survey.id}-${survey.courseId}`
    : survey.id;

const AdminSurveyResponses = () => {
  const token = localStorage.getItem("token");

  // 강의 목록을 저장할 상태
  const [courses, setCourses] = useState([]);
  // 설문 목록을 그룹화한 객체
  const [groupedSurveys, setGroupedSurveys] = useState({});
  const [loading, setLoading] = useState(true);
  const [errorSurveys, setErrorSurveys] = useState(null);

  // 설문별 응답 캐싱: { [key]: ArrayOfResponses } (여기서 key는 getKey() 결과)
  const [responsesBySurvey, setResponsesBySurvey] = useState({});
  // 그룹 확장 여부 (각 그룹의 전체 응답 펼치기)
  const [expandedGroups, setExpandedGroups] = useState({});
  // 개별 설문 그래프 토글 여부
  const [expandedCharts, setExpandedCharts] = useState({});

  // 차트 옵션 (y축 정수화)
  const chartOptions = {
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          precision: 0,
        },
      },
    },
  };

  /**
   * 1) 강의 목록 + 설문 목록 동시 불러오기
   *    - 강의 목록: /api/courses
   *    - 설문 목록: /api/surveys
   */
  useEffect(() => {
    const fetchData = async () => {
      try {
        // 두 API를 병렬로 호출
        const [coursesRes, surveysRes] = await Promise.all([
          axios.get("http://localhost:8090/api/courses", {
            headers: { Authorization: token ? `Bearer ${token}` : "" },
          }),
          axios.get("http://localhost:8090/api/surveys", {
            headers: { Authorization: token ? `Bearer ${token}` : "" },
          }),
        ]);

        // 1. 강의 목록 저장
        setCourses(coursesRes.data);

        // 2. 설문 목록 → 그룹화
        const surveys = surveysRes.data;
        const grouped = surveys.reduce((acc, survey) => {
          const type = survey.surveyType;
          if (!acc[type]) acc[type] = {};

          // 기본 그룹 키: survey.title || "제목없음"
          let groupKey = survey.title || "제목없음";

          // 만약 LECTURE_EVALUATION이라면, courseId로 강의 찾아서 이름 사용
          if (type === "LECTURE_EVALUATION") {
            if (survey.courseId) {
              const foundCourse = coursesRes.data.find(
                (c) => c.id === survey.courseId
              );
              if (foundCourse) {
                groupKey = `강의 ${foundCourse.name} (ID: ${foundCourse.id})`;
              } else {
                groupKey = `강의 ${survey.courseId}`;
              }
            }
          }

          if (!acc[type][groupKey]) acc[type][groupKey] = [];
          acc[type][groupKey].push(survey);
          return acc;
        }, {});

        setGroupedSurveys(grouped);
      } catch (error) {
        console.error("데이터 불러오기 오류:", error);
        setErrorSurveys("강의/설문 데이터를 불러오는 데 실패했습니다.");
      } finally {
        setLoading(false);
      }
    };

    if (token) {
      fetchData();
    } else {
      setLoading(false);
      setErrorSurveys("로그인이 필요합니다.");
    }
  }, [token]);

  /**
   * 2) 특정 설문의 응답 불러오기
   *    - API가 단일 객체를 반환할 수도 있으므로, 배열로 감싸서 처리
   */
  const fetchResponses = async (survey) => {
    try {
      const res = await axios.get(
        `http://localhost:8090/api/survey-responses/${survey.id}`,
        { headers: { Authorization: token ? `Bearer ${token}` : "" } }
      );
      let responses = [];
      if (Array.isArray(res.data)) {
        responses = res.data;
      } else if (res.data) {
        responses = [res.data];
      }
      console.log(
        `✅ 설문 ID: ${survey.id}, 강의 ID: ${survey.courseId}, 응답 인원: ${responses.length}`
      );
      setResponsesBySurvey((prev) => ({
        ...prev,
        [getKey(survey)]: responses,
      }));
    } catch (err) {
      console.error(`설문 ${survey.id} 응답 불러오기 오류:`, err);
      alert(`설문 ${survey.id} 응답을 불러오는 데 실패했습니다.`);
    }
  };

  /**
   * 3) 그룹 전체 펼치기/접기
   *    - 해당 그룹 내 모든 설문 응답을 미리 불러온 뒤 펼침
   */
  const handleViewGroupSubmissions = async (type, groupKey) => {
    const group = groupedSurveys[type][groupKey];
    for (const survey of group) {
      if (!responsesBySurvey[getKey(survey)]) {
        await fetchResponses(survey);
      }
    }
    const compositeKey = `${type}-${groupKey}`;
    setExpandedGroups((prev) => ({
      ...prev,
      [compositeKey]: !prev[compositeKey],
    }));
  };

  /**
   * 4) 개별 설문 그래프 토글
   */
  const toggleChart = async (survey) => {
    if (!responsesBySurvey[getKey(survey)]) {
      await fetchResponses(survey);
    }
    setExpandedCharts((prev) => ({
      ...prev,
      [getKey(survey)]: !prev[getKey(survey)],
    }));
  };

  // 로딩/에러 처리
  if (loading) return <div>데이터 불러오는 중...</div>;
  if (errorSurveys) return <div style={{ color: "red" }}>{errorSurveys}</div>;

  return (
    <div className={styles.adminSurveyResponses}>
      <h2 style={{ textAlign: "center" }}>설문 통계</h2>
      {Object.keys(groupedSurveys).map((type) => (
        <div key={type} className={styles.groupContainer}>
          <h3 className={styles.groupHeader}>{type}</h3>
          <div className={styles.groupBoxList}>
            {Object.keys(groupedSurveys[type]).map((groupKey) => {
              const group = groupedSurveys[type][groupKey];
              const compositeKey = `${type}-${groupKey}`;
              const isExpanded = expandedGroups[compositeKey];
              return (
                <div key={groupKey} className={styles.groupBox}>
                  <h4 className={styles.groupBoxHeader}>{groupKey}</h4>
                  <button
                    className={styles.groupButton}
                    onClick={() => handleViewGroupSubmissions(type, groupKey)}
                  >
                    {isExpanded ? "전체 응답 숨기기" : "전체 응답 보기"}
                  </button>
                  {isExpanded && (
                    <div className={styles.submissionGrid}>
                      {group.map((survey) => {
                        const surveyResponses =
                          responsesBySurvey[getKey(survey)] || [];
                        return (
                          <div key={survey.id} className={styles.surveyCard}>
                            <h5 className={styles.surveyCardHeader}>
                              {survey.description}
                            </h5>
                            <p>응답 인원: {surveyResponses.length}</p>
                            <button
                              className={styles.responseButton}
                              onClick={() => toggleChart(survey)}
                            >
                              {expandedCharts[getKey(survey)]
                                ? "그래프 숨기기"
                                : "그래프 보기"}
                            </button>
                            {expandedCharts[getKey(survey)] &&
                              (survey.questions && survey.questions.length > 0
                                ? survey.questions.map((question) => {
                                    const optionCounts = {};
                                    surveyResponses.forEach((resp) => {
                                      const chosenOption =
                                        resp.response &&
                                        resp.response[question.id];
                                      if (chosenOption) {
                                        optionCounts[chosenOption] =
                                          (optionCounts[chosenOption] || 0) + 1;
                                      }
                                    });
                                    const labels = Object.keys(optionCounts);
                                    const data = labels.map(
                                      (label) => optionCounts[label]
                                    );
                                    const chartData = {
                                      labels,
                                      datasets: [
                                        {
                                          label: "응답 인원",
                                          data,
                                          backgroundColor:
                                            "rgba(75,192,192,0.4)",
                                          borderColor:
                                            "rgba(75,192,192,1)",
                                          borderWidth: 1,
                                        },
                                      ],
                                    };
                                    return (
                                      <div
                                        key={question.id}
                                        className={styles.chartBox}
                                      >
                                        <h6 className={styles.chartBoxHeader}>
                                          {question.text}
                                        </h6>
                                        {labels.length > 0 ? (
                                          <div className={styles.chartContainer}>
                                            <Bar
                                              data={chartData}
                                              options={chartOptions}
                                            />
                                          </div>
                                        ) : (
                                          <p>응답이 없습니다.</p>
                                        )}
                                      </div>
                                    );
                                  })
                                : (() => {
                                    const optionCounts = {};
                                    surveyResponses.forEach((resp) => {
                                      const chosenOption = resp.response;
                                      if (!optionCounts[chosenOption]) {
                                        optionCounts[chosenOption] = 0;
                                      }
                                      optionCounts[chosenOption]++;
                                    });
                                    const labels = Object.keys(optionCounts);
                                    const data = labels.map(
                                      (label) => optionCounts[label]
                                    );
                                    const chartData = {
                                      labels,
                                      datasets: [
                                        {
                                          label: "응답 인원",
                                          data,
                                          backgroundColor:
                                            "rgba(75,192,192,0.4)",
                                          borderColor:
                                            "rgba(75,192,192,1)",
                                          borderWidth: 1,
                                        },
                                      ],
                                    };
                                    return labels.length > 0 ? (
                                      <div className={styles.chartContainer}>
                                        <Bar
                                          data={chartData}
                                          options={chartOptions}
                                        />
                                      </div>
                                    ) : (
                                      <p>응답이 없습니다.</p>
                                    );
                                  })())}
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      ))}
    </div>
  );
};

export default AdminSurveyResponses;
