import { useState, useEffect } from "react";
import axios from "axios";

function KocwCoursePage() {
  const [videoList, setVideoList] = useState([]);
  const [videoProgress, setVideoProgress] = useState({});
  const [attendanceStatus, setAttendanceStatus] = useState({});

  // 토큰을 가져오는 함수
  const getAuthToken = () => {
    const token = localStorage.getItem("token");
    return token ? `Bearer ${token}` : null;
  };

  // 현재 로그인한 사용자의 ID를 가져오는 함수
  const getCurrentUserId = () => {
    const token = localStorage.getItem("token");
    if (!token) return null;
  
    try {
      const payload = JSON.parse(atob(token.split(".")[1])); // JWT payload 디코딩
      return payload.sub; // `sub`을 학생 ID로 사용
    } catch (error) {
      console.error("Error extracting userId from token:", error);
      return null;
    }
  };

  // 비디오 목록을 가져오는 함수
  const fetchVideoList = async () => {
    try {
      const token = getAuthToken();
      if (!token) {
        alert("로그인이 필요합니다.");
        return;
      }

      const response = await axios.get("http://localhost:8090/api/lectures/list", {
        headers: {
          Authorization: token,  // 인증 토큰 추가
        },
      });
      setVideoList(response.data);
    } catch (error) {
      console.error("비디오 목록 조회 실패", error);
      alert("비디오 목록을 불러올 수 없습니다.");
    }
  };

  // 진도율 상태를 가져오는 함수
  const fetchProgressStatus = async () => {
    try {
      const token = getAuthToken();
      const studentId = getCurrentUserId();
      if (!token || !studentId) {
        alert("로그인이 필요합니다.");
        return;
      }

      const response = await axios.get("http://localhost:8090/api/attendance/progress/status", {
        params: { studentId },
        headers: {
          Authorization: token,  // 인증 토큰 추가
        },
      });
      setVideoProgress(response.data);
    } catch (error) {
      console.error("진도율 상태 조회 실패", error);
      alert("진도율 상태를 불러올 수 없습니다.");
    }
  };

  // 출석 상태를 가져오는 함수
  const fetchAttendanceStatus = async () => {
    try {
      const studentId = getCurrentUserId();
      if (!studentId) {
        alert("로그인이 필요합니다.");
        return;
      }

      const token = getAuthToken();
      const response = await axios.get("http://localhost:8090/api/attendance/status", {
        params: { studentId },
        headers: {
          Authorization: token,  // 인증 토큰 추가
        },
      });

      // 응답 데이터가 배열이 아니라면 상태가 객체일 수 있으므로, 이를 처리
      if (response.data && typeof response.data === "object") {
        setAttendanceStatus(response.data);
      } else {
        console.error("출석 상태 데이터가 잘못되었습니다", response.data);
      }
    } catch (error) {
      console.error("출석 상태 조회 실패", error);
      alert("출석 상태를 불러올 수 없습니다.");
    }
  };

  // 시청하지 않은 부분으로 이동하려는 경우를 처리하는 함수
  const handleSeeking = (event, videoId) => {
    const currentTime = event.target.currentTime;
    const lastWatchedProgress = videoProgress[videoId] || 0;
    const duration = event.target.duration;
  
    // 출석이 완료된 강의라면 자유롭게 시청 가능
    if (attendanceStatus[videoId]) {
      return; // 출석이 완료되었으면 아무 제약 없이 시청할 수 있음
    }
  
    // 현재 시청한 진도율을 기준으로 이동을 제한 (진도율 이상으로 이동 못하게)
    const maxSeekableTime = (lastWatchedProgress / 100) * duration;
  
    if (currentTime > maxSeekableTime) {
      event.preventDefault();  // 기본 이동 동작을 방지
      event.target.currentTime = maxSeekableTime; // 시청한 부분으로 돌아가게 설정
      alert("시청하지 않은 부분으로 이동할 수 없습니다.");
    }
  };
  

  // 비디오의 시청 진행률을 업데이트하는 함수
  const handleTimeUpdate = async (event, videoId) => {
    const currentTime = event.target.currentTime;
    const duration = event.target.duration;
    const progress = (currentTime / duration) * 100;

    // 진도율 업데이트 (사용자가 시청한 부분만 저장)
    setVideoProgress((prevProgress) => ({
      ...prevProgress,
      [videoId]: progress,
    }));

    const studentId = getCurrentUserId();
    if (!studentId) {
      alert("회원 ID가 없습니다.");
      return;
    }

    try {
      const token = getAuthToken();
      if (!token) {
        alert("로그인이 필요합니다.");
        return;
      }

      // 현재 저장된 진도율을 가져오기
      const currentProgress = videoProgress[videoId] || 0;

      // 진도율이 변화했다면 업데이트
      if (progress > currentProgress) {
        // 진도율 저장을 위한 API 요청
        await axios.post(
          "http://localhost:8090/api/attendance/progress/video",
          {
            studentId: studentId,
            videoId: videoId,
            watchedPercentage: progress,
          },
          {
            headers: {
              Authorization: token,  // 인증 토큰 추가
              'Content-Type': 'application/json',
            },
          }
        );

        // 출석 처리 (진도율이 90% 이상이면 출석 처리)
        if (progress >= 90) {
          await handleAttendance(videoId, progress);  // 진도율을 그대로 출석 처리
        }
      }
    } catch (error) {
      console.error("진도율 업데이트 실패", error);
      if (error.response) {
        alert(error.response.data);
      } else {
        alert("진도율 업데이트 실패");
      }
    }
  };

  // 출석을 처리하는 함수
  const handleAttendance = async (videoId, progress) => {
    const studentId = getCurrentUserId();  // 로그인된 사용자의 ID 가져오기

    try {
      const token = getAuthToken();
      if (!token) {
        alert("로그인이 필요합니다.");
        return;
      }

      if (!studentId) {
        alert("회원 ID가 없습니다.");
        return;
      }

      // 출석 처리를 위한 API 요청
      const response = await axios.post(
        "http://localhost:8090/api/attendance/video",
        {
          studentId: studentId,  // 회원 ID 추가
          videoId: videoId,
          watchedPercentage: progress,  // 시청 진도율을 사용
        },
        {
          headers: {
            Authorization: token,  // 인증 토큰 추가
            'Content-Type': 'application/json',
          },
        }
      );

      // 출석 완료 알림을 한 번만 출력
      if (!attendanceStatus[videoId]) {
        alert(response.data);
      }

      setAttendanceStatus((prevStatus) => ({
        ...prevStatus,
        [videoId]: true,
      }));
    } catch (error) {
      console.error("출석 처리 실패", error);
      if (error.response) {
        alert(error.response.data);
      } else {
        alert("출석 처리가 실패했습니다.");
      }
    }
  };

  // 컴포넌트 마운트 시 비디오 목록과 출석 상태, 진도율 상태를 가져옵니다.
  useEffect(() => {
    fetchVideoList();
    fetchProgressStatus();
    fetchAttendanceStatus(); // 출석 상태도 함께 가져옵니다.
  }, []);

  return (
    <div>
      <h1>강의 비디오 업로드</h1>
      <h2>업로드된 비디오 목록</h2>
      <ul>
        {videoList.map((video) => (
          <li key={video.id}>
            <video
              width="300"
              controls
              // 진도율을 기준으로 초기 currentTime 설정
              onLoadedMetadata={(event) => {
                const savedProgress = videoProgress[video.id] || 0;
                const duration = event.target.duration;
                const initialTime = (savedProgress / 100) * duration;
                event.target.currentTime = initialTime; // 초기 시점 설정
              }}
              onTimeUpdate={(event) => handleTimeUpdate(event, video.id)}
              onSeeking={(event) => handleSeeking(event, video.id)} // 바를 강제로 움직일 때 처리
            >
              <source src={`http://localhost:8090/${video.videoUrl}`} />
              Your browser does not support the video tag.
            </video>
            <p>시청 진행률: {(videoProgress[video.id] || 0).toFixed(2)}%</p>
            <input
              type="checkbox"
              checked={attendanceStatus[video.id] || false}
              readOnly
            />
          </li>
        ))}
      </ul>
    </div>
  );
}

export default KocwCoursePage;
