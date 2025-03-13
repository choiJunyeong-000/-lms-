import { useState, useEffect } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";
import "./CourseQnA.css"; // ✅ CSS 파일 추가

export default function CourseQnA() {
  const { courseId } = useParams();
  const [qnaList, setQnaList] = useState([]);
  const [page, setPage] = useState(0);
  const [isWriting, setIsWriting] = useState(false);
  const [formData, setFormData] = useState({ title: "", content: "", secret: false });
  const [memberId, setMemberId] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchUserData();
  }, []);

  useEffect(() => {
    if (memberId) fetchQnAList();
  }, [courseId, page, memberId]);

  const fetchUserData = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      setError("로그인이 필요합니다.");
      return;
    }

    try {
      const response = await axios.get("http://localhost:8090/api/users/me", {
        headers: { Authorization: `Bearer ${token}` },
      });

      setMemberId(response.data.id);
    } catch (err) {
      console.error("사용자 정보 조회 실패:", err);
      setError("사용자 정보를 불러오는 데 실패했습니다.");
    }
  };

  const fetchQnAList = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8090/courses/${courseId}/qna?page=${page}&size=15`,
        {
          headers: memberId
            ? { Authorization: `Bearer ${localStorage.getItem("token")}` }
            : {},
        }
      );
      setQnaList(response.data.content || []);
    } catch (err) {
      console.error("QnA 목록 불러오기 오류:", err);
      setError("QnA 목록을 불러오는 데 실패했습니다.");
    }
  };

  const handleWrite = () => setIsWriting(true);
  const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });
  const handleCheckbox = () => setFormData({ ...formData, secret: !formData.secret });

  const handleSubmit = async () => {
    if (!memberId) {
      alert("로그인이 필요합니다.");
      return;
    }

    try {
      await axios.post(
        `http://localhost:8090/courses/${courseId}/qna`,
        formData,
        {
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${localStorage.getItem("token")}`,
            "memberId": memberId,
          },
        }
      );
      setIsWriting(false);
      setFormData({ title: "", content: "", secret: false });
      setPage(0);
      fetchQnAList();
    } catch (error) {
      console.error("QnA 등록 실패:", error);
      alert("QnA 등록에 실패했습니다.");
    }
  };

  return (
    <div className="course-qna-container">
      {error && <p style={{ color: "red" }}>{error}</p>}

      {isWriting ? (
        <div className="qna-form">
          <h1>❓ QnA 작성</h1>
          <input name="title" placeholder="제목" value={formData.title} onChange={handleChange} />
          <textarea name="content" placeholder="내용" value={formData.content} onChange={handleChange} />
          <label>
          </label>
          <button onClick={handleSubmit}>등록</button>
        </div>
      ) : (
        <div>
                    <h1 className="QnA-list">❓ QnA 목록</h1>
          <table className="qna-table">
            <thead>
              <tr>
                <th>번호</th>
                <th>제목</th>
                <th>작성자</th>
                <th>작성일</th>
              </tr>
            </thead>
            <tbody>
              {qnaList.length > 0 ? (
                qnaList.map((qna, index) => (
                  <tr key={qna.id}>
                    <td>{index + 1 + page * 15}</td>
                    <td>
                      <Link to={`/courses/${courseId}/qna/${qna.id}`}>
                        {qna.title || "제목 없음"}
                      </Link>
                    </td>
                    <td>{qna.author || "알 수 없음"}</td>
                    <td>{qna.createdAt ? new Date(qna.createdAt).toLocaleDateString() : "-"}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="4" style={{ textAlign: "center" }}>QnA가 없습니다.</td>
                </tr>
              )}
            </tbody>
          </table>

          <div className="pagination">
            <button onClick={() => setPage((p) => Math.max(p - 1, 0))} disabled={page === 0}>이전</button>
            <button onClick={handleWrite} className="qna-write-button" disabled={!memberId}>QnA 작성</button>
            <button onClick={() => setPage((p) => p + 1)}>다음</button>
          </div>
        </div>
      )}
    </div>
  );
  
}