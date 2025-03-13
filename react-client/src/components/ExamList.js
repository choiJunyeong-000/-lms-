import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import './ExamList.css';

const ExamList = () => {
  const [exams, setExams] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const courseId = localStorage.getItem("courseId");
  useEffect(() => {
    const fetchExams = async () => {
      try {
        const token = localStorage.getItem("token"); // ✅ 저장된 토큰 가져오기
        if (!token) {
          console.error("❌ 토큰이 없습니다.");
          return;
        }

        const response = await axios.get(
          `http://localhost:8090/api/exams/select?courseId=${courseId}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );

        console.log("✅ API 응답 데이터:", response.data);
        setExams(response.data || []);
      } catch (error) {
        console.error("❌ axios 요청 오류:", error.response?.data || error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchExams();
  }, []);

  const handleExamSelect = (examId) => {
    navigate(`/exam/${examId}`);
  };

  if (loading) {
    return <p>로딩 중...</p>;
  }

  return (
    <div>
      <h1>시험 목록</h1>
      <ul>
        {exams.map(exam => (
          <li key={exam.id}>
            <button onClick={() => handleExamSelect(exam.id)}>
              {exam.title}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ExamList;
