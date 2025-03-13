import React, { useState, useEffect, useMemo } from 'react';
import { io } from 'socket.io-client';
import axios from 'axios';
import { v4 as uuidv4 } from 'uuid';
import './MessagePage.css';

const socket = io('http://localhost:5000');

const MessagePage = () => {
  const [member, setMember] = useState(null);
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState('');
  const [recipient, setRecipient] = useState(null);
  const [members, setMembers] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');

  // 로그인한 사용자 및 전체 멤버 조회
  useEffect(() => {
    async function fetchMemberData() {
      try {
        const token = localStorage.getItem("token");
        const resMe = await axios.get('http://localhost:8090/api/users/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setMember(resMe.data);

        const resMembers = await axios.get('http://localhost:8090/api/chat-members', {
          headers: { Authorization: `Bearer ${token}` }
        });
        // 본인 제외한 멤버 목록
        const otherMembers = resMembers.data.filter(m => m.studentId !== resMe.data.studentId);
        setMembers(otherMembers);
      } catch (error) {
        console.error("🚨 멤버 정보를 불러오는 데 실패했습니다.", error);
      }
    }
    fetchMemberData();
  }, []);

  // 전체 메시지 조회
  useEffect(() => {
    async function fetchMessages() {
      if (!member) return;
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(`http://localhost:8090/api/chat/messages/${member.studentId}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        console.log("서버에서 받은 메시지:", response.data);
        setMessages(response.data);
      } catch (error) {
        console.error("🚨 채팅 내역을 불러오지 못했습니다.", error);
      }
    }
    fetchMessages();
  }, [member]);
  
  // 실시간 메시지 수신
  useEffect(() => {
    const handleMessageReceive = (msg) => {
      setMessages(prev => {
        if (prev.some(m => m.id === msg.id)) {
          console.warn("⚠️ 중복 메시지 감지 - 추가 안 함", msg);
          return prev;
        }
        return [...prev, msg];
      });
    };

    socket.on('chat message', handleMessageReceive);
    return () => socket.off('chat message', handleMessageReceive);
  }, []);

  // 실시간 읽음 이벤트 수신 (송신자 측)
  useEffect(() => {
    const handleReadMessage = ({ messageId, readerId }) => {
      console.log("읽음 이벤트 수신:", messageId, readerId);
      setMessages(prev =>
        prev.map(m => String(m.id) === String(messageId) ? { ...m, read: true } : m)
      );
    };

    socket.on('read message', handleReadMessage);
    return () => socket.off('read message', handleReadMessage);
  }, []);

  // 메시지 전송 (엔터키 포함)
  const handleSend = async () => {
    const trimmedText = inputText.trim();
    if (!trimmedText || !recipient || !member) return;
    const newMsg = {
      sender: member.studentId,
      recipient: recipient.studentId,
      content: trimmedText
    };

    try {
      const token = localStorage.getItem("token");
      const response = await axios.post("http://localhost:8090/api/chat/send", newMsg, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setMessages(prev => [...prev, { ...response.data, id: response.data.id || uuidv4() }]);
      socket.emit('chat message', response.data);
      setInputText('');
    } catch (error) {
      console.error("🚨 메시지 전송 실패", error);
    }
  };

  // 선택된 대화 상대와 관련된 메시지만 필터링
  const conversationMessages = useMemo(() => {
    if (!member || !recipient) return [];
    return messages.filter(msg => {
      return (
        (msg.sender === member.studentId && msg.recipient === recipient.studentId) ||
        (msg.sender === recipient.studentId && msg.recipient === member.studentId)
      );
    });
  }, [messages, member, recipient]);

  // 수신자 입장에서, 메시지를 읽었을 때 처리하는 함수
  const markMessageAsRead = (msg) => {
    if (!member || !recipient) return;
    const token = localStorage.getItem("token");
    axios.post(`http://localhost:8090/api/chat/messages/read/${msg.id}`, null, {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(() => {
      setMessages(prev =>
        prev.map(m => m.id === msg.id ? { ...m, read: true } : m)
      );
      socket.emit('read message', { messageId: msg.id, readerId: member.studentId });
    })
    .catch(error => console.error("읽음 처리 실패", error));
  };

  // 대화창 열릴 때 자동으로 읽음 처리
  useEffect(() => {
    if (!member || !recipient) return;
    conversationMessages.forEach(msg => {
      if (msg.sender !== member.studentId && !msg.read) {
        markMessageAsRead(msg);
      }
    });
  }, [member, recipient, conversationMessages]);

  // 멤버 목록 정렬 및 검색 필터링 (카톡 스타일)
  const sortedMembers = useMemo(() => {
    let filtered = members.filter(m =>
      m.name.toLowerCase().includes(searchQuery.toLowerCase())
    );
    if (member) {
      filtered.sort((a, b) => {
        const conversationA = messages.filter(msg =>
          (msg.sender === member.studentId && msg.recipient === a.studentId) ||
          (msg.sender === a.studentId && msg.recipient === member.studentId)
        );
        const conversationB = messages.filter(msg =>
          (msg.sender === member.studentId && msg.recipient === b.studentId) ||
          (msg.sender === b.studentId && msg.recipient === member.studentId)
        );
        const lastTimeA = conversationA.reduce((acc, cur) => {
          const curTime = cur.timestamp ? new Date(cur.timestamp).getTime() : 0;
          return curTime > acc ? curTime : acc;
        }, 0);
        const lastTimeB = conversationB.reduce((acc, cur) => {
          const curTime = cur.timestamp ? new Date(cur.timestamp).getTime() : 0;
          return curTime > acc ? curTime : acc;
        }, 0);
        if(lastTimeA === lastTimeB){
          return a.name.localeCompare(b.name);
        }
        return lastTimeB - lastTimeA;
      });
    } else {
      filtered.sort((a, b) => a.name.localeCompare(b.name));
    }
    return filtered;
  }, [members, searchQuery, messages, member]);

  return (
    <div className="message-page-container">
      <div className="chat-container">
        {/* 좌측: 연락처 목록 */}
        <div className="contact-list">
          <h3>쪽지함</h3>
          {/* 검색 입력창 */}
          <input
            type="text"
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            placeholder="검색"
            className="contact-search"
          />
          {sortedMembers.map(m => {
            // 각 멤버별 읽지 않은 메시지 개수 계산 (내가 수신자이고 아직 읽지 않은 메시지)
            const unreadCount = member
              ? messages.filter(msg =>
                  msg.sender === m.studentId &&
                  msg.recipient === member.studentId &&
                  !msg.read
                ).length
              : 0;
            return (
              <div 
                key={m.studentId}
                className={`contact-item ${recipient && recipient.studentId === m.studentId ? 'selected' : ''}`}
                onClick={() => setRecipient(m)}
              >
                <div className="contact-name">
                  {m.name}
                  {unreadCount > 0 && <span className="unread-badge">{unreadCount}</span>}
                </div>
              </div>
            );
          })}
        </div>
        {/* 우측: 채팅 영역 */}
        <div className="chat-area">
          <div className="chat-header">
            {member && recipient ? `${member.name}과 ${recipient.name}의 대화` : "대화를 선택하세요"}
          </div>
          <div className="chat-messages">
            {conversationMessages.map(msg => (
              <div key={msg.id || uuidv4()} className={`chat-message ${msg.sender === member?.studentId ? 'sent' : 'received'}`}>
                {msg.sender === member?.studentId && (
                  <div className="read-status">
                    {msg.read ? '읽음' : '안읽음'}
                  </div>
                )}
                <div className="message-content">
                  <span>{msg.content}</span>
                </div>
                <div className="timestamp">
                  {msg.timestamp ? new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : ''}
                </div>
              </div>
            ))}
          </div>
          <div className="chat-input">
            <input
              type="text"
              value={inputText}
              onChange={e => setInputText(e.target.value)}
              onKeyDown={e => {
                if (e.key === 'Enter') {
                  e.preventDefault();
                  handleSend();
                }
              }}
              placeholder="메시지를 입력하세요..."
            />
            <button onClick={handleSend}>전송</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MessagePage;
