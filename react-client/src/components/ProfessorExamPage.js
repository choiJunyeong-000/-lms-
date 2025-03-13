import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./ProfessorExamPage.css";

const ProfessorExamPage = () => {
  const [courses, setCourses] = useState([]);
  const [selectedCourseId, setSelectedCourseId] = useState("");
  const [exams, setExams] = useState([]);
  const [newExam, setNewExam] = useState({ title: "", description: "", date: "", total_points: "", examType: "중간고사" });
  const [editingExam, setEditingExam] = useState(null); 
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [professorId, setProfessorId] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchProfessorAndCourses = async () => {
      try {
        const token = localStorage.getItem("token");
        if (!token) {
          setError("로그인이 필요합니다.");
          return;
        }

        const userResponse = await axios.get("http://localhost:8090/api/users/me", {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (!userResponse.data || !userResponse.data.id) {
          setError("사용자 정보를 가져올 수 없습니다.");
          return;
        }

        setProfessorId(userResponse.data.id);

        const coursesResponse = await axios.get("http://localhost:8090/api/courses/professor", {
          headers: { Authorization: `Bearer ${token}` },
        });

        setCourses(coursesResponse.data);
        if (coursesResponse.data.length > 0) {
          setSelectedCourseId(coursesResponse.data[0].id);
        }
      } catch (error) {
        console.error("사용자 정보 또는 강의 목록 가져오기 실패:", error);
        setError("사용자 정보를 불러오는 중 오류가 발생했습니다.");
      }
    };

    fetchProfessorAndCourses();
  }, []);

  const fetchExams = async () => {
    if (!selectedCourseId) return;

    try {
      setLoading(true);
      const token = localStorage.getItem("token");

      const response = await axios.get(`http://localhost:8090/api/exams/select?courseId=${selectedCourseId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (Array.isArray(response.data)) {
        setExams(response.data);
      } else {
        console.error("❌ 예상치 못한 응답 형식:", response.data);
        setExams([]);
      }
    } catch (error) {
      console.error("❌ 시험 목록 불러오기 실패:", error.response ? error.response.data : error.message);
      setExams([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchExams();
  }, [selectedCourseId]);

  const handleAddExam = async () => {
    if (!selectedCourseId) {
      alert("시험을 추가할 강의를 선택하세요.");
      return;
    }

    if (!professorId) {
      alert("로그인된 교수의 정보를 불러올 수 없습니다.");
      return;
    }

    try {
      const token = localStorage.getItem("token");

      const response = await axios.post(
        `http://localhost:8090/api/exams/${professorId}/${selectedCourseId}`,
        {
          title: newExam.title,
          description: newExam.description,
          examDate: newExam.date,
          totalPoints: newExam.total_points ? parseFloat(newExam.total_points) : 0,
          courseId: selectedCourseId,
          examType: newExam.examType,
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      alert("시험이 성공적으로 생성되었습니다!");

      await fetchExams();

      setNewExam({ title: "", description: "", date: "", total_points: "", examType: "중간고사" });
    } catch (error) {
      console.error("❌ 시험 추가 실패:", error.response?.data || error.message);
      alert("시험 추가에 실패했습니다. 다시 시도해주세요.");
    }
  };

  const handleEditExam = async () => {
    if (!editingExam) return;

    try {
      const token = localStorage.getItem("token");

      await axios.put(
        `http://localhost:8090/api/exams/${editingExam.id}`,
        {
          title: editingExam.title,
          description: editingExam.description,
          totalPoints: editingExam.totalPoints ? parseFloat(editingExam.totalPoints) : 0,
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      alert("시험이 성공적으로 수정되었습니다!");
      setEditingExam(null);
      await fetchExams();
    } catch (error) {
      console.error("❌ 시험 수정 실패:", error.response?.data || error.message);
      alert("시험 수정에 실패했습니다.");
    }
  };

  const handleDeleteExam = async (examId) => {
    if (!window.confirm("이 시험을 삭제하시겠습니까?")) return;

    try {
      const token = localStorage.getItem("token");

      await axios.delete(`http://localhost:8090/api/exams/${examId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      alert("시험이 성공적으로 삭제되었습니다!");
      fetchExams();  // 최신 시험 목록 다시 불러오기
    } catch (error) {
      console.error("❌ 시험 삭제 실패:", error.response?.data || error.message);
      alert("시험 삭제에 실패했습니다.");
    }
  };

  return (
    <div className="container">
      <h2 className="title">시험 관리</h2>

      <label className="label">강의 선택:</label>
      <select className="select" value={selectedCourseId} onChange={(e) => setSelectedCourseId(e.target.value)}>
        <option value="">강의를 선택하세요</option>
        {courses.map((course) => (
          <option key={course.id} value={course.id}>
            {course.name}
          </option>
        ))}
      </select>

      {loading ? (
        <p>로딩 중...</p>
      ) : Array.isArray(exams) && exams.length === 0 ? (
        <p className="no-exam-message">현재 등록된 시험이 없습니다. 새로운 시험을 추가하세요.</p>
      ) : (
        <ul className="exam-list">
          {exams.map((exam) => (
            <li key={exam.id} className="exam-item">
              <a href={`/professor/exams/${exam.id}/questions`} className="exam-link">
                {exam.title}
              </a>
              <button onClick={() => setEditingExam(exam)}>수정</button>
              <button onClick={() => handleDeleteExam(exam.id)}>삭제</button>
            </li>
          ))}
        </ul>
      )}

      {editingExam && (
        <div className="modal">
          <h3>시험 수정</h3>
          <input type="text" placeholder="시험 제목" value={editingExam.title} onChange={(e) => setEditingExam({ ...editingExam, title: e.target.value })} />
          <input type="text" placeholder="시험 설명" value={editingExam.description} onChange={(e) => setEditingExam({ ...editingExam, description: e.target.value })} />
          <input type="number" placeholder="총 점수" value={editingExam.totalPoints} onChange={(e) => setEditingExam({ ...editingExam, totalPoints: e.target.value })} />
          <button onClick={handleEditExam}>저장</button>
          <button onClick={() => setEditingExam(null)}>취소</button>
        </div>
      )}

      <div className="form">
        <h3>시험 추가</h3>
        <input type="text" placeholder="시험 제목" value={newExam.title} onChange={(e) => setNewExam({ ...newExam, title: e.target.value })} />
        <input type="text" placeholder="시험 설명" value={newExam.description} onChange={(e) => setNewExam({ ...newExam, description: e.target.value })} />
        <input type="number" placeholder="총 점수" value={newExam.total_points} onChange={(e) => setNewExam({ ...newExam, total_points: e.target.value })} />
        <input type="date" value={newExam.date} onChange={(e) => setNewExam({ ...newExam, date: e.target.value })} />

        <div className="exam-type">
          <label>
            중간 고사
            <input type="radio" value="중간고사" checked={newExam.examType === "중간고사"} onChange={(e) => setNewExam({ ...newExam, examType: e.target.value })} />
          </label>
          <label>
            기말 고사
            <input type="radio" value="기말고사" checked={newExam.examType === "기말고사"} onChange={(e) => setNewExam({ ...newExam, examType: e.target.value })} />
          </label>
        </div>

        <button className="add-exam-btn" onClick={handleAddExam}>
          시험 추가
        </button>
      </div>
    </div>
  );
};

export default ProfessorExamPage;