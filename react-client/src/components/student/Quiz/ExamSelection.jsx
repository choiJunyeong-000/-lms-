import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';

function ExamSelection() {
    const { courseId } = useParams(); // URL에서 courseId 가져오기
    console.log("Fetched courseId:", courseId); // courseId 확인

    const [examId, setExamId] = useState(""); // 초기값을 빈 문자열로 설정
    const [exams, setExams] = useState([]);
    const [questions, setQuestions] = useState([]); // 문제 목록 상태 추가
    const [loading, setLoading] = useState(true);
    const token = localStorage.getItem("token");
    const navigate = useNavigate();

    const fetchExams = useCallback(async () => {
        console.log("시험 목록 요청 중... courseId:", courseId); // courseId 확인
        if (!token) {
            console.error("로그인이 필요합니다.");
            setLoading(false);
            return;
        }

        try {
            const response = await axios.get(`http://localhost:8090/api/courses/${courseId}/exams`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            console.log("시험 목록 응답:", response.data);
            setExams(response.data); // 응답 데이터를 그대로 사용
        } catch (error) {
            console.error("시험 목록 불러오기 오류:", error.response ? error.response.data : error.message);
        } finally {
            setLoading(false);
        }
    }, [courseId, token]);

    useEffect(() => {
        fetchExams();
    }, [fetchExams]);

    // 선택된 시험의 문제 목록 가져오기
    const fetchQuestions = useCallback(async (examId) => {
        if (!examId) return;

        try {
            const response = await axios.get(`http://localhost:8090/api/exams/${examId}/questions`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            console.log("문제 목록 응답:", response.data);
            setQuestions(response.data); // 문제 목록 상태 업데이트
        } catch (error) {
            console.error("문제 목록 불러오기 오류:", error.response ? error.response.data : error.message);
        }
    }, [token]);

    const handleExamChange = (e) => {
        const selectedExamId = e.target.value;
        console.log("선택된 시험 ID:", selectedExamId); // 추가된 로그
        setExamId(selectedExamId); // 선택된 시험 ID 업데이트
        fetchQuestions(selectedExamId); // 문제 목록 가져오기
    };

    const handleStartQuiz = () => {
        if (examId) {
            navigate(`/courses/${courseId}/quiz/${examId}`); // 퀴즈 시작 페이지로 이동
        }
    };

    if (loading) return <div>📡 시험 목록 불러오는 중...</div>;

    return (
        <div>
            <h2>시험 선택</h2>
            <label htmlFor="exam-select">시험 선택:</label>
            <select
                id="exam-select"
                value={examId} // 초기값을 빈 문자열로 설정했으므로 null이 아닌 빈 문자열로 설정
                onChange={handleExamChange} // 드롭다운에서 선택된 값을 examId에 저장하고 문제 리스트 가져오기
            >
                <option value="">시험을 선택하세요</option>
                {exams.map((exam) => (
                    <option key={exam.id} value={exam.id}>
                        {exam.title}
                    </option>
                ))}
            </select>

            <h3>문제 목록</h3>
            <ul>
                {questions.map((question) => (
                    <li key={question.id}>{question.text}</li> // 문제 텍스트를 리스트로 표시
                ))}
            </ul>

            <button onClick={handleStartQuiz} className="start-btn" disabled={!examId}>
                퀴즈 시작
            </button>
        </div>
    );
}

export default ExamSelection;
