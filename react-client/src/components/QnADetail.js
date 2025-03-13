import { useEffect, useState } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import "./QnADetail.css";

const QnADetail = () => {
  const { courseId, qnaId } = useParams();
  const [qna, setQna] = useState(null);
  const [answers, setAnswers] = useState([]);
  const [answerContent, setAnswerContent] = useState("");
  const [editingAnswerId, setEditingAnswerId] = useState(null);
  const [editedContent, setEditedContent] = useState("");
  const [memberId, setMemberId] = useState(null);
  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchData = async () => {
      await fetchUserData();
      await fetchQnADetail();
      await fetchAnswers();
    };
    fetchData();
  }, [courseId, qnaId]);

  const fetchUserData = async () => {
    try {
      const response = await axios.get("http://localhost:8090/api/users/me", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setMemberId(response.data.id);
    } catch (error) {
      console.error("사용자 정보 조회 실패:", error);
    }
  };

  const fetchQnADetail = async () => {
    try {
      const response = await axios.get(`http://localhost:8090/courses/${courseId}/qna/${qnaId}`);
      setQna(response.data);
    } catch (error) {
      console.error("QnA 정보를 불러올 수 없습니다.", error.response || error);
    }
  };

  const fetchAnswers = async () => {
    try {
      const response = await axios.get(`http://localhost:8090/courses/${courseId}/qna/${qnaId}/answers`);
      setAnswers(response.data);
    } catch (error) {
      console.error("답변을 불러올 수 없습니다.", error.response || error);
    }
  };

  const handleAnswerSubmit = async () => {
    if (!answerContent.trim()) {
      alert("답변 내용을 입력해주세요.");
      return;
    }
  
    try {
      const response = await axios.post(
        `http://localhost:8090/courses/${courseId}/qna/${qnaId}/answers`,
        { content: answerContent }, // ✅ 변경: content만 객체로 감싸기
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
            memberId: memberId,
          },
        }
      );
  
      setAnswers([...answers, response.data]);
      setAnswerContent("");
    } catch (error) {
      console.error("답변 등록 실패:", error.response || error);
    }
  };

  const handleDeleteAnswer = async (answerId) => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;

    try {
      await axios.delete(`http://localhost:8090/courses/${courseId}/qna/${qnaId}/answers/${answerId}`, {
        headers: { 
          Authorization: `Bearer ${token}`,
          memberId: memberId, // 🔹 헤더에 memberId 추가
        },
      });
      setAnswers(answers.filter((answer) => answer.id !== answerId));
    } catch (error) {
      console.error("삭제 실패:", error.response || error);
    }
  };

  const handleEditAnswer = (answerId, content) => {
    setEditingAnswerId(answerId);
    setEditedContent(content);
  };

  const handleUpdateAnswer = async (answerId) => {
    if (!editedContent.trim()) {
      alert("수정할 내용을 입력해주세요.");
      return;
    }

    try {
      await axios.put(
        `http://localhost:8090/courses/${courseId}/qna/${qnaId}/answers/${answerId}`,
        { content: editedContent }, // 요청 본문에서 memberId 제거
        { 
          headers: { 
            Authorization: `Bearer ${token}`, 
            "Content-Type": "application/json",
            memberId: memberId, // 🔹 헤더에 memberId 추가
          } 
        }
      );

      setAnswers(
        answers.map((answer) => (answer.id === answerId ? { ...answer, content: editedContent } : answer))
      );
      setEditingAnswerId(null);
    } catch (error) {
      console.error("수정 실패:", error.response || error);
    }
  };

  if (!qna) return <p>로딩 중...</p>;

  return (
    <div className="qna-detail">
      <h2>{qna.title}</h2>
      <p><strong>작성자:</strong> {qna.author || "알 수 없음"}</p>
      <p><strong>작성일:</strong> {new Date(qna.createdAt).toLocaleString()}</p>
      <hr />
      <p>{qna.content}</p>

      <h3>답변</h3>
      {answers.length > 0 ? (
        answers.map((answer) => (
          <div key={answer.id} className="answer">
            {editingAnswerId === answer.id ? (
              <div>
                <textarea value={editedContent} onChange={(e) => setEditedContent(e.target.value)} />
                <button onClick={() => handleUpdateAnswer(answer.id)} className="save-btn">저장</button>
                <button onClick={() => setEditingAnswerId(null)} className="cancel-btn">취소</button>
              </div>
            ) : (
              <div>
                <p><strong>{answer.authorName || "알 수 없음"}</strong>: {answer.content}</p>
                {Number(answer.authorId) === Number(memberId) && (
                  <div className="answer-buttons">
                    <button onClick={() => handleEditAnswer(answer.id, answer.content)} className="edit-btn">수정</button>
                    <button onClick={() => handleDeleteAnswer(answer.id)} className="delete-btn">삭제</button>
                  </div>
                )}
              </div>
            )}
          </div>
        ))
      ) : (
        <p>아직 답변이 없습니다.</p>
      )}

      <textarea
        value={answerContent}
        onChange={(e) => setAnswerContent(e.target.value)}
        placeholder="답변을 입력하세요..."
        className="answer-input"
      />
      <button onClick={handleAnswerSubmit} className="submit-btn">답변 등록</button>
    </div>
  );
};

export default QnADetail;