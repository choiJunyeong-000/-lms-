package com.lms.project.LMS.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lms.project.LMS.DTO.WeeklyPostRequest;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.WeeklyBoard;
import com.lms.project.LMS.Entity.WeeklyPost;
import com.lms.project.LMS.Service.MemberService;
import com.lms.project.LMS.Service.WeeklyBoardService;
import com.lms.project.LMS.Service.WeeklyPostService;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ✅ 주차별 게시글 컨트롤러
 */
@RestController
@RequestMapping("/api/weekly-post")
public class WeeklyPostController {
    private final WeeklyPostService postService;
    private final WeeklyBoardService boardService;
    private final MemberService memberService;

    public WeeklyPostController(WeeklyPostService postService, WeeklyBoardService boardService, MemberService memberService) {
        this.postService = postService;
        this.boardService = boardService;
        this.memberService = memberService;
    }

    /**
     * ✅ 특정 게시판의 게시글 목록 조회
     */
    @GetMapping("/board/{boardId}")
    public ResponseEntity<Map<String, Object>> getPostsByBoard(@PathVariable Long boardId) {
        try {
            List<WeeklyPost> posts = postService.getPostsByBoard(boardId);

            // WeeklyPostRequest로 변환하여 응답
            List<WeeklyPostRequest> postRequests = posts.stream()
                .map(post -> new WeeklyPostRequest(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getMember().getName(),  // memberName을 가져오기
                    post.getMember().getId(),    // memberId 추가
                    post.getCreatedAt().toString(), // 날짜 포맷을 String으로 변환
                    boardId
                ))
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("posts", postRequests);  // 'posts'라는 키로 리스트를 래핑
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();  // 예외 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("posts", List.of())); // 빈 리스트 반환
        }
    }

    /**
     * ✅ 게시글 작성
     */
    @PostMapping("/create/{boardId}")
    public ResponseEntity<?> createPost(@PathVariable Long boardId, @RequestBody WeeklyPostRequest request) {
        try {
            // ✅ memberId를 사용하여 멤버 조회
            Optional<Member> memberOptional = memberService.findById(request.getMemberId()); // memberId로 멤버 조회
            
            if (memberOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ 회원을 찾을 수 없습니다.");
            }

            Member member = memberOptional.get();

            // ✅ 게시판 조회
            WeeklyBoard board = boardService.findById(boardId)  // boardId는 PathVariable로 받아옴
                .orElseThrow(() -> new IllegalArgumentException("❌ 게시판을 찾을 수 없습니다."));

            // ✅ 게시글 생성
            WeeklyPost post = postService.createPost(request, member, board);
            return ResponseEntity.ok(post);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ 서버 오류가 발생했습니다.");
        }
    }
    
    @GetMapping("/{postId}/{boardId}")
    public ResponseEntity<Map<String, Object>> getPostDetail(
        @PathVariable Long postId,
        @PathVariable Long boardId  // boardId를 추가로 받습니다.
    ) {
        try {
            // 서비스에서 postId로 게시글 조회
            Optional<WeeklyPost> postOptional = postService.findById(postId);

            if (postOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "❌ 게시글을 찾을 수 없습니다."));
            }

            WeeklyPost post = postOptional.get();

            // WeeklyPost를 WeeklyPostRequest로 변환
            WeeklyPostRequest postRequest = new WeeklyPostRequest(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getMember().getName(),
                post.getMember().getId(),
                post.getCreatedAt().toString(),
                boardId  // boardId 추가
            );

            return ResponseEntity.ok(Map.of("post", postRequest));  // 게시글과 함께 boardId를 응답으로 반환
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "❌ 서버 오류가 발생했습니다."));
        }
    }
    
 // 게시글 수정 API
    @PutMapping("/{postId}/{boardId}/edit")
    public ResponseEntity<?> updatePost(
            @PathVariable Long postId,
            @PathVariable Long boardId,
            @RequestBody WeeklyPostRequest request) {

        try {
            Optional<Member> memberOptional = memberService.findById(request.getMemberId());
            if (memberOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ 사용자 정보를 찾을 수 없습니다.");
            }

            Optional<WeeklyPost> postOptional = postService.findById(postId);
            if (postOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ 게시글을 찾을 수 없습니다.");
            }

            WeeklyPost existingPost = postOptional.get();
            if (!existingPost.getWeeklyBoard().getId().equals(boardId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ 게시글이 해당 게시판에 존재하지 않습니다.");
            }

            WeeklyPost updatedPost = postService.updatePost(postId, request, request.getMemberId());

            WeeklyPostRequest postResponse = new WeeklyPostRequest(
                    updatedPost.getId(),
                    updatedPost.getTitle(),
                    updatedPost.getContent(),
                    updatedPost.getMember().getName(),
                    updatedPost.getMember().getId(),
                    updatedPost.getCreatedAt().toString(),
                    boardId
            );

            return ResponseEntity.ok(postResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

//  게시글 삭제 API
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, @RequestParam Long memberId) {
        try {
            postService.deletePost(postId, memberId);
            return ResponseEntity.ok().body("✅ 게시글이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}