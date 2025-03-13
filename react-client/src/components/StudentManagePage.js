import React, { useEffect, useState } from "react";
import axios from "axios";
import "./StudentManagePage.css"; // ✅ CSS 파일 불러오기

const StudentManagePage = () => {
    const [students, setStudents] = useState([]);
    const [openCourses, setOpenCourses] = useState({}); // ✅ 어떤 강좌가 열려 있는지 상태 관리
    const professorStudentId = localStorage.getItem('studentId');

    useEffect(() => {
        axios.get("http://localhost:8090/api/student-manage/approved", {
            params: { professorStudentId } 
        })
        .then(response => setStudents(response.data))
        .catch(error => console.error("Error fetching students:", error));
    }, []);
    

    // ✅ 강좌 클릭 시 열림/닫힘 토글
    const toggleCourse = (courseName) => {
        setOpenCourses(prevState => ({
            ...prevState,
            [courseName]: !prevState[courseName]
        }));
    };

    // ✅ 강좌별 그룹화 (강좌명 기준으로 학생 분류)
    const groupedStudents = students.reduce((acc, student) => {
        if (!acc[student.courseName]) acc[student.courseName] = [];
        acc[student.courseName].push(student);
        return acc;
    }, {});

    return (
        <div className="student-manage-container">
            <h2>수강생 관리</h2>
            <div className="course-list">
                {Object.keys(groupedStudents).map((courseName, index) => (
                    <div key={index} className="course-item">
                        {/* ✅ 강좌명 클릭 시 학생 리스트 열림/닫힘 */}
                        <div 
                            className="course-header" 
                            onClick={() => toggleCourse(courseName)}
                        >
                            {courseName}
                            <span className={`arrow ${openCourses[courseName] ? "open" : ""}`}>&#9662;</span>
                        </div>
                        {openCourses[courseName] && (
                            <div className="student-list">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>학번</th>
                                            <th>학생 이름</th>
                                            <th>출석률</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {groupedStudents[courseName].map((student, idx) => (
                                            <tr key={idx}>
                                                <td>{student.studentId}</td>
                                                <td>{student.studentName}</td>
                                                <td>{student.attendanceRate}%</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
};

export default StudentManagePage;