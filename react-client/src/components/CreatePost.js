import { useState, useEffect } from "react";
import { useParams, useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import "./CreatePost.css";

export function CreatePost() {
    const { boardId } = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    const token = localStorage.getItem("token");
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [memberId, setMemberId] = useState(location.state?.memberId || null);

    const fetchUserData = async () => {
        try {
            const response = await axios.get("http://localhost:8090/api/users/me", {
                headers: { Authorization: `Bearer ${token}` },
                withCredentials: true,
            });
            setMemberId(response.data.id);
        } catch (error) {
            console.error("사용자 정보 조회 실패:", error);
            alert("사용자 정보 조회에 실패했습니다.");
        }
    };

    useEffect(() => {
        if (!memberId && token) {
            fetchUserData();
        }
    }, [token, memberId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!memberId) {
            alert("로그인이 필요합니다.");
            return;
        }

        const requestData = { title, content, memberId };
        console.log("게시글 작성 데이터:", requestData);

        try {
            const response = await axios.post(
                `http://localhost:8090/api/weekly-post/create/${boardId}`,
                requestData,
                {
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`,
                    },
                    withCredentials: true,
                }
            );
            if (response.status === 200) {
                alert("게시글이 등록되었습니다.");
                navigate(`/board/${boardId}`);
            } else {
                alert("게시글 등록에 실패했습니다.");
            }
        } catch (error) {
            console.error("게시글 등록 실패:", error.response ? error.response.data : error.message);
            alert("게시글 등록에 실패했습니다.");
        }
    };

    return (
        <div className="create-post-container">
            <h1 className="create-post-title">📝 게시글 작성</h1>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    className="create-post-input"
                    placeholder="제목"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    required
                />
                <textarea
                    className="create-post-textarea"
                    placeholder="내용"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    required
                />
                <div className="create-post-buttons">
                    <button
                        type="button"
                        className="create-post-cancel"
                        onClick={() => navigate(`/api/weekly-post/board/${boardId}`)}>
                        취소
                    </button>
                    <button type="submit" className="create-post-submit">
                        등록하기
                    </button>
                </div>
            </form>
        </div>
    );
}

export default CreatePost;
