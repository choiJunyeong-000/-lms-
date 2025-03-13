package com.lms.project.LMS.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.project.LMS.Entity.WeeklyPost;

import java.util.List;

/**
 * 게시글 레포지토리
 */
public interface WeeklyPostRepository extends JpaRepository<WeeklyPost, Long> {
    List<WeeklyPost> findByWeeklyBoardId(Long boardId);
}
