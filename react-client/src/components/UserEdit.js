import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import './UserEdit.css';

function UserEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const [user, setUser] = useState({
    name: '',
    password: '',  // 비밀번호 필드는 유지하되, 수정 불가
    role: '',
    birthDate: '',
    email: ''
  });

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const token = localStorage.getItem("token"); // ✅ 토큰 포함
        const response = await axios.get(`http://localhost:8090/api/users/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setUser(response.data);
      } catch (error) {
        console.error("회원 정보를 불러오는 중 오류 발생:", error);
      }
    };
  
    fetchUser();
  }, [id]);
  

  const handleChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');


      // 비밀번호는 수정하지 않도록 user 객체에서 제거
      const { password, ...updatedUser } = user;

      await axios.put(`http://localhost:8090/api/users/${id}`, updatedUser, {
        headers: { Authorization: `Bearer ${token}` }
      });
      alert('회원 정보가 수정되었습니다.');
      const searchParams = new URLSearchParams(location.search);
      const category = searchParams.get('category');
      navigate(`/admin?category=${category}`); // ✅ 수정 후 원래 카테고리로 돌아가기
    } catch (error) {
      console.error('Error updating user:', error);
    }
  };

  return (
    <form className="user-form" onSubmit={handleSubmit}>
      <h2>회원 수정</h2>
      <label>
        이름:
        <input type="text" name="name" value={user.name} onChange={handleChange} required />
      </label>
      <label>
        {/* 비밀번호 입력 필드를 비활성화 */}
        비밀번호:
        <input type="password" name="password" value={user.password} onChange={handleChange} disabled required />
      </label>
      <label>
        역할:
        <select name="role" value={user.role} onChange={handleChange}>
          <option value="STUDENT">학생</option>
          <option value="PROFESSOR">교수</option>
        </select>
      </label>
      <label>
        생년월일:
        <input type="date" name="birthDate" value={user.birthDate} onChange={handleChange} />
      </label>
      <label>
        이메일:
        <input type="email" name="email" value={user.email} onChange={handleChange} />
      </label>
      <button type="submit">수정</button>
    </form>
  );
}

export default UserEdit;
