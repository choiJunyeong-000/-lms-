import React, { useState } from "react";
import axios from "axios";
import "./UserForm.css";

function UserForm() {
  const [user, setUser] = useState({
    name: "",
    studentId: "",
    password: "",
    role: "STUDENT",
    birthDate: "",
    email: "",
  });

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");

    axios
      .post("http://localhost:8090/api/users", user, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((response) => {
        alert("회원이 등록되었습니다.");
      })
      .catch((error) => {
        console.error("회원 등록 실패:", error);
        alert("회원 등록에 실패했습니다.");
      });
  };

  return (
    <form className="user-form" onSubmit={handleSubmit}>
      <h2 className="user-title">📌 회원 추가</h2>

      <div className="user-field">
        <label>
          <span className="user-icon">👤</span> 이름:
        </label>
        <input type="text" name="name" value={user.name} onChange={handleChange} required />
      </div>

      <div className="user-field">
        <label>
          <span className="user-icon">🆔</span> 학번:
        </label>
        <input type="text" name="studentId" value={user.studentId} onChange={handleChange} required />
      </div>

      <div className="user-field">
        <label>
          <span className="user-icon">🔒</span> 비밀번호:
        </label>
        <input type="password" name="password" value={user.password} onChange={handleChange} required />
      </div>

      <div className="user-field">
        <label>
          <span className="user-icon">🎓</span> 역할:
        </label>
        <select name="role" value={user.role} onChange={handleChange}>
          <option value="STUDENT">학생</option>
          <option value="PROFESSOR">교수</option>
        </select>
      </div>

      <div className="user-field">
        <label>
          <span className="user-icon">📅</span> 생년월일:
        </label>
        <input type="date" name="birthDate" value={user.birthDate} onChange={handleChange} />
      </div>

      <div className="user-field">
        <label>
          <span className="user-icon">📧</span> 이메일:
        </label>
        <input type="email" name="email" value={user.email} onChange={handleChange} />
      </div>

      <button type="submit" className="user-submit-btn">회원 등록</button>
    </form>
  );
}

export default UserForm;
