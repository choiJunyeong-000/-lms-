import React, { useState, useEffect, useMemo } from 'react';
import axios from 'axios';
import './BoardPage.css'; // ✅ 스타일 파일 가져오기


function BoardPage() {
    const [searchTerm, setSearchTerm] = useState('');
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const token = localStorage.getItem("token"); // ✅ JWT 토큰 가져오기

    // ✅ API에서 게시판 데이터 불러오기
    useEffect(() => {
        if (!token) {
            console.warn("⚠️ 토큰이 없습니다. 로그인이 필요합니다.");
            setError("로그인이 필요합니다.");
            setLoading(false);
            return;
        }

        const fetchBoardData = async () => {
            try {
                const response = await axios.get('/api/board', {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setPosts(response.data);
            } catch (error) {
                console.error("🚨 게시판 데이터 불러오기 오류:", error);
                setError("게시판 데이터를 불러오는 중 오류가 발생했습니다.");
            } finally {
                setLoading(false);
            }
        };

        fetchBoardData();
    }, [token]);

    // ✅ 검색 기능 직접 구현
    const filteredPosts = useMemo(() => {
        return posts.filter(post =>
            post.title.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }, [posts, searchTerm]);

    if (loading) return <div>📡 게시판 데이터 불러오는 중...</div>;

    return (
        <div className="dashboard">
            

            <main className="main-content">
                <h2>게시판</h2>

                {error ? (
                    <p className="error-message">{error}</p>
                ) : (
                    <>
                        {/* 🔹 게시글 검색 입력창 */}
                        <input
                            type="text"
                            placeholder="게시글 검색"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />

                        {/* 🔹 필터링된 게시글 목록 출력 */}
                        <table className="table">
                            <thead>
                                <tr>
                                    <th>글 번호</th>
                                    <th>제목</th>
                                    <th>작성자</th>
                                    <th>작성일</th>
                                </tr>
                            </thead>
                            <tbody>
                                {filteredPosts.length > 0 ? (
                                    filteredPosts.map((post) => (
                                        <tr key={post.id}>
                                            <td>{post.id}</td>
                                            <td>{post.title}</td>
                                            <td>{post.writer}</td>
                                            <td>{new Date(post.date).toLocaleString()}</td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="4">검색 결과가 없습니다.</td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </>
                )}
            </main>
        </div>
    );
}

export default BoardPage;
