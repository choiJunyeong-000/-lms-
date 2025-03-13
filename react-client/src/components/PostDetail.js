import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./PostDetail.css";

export function PostDetail() {
    const { postId, boardId } = useParams();
    const [post, setPost] = useState(null);
    const [memberId, setMemberId] = useState(null);
    const token = localStorage.getItem("token");
    const navigate = useNavigate();

    useEffect(() => {
        if (token) {
            fetchUserData();
            fetchPostDetail();
        }
    }, [token, postId, boardId]);

    //  현재 로그인한 사용자 정보 조회
    const fetchUserData = async () => {
        try {
            const response = await axios.get("http://localhost:8090/api/users/me", {
                headers: { Authorization: `Bearer ${token}` },
            });
            setMemberId(response.data.id);
        } catch (error) {
            console.error("사용자 정보 조회 실패:", error);
        }
    };

    //  게시글 상세 조회
    const fetchPostDetail = async () => {
        try {
            const response = await axios.get(`http://localhost:8090/api/weekly-post/${postId}/${boardId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setPost(response.data.post);
        } catch (error) {
            console.error("게시글 상세 조회 실패:", error);
            alert("게시글을 불러오는 데 실패했습니다.");
        }
    };

    //  수정 페이지로 이동 (경로 수정됨!)
    const handleEdit = () => {
        navigate(`/api/weekly-post/${postId}/${boardId}/edit`);  // 🔥 기존 경로 오류 수정
    };

    //  게시글 삭제 요청
    const handleDelete = async () => {
        if (window.confirm("정말 삭제하시겠습니까?")) {
            try {
                await axios.delete(`http://localhost:8090/api/weekly-post/${postId}?memberId=${memberId}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                alert("게시글이 삭제되었습니다.");
                navigate(`/board/${boardId}`);
            } catch (error) {
                alert(error.response?.data || "게시글 삭제에 실패했습니다.");
            }
        }
    };

    if (!post) return <div>로딩 중...</div>;

    return (
        <div className="post-detail-wrapper">
            <h1 className="post-detail-title">{post.title}</h1>
            <div className="post-detail-info">
                <span><strong>작성자:</strong> {post.memberName}</span> &nbsp;
                <span className="post-detail-date">
                    <strong>작성일:</strong> {post.createdAt ? new Date(post.createdAt).toLocaleDateString() : "알 수 없음"}
                </span>
            </div>
            <p className="post-detail-content">{post.content}</p>

            {/* 현재 로그인한 사용자와 게시글 작성자가 같으면 수정/삭제 UI 표시 */}
            {memberId === post.memberId && (
                <div className="post-detail-buttons">
                    <button className="post-edit-button" onClick={handleEdit}>수정</button>
                    <button className="post-delete-button" onClick={handleDelete}>삭제</button>
                </div>
            )}

<button className="post-back-button" onClick={() => navigate(`/api/weekly-post/board/${boardId}`)}>
  목록
</button>

        </div>
    );
}

export default PostDetail;
