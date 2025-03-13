package com.lms.project.LMS.Service;

import com.lms.project.LMS.DTO.WeeklyPostRequest;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Entity.WeeklyBoard;
import com.lms.project.LMS.Entity.WeeklyPost;
import com.lms.project.LMS.Repository.WeeklyPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WeeklyPostService {
    private final WeeklyPostRepository postRepository;

    public WeeklyPostService(WeeklyPostRepository postRepository) {
        this.postRepository = postRepository;
    }
    public Optional<WeeklyPost> findById(Long postId) {
        return postRepository.findById(postId);  // 리포지토리의 findById 메소드 호출
    }
    /**
     * ✅ 특정 게시판의 게시글 목록 조회
     */
    public List<WeeklyPost> getPostsByBoard(Long boardId) {
        return postRepository.findByWeeklyBoardId(boardId);
    }

    /**
     * ✅ 게시글 작성
     */
    public WeeklyPost createPost(WeeklyPostRequest request, Member member, WeeklyBoard board) {
        WeeklyPost post = new WeeklyPost();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setMember(member);
        post.setWeeklyBoard(board);
        post.setCreatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }
    
    @Transactional //수정 (본인만 가능)
    public WeeklyPost updatePost(Long postId, WeeklyPostRequest request, Long memberId) {
        WeeklyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 게시글입니다."));

        if (!post.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("❌ 수정 권한이 없습니다.");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        return postRepository.save(post);
    }

    // 게시글 삭제 (본인만 가능)
    @Transactional
    public void deletePost(Long postId, Long memberId) {
        WeeklyPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 게시글입니다."));

        if (!post.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("❌ 삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }
}