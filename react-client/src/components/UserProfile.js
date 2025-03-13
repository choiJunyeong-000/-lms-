import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./UserProfile.css"; // CSS 파일 추가

function UserProfile() {
  const navigate = useNavigate();
  const [user, setUser] = useState({ name: "", email: "", password: "", birthDate: "" });

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const token = localStorage.getItem("token");
        if (!token) throw new Error("No token found. Please log in again.");
        
        const response = await axios.get("http://localhost:8090/api/users/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUser({ name: response.data.name, email: response.data.email, password: "", birthDate: response.data.birthDate });
      } catch (error) {
        console.error("Error fetching user data:", error);
      }
    };

    fetchUserData();
  }, []);

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const handleUpdate = async (e) => {
    e.preventDefault();

    // 입력 값 검증
    if (!user.name || !user.email || !user.password || !user.birthDate) {
      alert("모든 필드를 채워주세요.");
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(user.email)) {
      alert("유효한 이메일 주소를 입력해주세요.");
      return;
    }

    if (user.password.length < 8) {
      alert("비밀번호는 8자 이상이어야 합니다.");
      return;
    }

    try {
      const token = localStorage.getItem("token");
      if (!token) throw new Error("No token found. Please log in again.");

      // 이메일 중복 여부 확인
      const emailCheckResponse = await axios.post("http://localhost:8090/api/users/check-email", { email: user.email }, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (emailCheckResponse.data) {
        alert("이미 사용 중인 이메일입니다. 다른 이메일을 입력해주세요.");
        return;
      }
      
      await axios.put("http://localhost:8090/api/users/update", user, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });
      alert("정보가 업데이트되었습니다.");
      navigate("/student");
    } catch (error) {
      console.error("Error updating user info:", error);
      alert("정보 업데이트에 실패했습니다. 다시 시도해주세요.");
    }
  };

  return (
    <div className="user-profile-container">
      <h2>회원정보 수정</h2>
      <form className="user-profile-form" onSubmit={handleUpdate}>
        <div className="form-group">
          <label htmlFor="name">이름:</label>
          <input type="text" id="name" name="name" value={user.name} onChange={handleChange} required />
        </div>
        <div className="form-group">
          <label htmlFor="email">이메일:</label>
          <input type="email" id="email" name="email" value={user.email} onChange={handleChange} required />
        </div>
        <div className="form-group">
          <label htmlFor="password">비밀번호:</label>
          <input type="password" id="password" name="password" value={user.password} onChange={handleChange} required />
        </div>
        <div className="form-group">
          <label htmlFor="birthDate">생년월일:</label>
          <input type="date" id="birthDate" name="birthDate" value={user.birthDate} onChange={handleChange} required />
        </div>
        <button type="submit" className="update-button">정보 수정</button>
      </form>
    </div>
  );
}

export default UserProfile;