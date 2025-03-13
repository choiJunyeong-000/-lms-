import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';
import "./AssignmentDetail.css"; // 추가된 CSS 파일

const AssignmentDetail = () => {
    const { courseId, weekNumber } = useParams();
    const [assignments, setAssignments] = useState([]);
    const [courseName, setCourseName] = useState("");
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [selectedFile, setSelectedFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [isProfessor, setIsProfessor] = useState(false);
    const [isStudent, setIsStudent] = useState(false);

    const token = localStorage.getItem('token');
    const studentId = localStorage.getItem('studentId'); // 로그인 시 저장한 학번

    useEffect(() => {
        const fetchData = async () => {
            if (!token) {
                setError('로그인 정보가 없습니다.');
                setLoading(false);
                return;
            }
            try {
                // 과제 목록 불러오기
                const assignmentsResponse = await axios.get(
                    `http://localhost:8090/api/courses/${courseId}/weeks/${weekNumber}/assignments`,
                    { headers: { Authorization: `Bearer ${token}` } }
                );
                setAssignments(assignmentsResponse.data);

                // 강좌 정보 불러오기
                const courseResponse = await axios.get(
                    `http://localhost:8090/api/courses/${courseId}`,
                    { headers: { Authorization: `Bearer ${token}` } }
                );
                setCourseName(courseResponse.data.name);

                // 사용자 정보(역할) 확인
                const userResponse = await axios.get(
                    'http://localhost:8090/api/users/me',
                    { headers: { Authorization: `Bearer ${token}` } }
                );
                if (userResponse.data.role === 'PROFESSOR') {
                    setIsProfessor(true);
                } else if (userResponse.data.role === 'STUDENT') {
                    setIsStudent(true);
                }
            } catch (err) {
                console.error('데이터를 불러오는 중 오류 발생:', err);
                setError('데이터를 불러오는 데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [courseId, weekNumber, token]);

    const handleFileChange = (e) => {
        setSelectedFile(e.target.files[0]);
    };

    // 🔥 마감일 체크만 수행
    const handleSubmitAssignment = async (assignmentId, dueDate) => {
        const today = new Date();
        const assignmentDueDate = new Date(dueDate);

        // 🔥 마감일 체크 (마감일이 지났으면 제출 불가)
        if (today > assignmentDueDate) {
            alert('과제 제출 마감일이 지났습니다. 제출할 수 없습니다.');
            return;
        }

        if (!selectedFile) {
            alert('파일을 선택해주세요.');
            return;
        }

        setUploading(true);
        const formData = new FormData();
        formData.append('file', selectedFile);

        try {
            await axios.post(
                `http://localhost:8090/api/submissions/${assignmentId}/submit`,
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'multipart/form-data',
                        studentId: studentId // 헤더에 학번 전달
                    }
                }
            );
            alert('과제가 성공적으로 제출되었습니다!');
        } catch (err) {
            console.error('과제 제출 실패:', err);
            alert('과제 제출에 실패했습니다. 다시 시도해주세요.');
        } finally {
            setUploading(false);
            setSelectedFile(null);
        }
    };

    if (loading) return <div>로딩 중...</div>;
    if (error) return <div style={{ color: 'red' }}>{error}</div>;

    return (
        <div className="assignment-detail-container">
            <h2 className="assignment-detail-header">
                {courseName} 강좌의 {weekNumber}주차 과제
            </h2>
            
            {assignments.length > 0 ? (
                <ul className="assignment-detail-list">
                    {assignments.map((assignment) => {
                        // 마감일 변환
                        const formattedDueDate = assignment.dueDate 
                            ? new Date(assignment.dueDate).toLocaleString("ko-KR", { timeZone: "Asia/Seoul" }) 
                            : "마감일 정보 없음";

                        // 현재 날짜와 비교하여 제출 가능 여부 확인
                        const canSubmit = new Date() <= new Date(assignment.dueDate);

                        return (
                            <li className="assignment-detail-item" key={assignment.id}>
                                <p>제목: {assignment.title}</p>
                                <p>설명: {assignment.description}</p>
                                <p>마감일: {formattedDueDate}</p>

                                {isProfessor && (
                                    <div className="assignment-actions">
                                        <Link 
                                            to={`/api/courses/${courseId}/weeks/${weekNumber}/assignments/${assignment.id}/grade`}
                                        >
                                            <button>과제 평가하기</button>
                                        </Link>
                                    </div>
                                )}

                                {isStudent && (
                                    <div className="assignment-actions">
                                        <input
                                            className="assignment-file-input"
                                            type="file"
                                            onChange={handleFileChange}
                                        />
                                        {!canSubmit ? (
                                            <p style={{ color: 'red' }}>
                                                과제 제출 마감일이 지났습니다. 제출할 수 없습니다.
                                            </p>
                                        ) : (
                                            <button 
                                                onClick={() => handleSubmitAssignment(assignment.id, assignment.dueDate)}
                                                disabled={uploading}
                                            >
                                                {uploading ? '제출 중...' : '과제 제출'}
                                            </button>
                                        )}
                                    </div>
                                )}
                            </li>
                        );
                    })}
                </ul>
            ) : (
                <p>해당 주차에 과제가 없습니다.</p>
            )}
        </div>
    );
};

export default AssignmentDetail;
