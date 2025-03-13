import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import './UserList.css'; // ✅ CSS 파일 추가

function UserList() {
  const [users, setUsers] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('STUDENT'); // 현재 선택된 카테고리 상태
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get('http://localhost:8090/api/users', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setUsers(response.data);
      } catch (error) {
        console.error('Error fetching users:', error);
      }
    };

    fetchUsers();
  }, []);

  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const category = searchParams.get('category');
    if (category) {
      setSelectedCategory(category);
    }
  }, [location.search]);

  const handleDelete = async (userId) => {
    if (window.confirm('삭제하시겠습니까?')) {
      try {
        const token = localStorage.getItem('token');
        await axios.delete(`http://localhost:8090/api/users/${userId}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        setUsers(users.filter(user => user.id !== userId));
        alert('사용자가 삭제되었습니다.');
      } catch (error) {
        console.error('Error deleting user:', error);
        alert('사용자 삭제에 실패했습니다. 다시 시도해주세요.');
      }
    }
  };

  const renderTable = (users) => (
    <table className="user-list-table">
      <thead>
        <tr>
          <th>이름</th>
          <th>학번</th>
          <th>이메일</th>
          <th>역할</th>
          <th>수정</th>
          <th>삭제</th>
        </tr>
      </thead>
      <tbody>
        {users.map((user) => (
          <tr key={user.id}>
            <td>{user.name}</td>
            <td>{user.studentId}</td>
            <td>{user.email}</td>
            <td>{user.role}</td>
            <td className="button-cell">
              <button className="edit-button" onClick={() => navigate(`/edit/${user.id}?category=${selectedCategory}`)}>수정</button>
            </td>
            <td className="button-cell">
              <button className="delete-button" onClick={() => handleDelete(user.id)}>삭제</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );

  const students = users.filter(user => user.role === 'STUDENT');
  const professors = users.filter(user => user.role === 'PROFESSOR');
  const admins = users.filter(user => user.role === 'ROLE_ADMIN');

  return (
    <div className="user-list-container">
      <div className="category-buttons">
        <button onClick={() => setSelectedCategory('STUDENT')}>학생</button>
        <button onClick={() => setSelectedCategory('PROFESSOR')}>교수</button>
        <button onClick={() => setSelectedCategory('ROLE_ADMIN')}>관리자</button>
      </div>
      {selectedCategory === 'STUDENT' && (
        <>
          <h3>학생</h3>
          {renderTable(students)}
        </>
      )}
      {selectedCategory === 'PROFESSOR' && (
        <>
          <h3>교수</h3>
          {renderTable(professors)}
        </>
      )}
      {selectedCategory === 'ROLE_ADMIN' && (
        <>
          <h3>관리자</h3>
          {renderTable(admins)}
        </>
      )}
    </div>
  );
}

export default UserList;