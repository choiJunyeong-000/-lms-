import { useState } from "react";
import Cookies from "js-cookie";
import './styles/custom-livekit.css'; // 이 라인을 통해 CSS를 불러옵니다.

import {
  RemoteParticipant,
  RemoteTrack,
  RemoteTrackPublication,
  Room,
  RoomEvent
} from "livekit-client";
import { LiveKitRoom, VideoConference } from "@livekit/components-react";
import '@livekit/components-styles';
import './styles/custom-livekit.css';
import ScreenRecorder from './components/ScreenRecorder'; // 화면 녹화 컴포넌트

type TrackInfo = {
  trackPublication: RemoteTrackPublication;
  participantIdentity: string;
};

// 🔥 서버 및 LiveKit URL 설정 함수
const APPLICATION_SERVER_URL = window.location.hostname === "localhost"
  ? "http://localhost:8090/"
  : `https://${window.location.hostname}:6443/`;

const LIVEKIT_URL = window.location.hostname === "localhost"
  ? "ws://localhost:7880/"
  : `wss://${window.location.hostname}:7443/`;

// ✅ JWT 토큰에서 사용자 ID 추출
const extractStudentIdFromToken = (token?: string) => {
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split(".")[1])); // JWT 디코딩
    return payload.sub ?? null;
  } catch {
    return null;
  }
};

function App() {
  const token = Cookies.get("jwt"); // ✅ JWT 토큰 가져오기
  const initialParticipantName = extractStudentIdFromToken(token);
  
  const [participantName] = useState(initialParticipantName); // ✅ 참가자 이름 (불변)
  const [room, setRoom] = useState<Room | undefined>(undefined);
  const [remoteTracks, setRemoteTracks] = useState<TrackInfo[]>([]);
  const [roomName, setRoomName] = useState("");

  async function joinRoom() {
    const room = new Room();
    setRoom(room);
    document.title = roomName; // 🔥 제목 변경

    room.on(RoomEvent.TrackSubscribed, (_track, publication, participant) => {
      setRemoteTracks((prev) => [
        ...prev,
        { trackPublication: publication, participantIdentity: participant.identity }
      ]);
    });

    room.on(RoomEvent.TrackUnsubscribed, (_track, publication) => {
      setRemoteTracks((prev) => prev.filter((track) => track.trackPublication.trackSid !== publication.trackSid));
    });

    try {
      const token = await getToken(roomName, participantName);
      await room.connect(LIVEKIT_URL, token);
      try{
      await room.localParticipant.enableCameraAndMicrophone();
    } catch (deviceError) {
      console.warn("Camera or microphone not available.", deviceError);
      alert("카메라나 마이크가 연결되지 않았습니다. 연결 후 다시 시도해주세요.");
    }
    } catch (error) {
      console.error("Error connecting to the room:", error);
      await leaveRoom();
    }
  }

  async function leaveRoom() {
    await room?.disconnect();
    setRoom(undefined);
    setRemoteTracks([]);
    document.title = "화상강의"; // 🔥 방 나가면 제목 초기화
  }

  async function getToken(roomName: string, participantName: string) {
    const response = await fetch(`${APPLICATION_SERVER_URL}token`, {
      method: "POST",
      headers: { "Content-Type": "application/json", "Authorization": `Bearer ${token}` },
      body: JSON.stringify({ roomName, participantName })
    });

    if (!response.ok) throw new Error("토큰 가져오기 실패");
    
    const data = await response.json();
    return data.token;
  }

  return (
    <>
      {!room ? (
        <div id="join">
          <div id="join-dialog">
            <h2>화상강의 참여하기</h2>
            <form onSubmit={(e) => { joinRoom(); e.preventDefault(); }}>
              <div>
                <label htmlFor="participant-name">참가자</label>
                <input id="participant-name" className="form-control" type="text" value={participantName} readOnly required />
              </div>
              <div>
                <label htmlFor="room-name">방</label>
                <input id="room-name" className="form-control" type="text" value={roomName} onChange={(e) => setRoomName(e.target.value)} required />
              </div>
              <button className="btn btn-lg btn-success" type="submit" disabled={!roomName || !participantName}>
                참여하기!
              </button>
            </form>
          </div>
        </div>
      ) : (
        <LiveKitRoom          
        data-lk-theme="default"
        room={room} 
        connectOptions={{ adaptiveStream: true }} 
        identity={participantName} 
        data-roomname={roomName} 
        onDisconnected={leaveRoom}>
          <div id="room">
            <VideoConference />
            <ScreenRecorder />
          </div>
        </LiveKitRoom>
      )}
    </>
  );
}

export default App;
