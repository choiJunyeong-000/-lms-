import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./PostEdit.css";  // ✅ CSS 파일 추가

export function PostEdit() {
    const { postId, boardId } = useParams();
    const navigate = useNavigate();
    const token = localStorage.getItem("token");

    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [memberId, setMemberId] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!token) {
            alert("🚨 로그인 정보가 없습니다.");
            navigate("/login");
            return;
        }

        if (isNaN(postId) || isNaN(boardId)) {
            alert("🚨 잘못된 접근입니다!");
            navigate("/");
            return;
        }

        fetchUserData();
        fetchPostDetail();
    }, [token, postId, boardId]);

    const fetchUserData = async () => {
        try {
            const response = await axios.get("http://localhost:8090/api/users/me", {
                headers: { Authorization: `Bearer ${token}` },
            });
            setMemberId(response.data.id);
        } catch (error) {
            console.error("❌ 사용자 정보 조회 실패:", error);
        }
    };

    const fetchPostDetail = async () => {
        try {
            const response = await axios.get(`http://localhost:8090/api/weekly-post/${postId}/${boardId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });

            setTitle(response.data.post.title);
            setContent(response.data.post.content);
        } catch (error) {
            console.error("❌ 게시글 상세 조회 실패:", error);
            alert("게시글을 불러오는 데 실패했습니다.");
            navigate(-1);
        } finally {
            setLoading(false);
        }
    };

    const handleUpdate = async () => {
        if (!title.trim() || !content.trim()) {
            alert("🚨 제목과 내용을 입력해주세요!");
            return;
        }
        if (!boardId) {
            alert("🚨 게시판 정보를 불러오지 못했습니다!");
            return;
        }

        try {
            

            await axios.put(`http://localhost:8090/api/weekly-post/${postId}/${boardId}/edit`, {
                title,
                content,
                memberId,
            }, {
                headers: { Authorization: `Bearer ${token}` },
            });

            alert("✅ 게시글이 수정되었습니다!");
            navigate(`/api/weekly-post/${postId}/${boardId}`);
        } catch (error) {
            console.error("❌ 게시글 수정 실패:", error);
            alert("게시글 수정 중 오류가 발생했습니다.");
        }
    };

    if (loading) return <div>⏳ 로딩 중...</div>;

    return (
        <div className="edit-container">
            <h2>📝 게시글 수정</h2>
            <input
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="제목"
            />
            <textarea
                value={content}
                onChange={(e) => setContent(e.target.value)}
                placeholder="내용"
            />
            <div className="button-group">
                <button className="update-button" onClick={handleUpdate}>수정 완료</button>
                <button className="cancel-button" onClick={() => navigate(-1)}>취소</button>
            </div>
        </div>
    );
}

export default PostEdit;
