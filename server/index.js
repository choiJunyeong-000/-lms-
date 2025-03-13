const express = require('express');
const cors = require('cors');
const app = express();
const http = require('http').createServer(app);
const io = require('socket.io')(http, {
  cors: {
    origin: "http://localhost:3000", // 클라이언트 도메인 (개발 중엔 3000번 포트)
    methods: ["GET", "POST"]
  }
});

app.use(cors());

// Socket.IO 연결 처리
io.on('connection', (socket) => {
  console.log('새로운 클라이언트 접속:', socket.id);

  // 1) 채팅 메시지 전송
  socket.on('chat message', (msg) => {
    console.log('메시지 받음:', msg);
    // 모든 클라이언트에게 브로드캐스트
    io.emit('chat message', msg);
  });

  // 2) 메시지 읽음 처리
  socket.on('read message', (readInfo) => {
    // readInfo: { messageId: xxx, readerId: yyy }
    console.log('메시지 읽음 처리 요청:', readInfo);

    // 모든 클라이언트에게 브로드캐스트
    // - 실제로는 특정 방(room) 내 클라이언트에게만 emit할 수도 있음
    io.emit('read message', readInfo);
  });

  socket.on('disconnect', () => {
    console.log('클라이언트 연결 해제:', socket.id);
  });
});

// 서버 구동
const PORT = 5000;
http.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
