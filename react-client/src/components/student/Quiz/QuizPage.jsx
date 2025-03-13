import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';
import './QuizPage.css';

function QuizPage() {
    const { examId, courseId } = useParams(); // ✅ `useParams()` 호출 한 번으로 합침
    const [quizzes, setQuizzes] = useState([]);
    const [currentQuestion, setCurrentQuestion] = useState(0);
    const [answers, setAnswers] = useState({});
    const [score, setScore] = useState(null);
    const [quizCompleted, setQuizCompleted] = useState(false);
    const [timeLeft, setTimeLeft] = useState(60);
    const [quizStarted, setQuizStarted] = useState(false);
    const [errorMessage, setErrorMessage] = useState(null);
    const [loading, setLoading] = useState(true);
    const [, setError] = useState("");
    const token = localStorage.getItem("token");
    const navigate = useNavigate();

    // ✅ 퀴즈 데이터 불러오기
    const fetchQuizzes = useCallback(async () => {
        if (!token) {
            setError("로그인이 필요합니다.");
            setLoading(false);
            return;
        }

        try {
            const response = await axios.get(`http://localhost:8090/api/exams/${examId}/questions`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setQuizzes(response.data);
        } catch (error) {
            console.error("🚨 퀴즈 데이터 불러오기 오류:", error);
            setError("퀴즈 데이터를 불러오는 중 오류가 발생했습니다.");
        } finally {
            setLoading(false);
        }
    }, [examId, token]);

    // ✅ 퀴즈 자동 제출
    const handleQuizTimeout = useCallback(async () => {
        try {
            const response = await axios.post(`/api/quizzes/submit`, {
                courseId,
                answers
            }, {
                headers: { Authorization: `Bearer ${token}` }
            });

            setScore(response.data.score);
            setQuizCompleted(true);
        } catch (error) {
            console.error("🚨 퀴즈 제출 오류:", error);
            setError("퀴즈 제출 중 오류가 발생했습니다.");
        }
    }, [courseId, answers, token]);

    // ✅ 퀴즈 데이터 로드
    useEffect(() => {
        fetchQuizzes();
    }, [fetchQuizzes]);

    // ✅ 타이머 관리
    useEffect(() => {
        if (quizStarted) {
            const timer = setInterval(() => {
                setTimeLeft((prevTime) => {
                    if (prevTime <= 1) {
                        clearInterval(timer);
                        handleQuizTimeout();
                        return 0;
                    }
                    return prevTime - 1;
                });
            }, 1000);

            return () => clearInterval(timer);
        }
    }, [quizStarted, handleQuizTimeout]);

    // ✅ 퀴즈 시작
    const startQuiz = () => {
        setQuizStarted(true);
        setTimeLeft(60);
        setScore(null);
        setCurrentQuestion(0);
        setAnswers({});
        setQuizCompleted(false);
    };

    // ✅ 퀴즈 제출 버튼
    const handleNextQuestion = () => {
        if (currentQuestion < quizzes.length - 1) {
            setCurrentQuestion((prev) => prev + 1);
        } else {
            handleQuizTimeout();
        }
    };

    // ✅ 강좌로 돌아가기
    const handleBackToCourse = () => {
        navigate(`/student/courses/${courseId}`);
    };

    if (loading) return <div>📡 퀴즈 데이터 불러오는 중...</div>;

    return (
        <div className="dashboard">
            <main className="main-content">
                {/* ✅ 퀴즈 컨테이너 */}
                <div className="quiz-container">
                    <h2>퀴즈</h2>

                    {/* ✅ 버튼 컨테이너 (제목 아래 정렬) */}
                    <div className="button-container">
                        {!quizStarted && !quizCompleted && (
                            <button onClick={startQuiz} className="start-btn">
                                퀴즈 시작
                            </button>
                        )}
                        <button className="back-btn" onClick={handleBackToCourse}>
                            뒤로가기
                        </button>
                    </div>

                    {/* ✅ 퀴즈 완료 후 결과 표시 */}
                    {quizCompleted ? (
                        <div className="score-result">
                            <h3>퀴즈 완료! 당신의 점수: {score} / {quizzes.length}</h3>
                        </div>
                    ) : (
                        quizStarted && (
                            <div className="quiz-content">
                                <div className="quiz-question">
                                    <h3>{quizzes[currentQuestion].question}</h3>
                                    <div>
                                        {quizzes[currentQuestion].options.map((option, index) => (
                                            <label key={index} className="quiz-option">
                                                <input
                                                    type="radio"
                                                    name={`quiz-${quizzes[currentQuestion].id}`}
                                                    value={option}
                                                    onChange={() => setAnswers({
                                                        ...answers,
                                                        [quizzes[currentQuestion].id]: option
                                                    })}
                                                    checked={answers[quizzes[currentQuestion].id] === option}
                                                />
                                                {option}
                                            </label>
                                        ))}
                                    </div>
                                </div>

                                {/* ✅ 타이머 표시 */}
                                <div className="quiz-timer">
                                    <span>남은 시간: {timeLeft}초</span>
                                </div>

                                {/* ✅ 다음 문제 버튼 */}
                                <div className="quiz-footer">
                                    <button onClick={handleNextQuestion} className="next-btn">
                                        {currentQuestion < quizzes.length - 1 ? "다음 문제" : "퀴즈 완료"}
                                    </button>
                                </div>
                            </div>
                        )
                    )}
                </div>
                  {/* 퀴즈 완료 후 결과 표시 */}
                  {quizCompleted && (
                    <div className="score-result">
                        <h3>퀴즈 완료! 당신의 점수: {score} / {quizzes.length}</h3>
                    </div>
                )}

                {/* 오류 메시지 표시 */}
                {errorMessage && <div className="error-message">{errorMessage}</div>}

                <button className="back-btn" onClick={() => navigate(`/courses/${courseId}`)}>뒤로가기</button>
            </main>
         
        </div>
    );
}

export default QuizPage;
