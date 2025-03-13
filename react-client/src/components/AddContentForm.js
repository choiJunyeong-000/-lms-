import { useState, useEffect } from "react";
import axios from "axios";

const AddContentForm = ({ courseId, weekNumber, token }) => {
  const [videoFile, setVideoFile] = useState(null);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [memberId, setMemberId] = useState(null);
  const [contentList, setContentList] = useState([]);

  // 🔹 사용자 정보 가져오기 (JWT 토큰 사용)
  useEffect(() => {
    if (token) {
      const fetchUserData = async () => {
        try {
          const userResponse = await axios.get("http://localhost:8090/api/users/me", {
            headers: { Authorization: `Bearer ${token}` },
          });
          setMemberId(userResponse.data.id);
        } catch (error) {
          console.error("사용자 정보 조회 실패:", error);
          setErrorMessage("사용자 정보를 가져오는 데 실패했습니다.");
        }
      };

      fetchUserData();
    }
  }, [token]);

  // 🔹 콘텐츠 목록 불러오기
  const fetchContentList = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8090/api/courses/${courseId}/weeks/contents`, // ✅ API URL 수정
        { headers: { Authorization: `Bearer ${token}` } }
      );

      const weekData = response.data[weekNumber] || [];
      setContentList(weekData);
    } catch (error) {
      console.error("콘텐츠 목록 조회 실패:", error);
      setErrorMessage("콘텐츠를 불러오는 데 실패했습니다.");
    }
  };

  // 📌 초기 렌더링 및 업로드 후 목록 갱신
  useEffect(() => {
    fetchContentList();
  }, [courseId, weekNumber, token]);

  // 🔹 비디오 업로드 함수
  const handleVideoUpload = async (e) => {
    e.preventDefault();
    setErrorMessage("");
    setSuccessMessage("");

    if (!videoFile) {
      setErrorMessage("업로드할 비디오 파일을 선택해주세요.");
      return;
    }

    if (!memberId) {
      setErrorMessage("로그인 정보가 없습니다.");
      return;
    }

    const formData = new FormData();
    formData.append("file", videoFile);
    formData.append("memberId", memberId); // ✅ memberId 추가

    try {
      const url = `http://localhost:8090/api/courses/${courseId}/weeks/${weekNumber}/content`;
      await axios.post(url, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
          Authorization: `Bearer ${token}`,
        },
      });

      setSuccessMessage("✅ 비디오 업로드 성공!");
      setVideoFile(null);
      fetchContentList();
    } catch (error) {
      console.error("❌ 업로드 실패:", error.response?.data);
      setErrorMessage(error.response?.data?.message || "업로드 중 오류 발생.");
    }
  };

  // 🔹 콘텐츠 삭제 함수
  const handleDeleteContent = async (contentId) => {
    if (!contentId) {
      setErrorMessage("삭제할 콘텐츠 정보가 올바르지 않습니다.");
      return;
    }
  
    if (!memberId) {
      setErrorMessage("로그인 정보가 없습니다.");
      return;
    }
  
    if (!window.confirm("정말로 삭제하시겠습니까?")) return;
  
    try {
      await axios.delete(`http://localhost:8090/api/courses/${courseId}/weeks/${weekNumber}/content/${contentId}?memberId=${memberId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
  
      setSuccessMessage("✅ 콘텐츠가 삭제되었습니다.");
      fetchContentList(); // 삭제 후 목록 갱신
    } catch (error) {
      console.error("❌ 삭제 실패:", error.response?.data);
      setErrorMessage(error.response?.data?.message || "삭제 중 오류 발생.");
    }
  };
  

  return (
    <div>
      <h3>{weekNumber}주차 비디오 업로드</h3>
      {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
      {successMessage && <p style={{ color: "green" }}>{successMessage}</p>}

      {/* 🔹 비디오 업로드 폼 */}
      <form onSubmit={handleVideoUpload}>
        <input type="file" accept="video/*" onChange={(e) => setVideoFile(e.target.files[0])} />
        <button type="submit">비디오 업로드</button>
      </form>

      {/* 🔹 콘텐츠 목록 표시 */}
      <h4>업로드된 콘텐츠</h4>
      {contentList.length === 0 ? (
        <p>등록된 콘텐츠가 없습니다.</p>
      ) : (
        <ul>
          {contentList.map((content, index) => {
           
            return (
              <li key={index}>
                <a href={content.filePath} target="_blank" rel="noopener noreferrer">
                  {content.fileName}
                </a>{" "}
                <button
                  onClick={() => {
                    if (!content.id) {
                      console.error("삭제할 콘텐츠의 ID가 없습니다.", content);
                      setErrorMessage("삭제할 콘텐츠 정보가 올바르지 않습니다.");
                      return;
                    }
                    handleDeleteContent(content.id);
                  }}
                  className="btn-delete"
                >
                  삭제
                </button>
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
};

export default AddContentForm;
