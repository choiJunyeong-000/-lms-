import { useRef,useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import "./VideoDetailPage.css";
const VideoDetailPage = ({ videoUrl, week }) => {
    const videoRef = useRef(null);
    const [isChecked, setIsChecked] = useState(false);
    const [lastSavedProgress, setLastSavedProgress] = useState(0);
    const progressSaveInterval = 1; // 1초마다 진도율 저장
    const ATTENDANCE_THRESHOLD = 90; // 출석 인정 기준 시청률 (90%)
    const [content, setContent] = useState(null);
    const token = localStorage.getItem('token');
    const studentId = localStorage.getItem('studentId');
    if (studentId) {
      console.log("studentId: ", studentId);
    } else {
      console.log("studentId가 localStorage에 없습니다.");
    }

    const courseId = localStorage.getItem('courseId');
    if (courseId) {
      console.log("courseId: ", studentId);
    } else {
      console.log("courseId가 localStorage에 없습니다.");
    }
    useEffect(() => {
        // videoContent를 sessionStorage에서 가져오기
        const savedContent = sessionStorage.getItem("videoContent");
        if (savedContent) {
          setContent(JSON.parse(savedContent));
        }
      }, []);
    useEffect(() => {
      // 초기 출석 상태 확인
      const checkAttendanceStatus = async () => {
        try {
          const response = await axios.get(
            `http://localhost:8090/api/attendance/status?studentId=${studentId}`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          
          // courseId가 키인 맵에서 출석 상태를 확인
          if (response.data[courseId] === true) {
            setIsChecked(true);
          }
        } catch (error) {
          console.error("출석 상태 확인 실패", error);
        }
      };
    
      
      // 이전 시청 진도율 확인
      const checkProgressStatus = async () => {
        try {
          const response = await axios.get(
            `http://localhost:8090/api/attendance/progress/status?studentId=${studentId}`,
            { headers: { Authorization: `Bearer ${token}` } }
          );
          
          // courseId가 키인 맵에서 진도율 확인
          if (response.data[courseId]) {
            setLastSavedProgress(response.data[courseId]);
            
            // 동영상 시간 설정 (비디오가 로드된 후에 실행)
            if (videoRef.current) {
              const duration = videoRef.current.duration;
              if (!isNaN(duration)) {
                videoRef.current.currentTime = (response.data[courseId] / 100) * duration;
              }
            }
          }
        } catch (error) {
          console.error("진도율 확인 실패", error);
        }
      };
      
      checkAttendanceStatus();
      checkProgressStatus();
    }, [studentId, courseId, token]);
  
    // 비디오 메타데이터 로드 완료 후 진도율에 따라 영상 위치 설정
    const handleLoadedMetadata = () => {
      if (videoRef.current && lastSavedProgress > 0) {
        const duration = videoRef.current.duration;
        videoRef.current.currentTime = (lastSavedProgress / 100) * duration;
      }
    };
    useEffect(() => {
        const savedContent = sessionStorage.getItem("videoContent");
        if (savedContent) {
          setContent(JSON.parse(savedContent));
        }
      }, []);
    
    // 주기적으로 진도율 저장 (10초마다)
    useEffect(() => {
      const intervalId = setInterval(() => {
        saveCurrentProgress();
      }, progressSaveInterval * 100);
      
      return () => clearInterval(intervalId);
    }, []);
    
    // 현재 진도율 저장
    const saveCurrentProgress = async () => {
      if (!videoRef.current) return;
      
      const video = videoRef.current;
      const watchedPercentage = (video.currentTime / video.duration) * 100;
      
      // 이미 출석이 완료되었거나 진도율이 0인 경우 저장하지 않음
      if (isChecked || watchedPercentage === 0) return;
      
      try {
        console.log("진도율 저장: ", watchedPercentage);
        
        setLastSavedProgress(watchedPercentage);
      } catch (error) {
        console.error("진도율 저장 실패", error);
      }
    };
    
    // 영상 시청 중 처리
    const handleTimeUpdate = async () => {
      if (!videoRef.current) return;
      
      const video = videoRef.current;
      const watchedPercentage = (video.currentTime / video.duration) * 100;
    
      if (watchedPercentage >= ATTENDANCE_THRESHOLD && !isChecked) {
        if (!content || !content.id) {
          console.error("contentId가 존재하지 않습니다.");
          return;
        }
    
        try {
          const response = await axios.post(
            "http://localhost:8090/api/attendance/course",
            { 
              studentId, 
              courseId, 
              contentId: content.id,
              watchedPercentage 
            },
            { headers: { Authorization: `Bearer ${token}` } }
          );
    
          if (response.data === "출석 완료") {
            setIsChecked(true);
          }
        } catch (error) {
          console.error("강의 출석 체크 실패", error);
        }
      }
    };
    
    if (!content) {
        return <p>로딩 중...</p>;
      }
    return (
        console.log(studentId),
<div className="video-player-container">
  <video
    ref={videoRef}
    controls
    autoPlay
    onTimeUpdate={handleTimeUpdate}
    onLoadedMetadata={handleLoadedMetadata}
    className="main-video"
  >
    <source src={content?.filePath} />
    브라우저가 비디오 태그를 지원하지 않습니다.
  </video>
</div>

        
      );
  };

export default VideoDetailPage;
