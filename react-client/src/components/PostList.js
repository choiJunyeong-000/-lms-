import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./PostList.css";

export function PostList() {
    const { boardId } = useParams();
    const navigate = useNavigate();
    const [posts, setPosts] = useState([]);
    const [memberId, setMemberId] = useState(null);
    const token = localStorage.getItem("token");

    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const response = await axios.get(`http://localhost:8090/api/weekly-post/board/${boardId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (response.data && Array.isArray(response.data.posts)) {
                    const simplifiedPosts = response.data.posts.map(post => ({
                        id: post.id,
                        title: post.title,
                        memberName: post.memberName,
                        createdAt: post.createdAt,
                    }));
                    setPosts(simplifiedPosts);
                } else {
                    setPosts([]);
                }
            } catch (error) {
                console.error("게시글 조회 실패:", error);
                setPosts([]);
            }
        };

        fetchPosts();
    }, [boardId, token]);
    const fetchUserData = async () => {
        try {
            const response = await axios.get("http://localhost:8090/api/users/me", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setMemberId(response.data.id);
        } catch (error) {
            console.error("사용자 정보 조회 실패:", error);
        }
    };

    useEffect(() => {
        if (token) {
            fetchUserData();
        }
    }, [token]);

    const handlePostClick = (postId, boardId) => {
        navigate(`/api/weekly-post/${postId}/${boardId}`);
    };

    return (
        <div className="post-container">
            <h1 className="post-title">📝 게시글 목록</h1>
            <table className="post-table">
                <thead>
                    <tr className="post-header">
                        <th className="post-col">번호</th>
                        <th className="post-col">제목</th>
                        <th className="post-col">작성자</th>
                        <th className="post-col">작성일</th>
                    </tr>
                </thead>
                <tbody>
                    {Array.isArray(posts) && posts.length > 0 ? (
                        posts.map((post, index) => (
                            <tr key={post.id} className="post-row">
                                <td className="post-cell">{index + 1}</td>
                                <td className="post-cell post-link" onClick={() => handlePostClick(post.id, boardId)}>
                                    {post.title}
                                </td>
                                <td className="post-cell">{post.memberName}</td>
                                <td className="post-cell">{new Date(post.createdAt).toLocaleDateString()}</td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="4" className="post-empty">게시글이 없습니다.</td>
                        </tr>
                    )}
                </tbody>
            </table>

            {memberId && (
                <div className="post-action">
                    <button className="post-button" onClick={() => navigate(`/weekly-post/create/${boardId}`, { state: { memberId } })}>
                        글쓰기
                    </button>
                </div>
            )}
        </div>
    );
}

export default PostList;