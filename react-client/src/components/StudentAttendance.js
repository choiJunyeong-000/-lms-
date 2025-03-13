import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams } from "react-router-dom"; // ✅ URL에서 memberId 가져오기
import "./StudentAttendance.css"; 

const StudentAttendance = () => {
    const { courseId, memberId } = useParams(); // ✅ URL에서 courseId, memberId 추출
    const token = localStorage.getItem("token");

    const [attendanceData, setAttendanceData] = useState([]);

    useEffect(() => {
        console.log("✅ StudentAttendance에서 받은 값:", { courseId, memberId });

        if (!courseId || !memberId) {
            console.error("❌ 유효하지 않은 courseId 또는 memberId:", courseId, memberId);
            return;
        }

        axios.get(`http://localhost:8090/api/student-attendance/${courseId}/${memberId}`, {
            headers: { Authorization: `Bearer ${token}` },
        })
        .then(response => {
            console.log("📢 출석부 데이터 (수정 후):", response.data);
            setAttendanceData(response.data);
        })
        .catch(error => console.error("❌ 출석부 데이터 로딩 실패:", error));
    }, [courseId, memberId]);

    return (
        <div className="attendance-container">
            <h2>출석부</h2>
            <table>
                <thead>
                    <tr>
                        <th>주차</th>
                        <th>강의명</th>
                        <th>출석</th>
                    </tr>
                </thead>
                <tbody>
                    {attendanceData.map((lecture, index) => (
                        <tr key={index}>
                            <td>{lecture.weekId}주차</td>
                            <td>{lecture.fileName}</td>
                            <td>{lecture.present ? "✅" : "❌"}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default StudentAttendance;
