import React, { useState, useEffect } from "react";
import axios from "axios";
import { useLocation } from "react-router-dom";

const ContentViewer = ({ token }) => {
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const fileUrl = queryParams.get("file");

  const [fileBlobUrl, setFileBlobUrl] = useState(null);

  useEffect(() => {
    const fetchFile = async () => {
      try {
        const response = await axios.get(fileUrl, {
          headers: { Authorization: `Bearer ${token}` },
          responseType: "blob", // 바이너리 데이터로 받기
        });

        const blob = new Blob([response.data], { type: response.headers["content-type"] });
        const blobUrl = URL.createObjectURL(blob);
        setFileBlobUrl(blobUrl);
      } catch (error) {
        console.error("파일 로딩 실패:", error);
      }
    };

    if (token && fileUrl) {
      fetchFile();
    }
  }, [token, fileUrl]);

  if (!fileBlobUrl) return <p>파일을 불러오는 중...</p>;

  return (
    <div className="content-viewer">
      <h2>강의 자료 보기</h2>
      <iframe src={fileBlobUrl} width="100%" height="800px" style={{ border: "none" }}></iframe>
    </div>
  );
};

export default ContentViewer;