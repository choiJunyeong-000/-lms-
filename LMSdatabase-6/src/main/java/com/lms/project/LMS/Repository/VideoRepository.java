package com.lms.project.LMS.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.project.LMS.Entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {
}