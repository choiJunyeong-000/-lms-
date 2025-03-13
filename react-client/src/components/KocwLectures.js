import { useState, useEffect } from "react";
import axios from "axios";
import "./KocwLectures.css";

function KocwLectures() {
  const [videoList, setVideoList] = useState([]);
  const [courseName, setCourseName] = useState(""); // 강의명 상태
  const [courseDescription, setCourseDescription] = useState(""); // 강의 설명 상태

  const getAuthToken = () => {
    const token = localStorage.getItem("token");
    return token ? `Bearer ${token}` : null;
  };

  const handleVideoUpload = async (event) => {
    const file = event.target.files[0];
    if (!file || !courseName || !courseDescription) {
      alert("강의명과 설명을 입력하고 파일을 선택해주세요.");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);
    formData.append("courseName", courseName);
    formData.append("courseDescription", courseDescription);

    try {
      const token = getAuthToken();
      if (!token) {
        alert("로그인이 필요합니다.");
        return;
      }

      await axios.post("http://localhost:8090/api/lectures/upload-video", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
          Authorization: token,
        },
      });

      alert("비디오 업로드 성공!");
      fetchVideoList();
      setCourseName(""); // 강의명 초기화
      setCourseDescription(""); // 강의 설명 초기화
    } catch (error) {
      console.error("비디오 업로드 실패", error);
      alert("비디오 업로드 실패! 서버 에러 또는 네트워크 문제를 확인해주세요.");
    }
  };

  const fetchVideoList = async () => {
    try {
      const token = getAuthToken();
      if (!token) {
        alert("로그인이 필요합니다.");
        return;
      }

      const response = await axios.get("http://localhost:8090/api/lectures/list", {
        headers: {
          Authorization: token,
        },
      });
      setVideoList(response.data);
    } catch (error) {
      console.error("비디오 목록 조회 실패", error);
      alert("비디오 목록을 불러올 수 없습니다.");
    }
  };

  const handleDeleteVideo = async (videoId) => {
    try {
      const token = getAuthToken();
      if (!token) {
        alert("로그인이 필요합니다.");
        return;
      }

      await axios.delete(`http://localhost:8090/api/lectures/delete-video/${videoId}`, {
        headers: {
          Authorization: token,
        },
      });

      alert("비디오 삭제 성공!");
      setVideoList(videoList.filter((video) => video.id !== videoId));
    } catch (error) {
      console.error("비디오 삭제 실패", error);
      alert("비디오 삭제 실패!");
    }
  };

  useEffect(() => {
    fetchVideoList();
  }, []);

  return (
    <div className="kocw-container">
      <h1 className="kocw-title">강의 비디오 업로드</h1>

      {/* ✅ 입력 폼 */}
      <div className="kocw-form">
        <input
          type="text"
          className="kocw-input"
          placeholder="강의명을 입력하세요"
          value={courseName}
          onChange={(e) => setCourseName(e.target.value)}
        />
        <textarea
          className="kocw-textarea"
          placeholder="강의 설명을 입력하세요"
          value={courseDescription}
          onChange={(e) => setCourseDescription(e.target.value)}
        ></textarea>
        <input type="file" className="kocw-file-input" accept="video/*" onChange={handleVideoUpload} />
      </div>

      <h2 className="kocw-subtitle">업로드된 비디오 목록</h2>

      {/* ✅ 비디오 리스트 */}
      <ul className="kocw-video-list">
        {videoList.map((video, index) => (
          <li key={index} className="kocw-video-item">
            <video className="kocw-video" controls>
              <source src={`http://localhost:8090/${video.videoUrl}`} />
              Your browser does not support the video tag.
            </video>
            <button className="kocw-delete-btn" onClick={() => handleDeleteVideo(video.id)}>
              삭제
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default KocwLectures;
