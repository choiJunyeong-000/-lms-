package com.lms.project.LMS.DTO;
import lombok.Getter;
@Getter
public class WeeklyPostRequest {
    private Long id;
    private String title;
    private String content;
    private String memberName;
    private Long memberId;  // memberId 추가
    private String createdAt;
    private Long boardId; 

    // 기본 생성자
    public WeeklyPostRequest() {
    }

    // 생성자 추가: id, title, content, memberName, memberId, createdAt을 인자로 받는 생성자
    public WeeklyPostRequest(Long id, String title, String content, String memberName, Long memberId, String createdAt, Long boardId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.memberName = memberName;
        this.memberId = memberId;
        this.createdAt = createdAt;
        this.boardId = boardId;
    }

 
}