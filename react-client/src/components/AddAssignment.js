import { useState, useEffect } from "react";
import axios from "axios";

const AddAssignment = ({ courseId, weekNumber, onAssignmentAdded, token }) => {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [startDate, setStartDate] = useState("");
  const [startTime, setStartTime] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [dueTime, setDueTime] = useState("");
  const [points, setPoints] = useState(100);
  const [isActive, setIsActive] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");
  const [memberId, setMemberId] = useState(null);

  // 🔹 JWT 토큰을 이용해 memberId 가져오기
  useEffect(() => {
    if (token) {
      const fetchUserData = async () => {
        try {
          const response = await axios.get("http://localhost:8090/api/users/me", {
            headers: { Authorization: `Bearer ${token}` },
          });
          setMemberId(response.data.id); // 사용자 ID 설정
        } catch (error) {
          console.error("❌ 사용자 정보 조회 실패:", error);
          setErrorMessage("사용자 정보를 가져오는 데 실패했습니다.");
        }
      };

      fetchUserData();
    }
  }, [token]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!memberId) {
      setErrorMessage("로그인 정보가 필요합니다.");
      return;
    }

    const formattedStartDate = startDate && startTime ? `${startDate}T${startTime}:00` : null;
    const formattedDueDate = dueDate && dueTime ? `${dueDate}T${dueTime}:00` : null;

    try {
      const response = await axios.post(
        `http://localhost:8090/api/courses/${courseId}/weeks/${weekNumber}/assignments`,
        { 
          title, 
          description, 
          startDate: formattedStartDate, 
          dueDate: formattedDueDate, 
          points, 
          isActive
        },
        {
          headers: { 
            Authorization: `Bearer ${token}`, 
            memberId: memberId  // 🔥 백엔드가 요구하는 memberId 추가!
          }
        }
      );

      alert("✅ 과제가 성공적으로 추가되었습니다!");
 

      
    } catch (error) {
      console.error("❌ 과제 추가 실패:", error);
      setErrorMessage("과제를 추가하는 데 실패했습니다.");
    }
  };

  return (
    <div className="assignment-form">
      <h2>과제 추가</h2>
      {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
      <form onSubmit={handleSubmit}>
        <label>과제 제목</label>
        <input type="text" value={title} onChange={(e) => setTitle(e.target.value)} required />

        <label>과제 설명</label>
        <textarea value={description} onChange={(e) => setDescription(e.target.value)} required />

        <label>제출 가능 시작일</label>
        <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} required />
        <input type="time" value={startTime} onChange={(e) => setStartTime(e.target.value)} required />

        <label>마감일</label>
        <input type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} required />
        <input type="time" value={dueTime} onChange={(e) => setDueTime(e.target.value)} required />

        <label>배점 (기본값: 100점)</label>
        <input type="number" value={points} onChange={(e) => setPoints(Number(e.target.value))} min="1" required />

        <button type="submit" disabled={!memberId}>과제 추가</button>
      </form>
    </div>
  );
};

export default AddAssignment;