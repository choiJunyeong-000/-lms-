import React, { useState, useEffect } from 'react';
import { Bar } from 'react-chartjs-2';
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    BarElement,
    Title,
    Tooltip,
    Legend,
} from 'chart.js';
import axios from 'axios';
import './LearningStatus.css';
import { useParams } from 'react-router-dom';

// Chart.js 구성 요소 등록
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

function LearningStatus() {
    const { studentId } = useParams(); // URL에서 studentId 가져오기
    const [studentCourses, setStudentCourses] = useState([]); // 학생의 학습 데이터
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const token = localStorage.getItem("token"); // ✅ JWT 토큰 가져오기

    // ✅ API에서 학습 현황 데이터 불러오기
    useEffect(() => {
        if (!token) {
            console.warn("⚠️ 토큰이 없습니다. 로그인이 필요합니다.");
            setError("로그인이 필요합니다.");
            setLoading(false);
            return;
        }

        const fetchLearningStatus = async () => {
            try {
                const response = await axios.get(`/api/learning-status/${studentId}`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setStudentCourses(response.data.courses);
            } catch (error) {
                console.error("🚨 학습 데이터 불러오기 오류:", error);
                setError("학습 데이터를 불러오는 중 오류가 발생했습니다.");
            } finally {
                setLoading(false);
            }
        };

        fetchLearningStatus();
    }, [studentId, token]);

    if (loading) return <div>📡 학습 데이터를 불러오는 중...</div>;
    if (error) return <p className="error-message">{error}</p>;

    // 학습 진도 데이터 준비
    const progressData = {
        labels: studentCourses.map((course) => course.course_name),
        datasets: [
            {
                label: '진도율 (%)',
                data: studentCourses.map((course) => course.progress),
                backgroundColor: 'rgba(75, 192, 192, 0.6)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1,
            },
        ],
    };

    return (
        <div className="learning-status-dashboard">
            <main className="learning-status-content">
                <h2 className="learning-status-title">학습 현황</h2>

                {/* 학습 진도 그래프 */}
                <div className="learning-status-chart-container">
                    <h3 className="learning-status-subtitle">강좌별 학습 진도</h3>
                    {studentCourses.length > 0 ? (
                        <Bar data={progressData} options={{ responsive: true, plugins: { legend: { position: 'top' } } }} />
                    ) : (
                        <p>등록된 학습 데이터가 없습니다.</p>
                    )}
                </div>

                {/* 성적 및 출결 정보 테이블 */}
                <h3 className="learning-status-subtitle">성적 및 출결 정보</h3>
                <table className="learning-status-table">
                    <thead>
                        <tr>
                            <th>강좌명</th>
                            <th>진도율</th>
                            <th>성적</th>
                            <th>출결</th>
                        </tr>
                    </thead>
                    <tbody>
                        {studentCourses.length > 0 ? (
                            studentCourses.map((course) => (
                                <tr key={course.course_id}>
                                    <td>{course.course_name}</td>
                                    <td>{course.progress}%</td>
                                    <td>{course.score || '미정'}</td>
                                    <td>{course.attendance}</td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="4">등록된 학습 데이터가 없습니다.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </main>
        </div>
    );
}

export default LearningStatus;
