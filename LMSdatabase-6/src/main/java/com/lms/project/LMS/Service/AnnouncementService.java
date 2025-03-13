package com.lms.project.LMS.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.project.LMS.DTO.AnnouncementDto;
import com.lms.project.LMS.Entity.Announcement;
import com.lms.project.LMS.Entity.Course;
import com.lms.project.LMS.Entity.Member;
import com.lms.project.LMS.Repository.AnnouncementRepository;
import com.lms.project.LMS.Repository.CourseRepository;
import com.lms.project.LMS.Repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository, 
                               MemberRepository memberRepository, 
                               CourseRepository courseRepository) {
        this.announcementRepository = announcementRepository;
        this.memberRepository = memberRepository;
        this.courseRepository = courseRepository;
    }

    /** ✅ 공지사항 생성 (교수 또는 관리자만 가능) */
    public Announcement createAnnouncement(Long memberId, Announcement announcement) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        // 🔥 교수(PROFESSOR) 또는 관리자(ADMIN)만 공지사항 생성 가능
        if (!(member.getRole().equals("PROFESSOR") || member.getRole().equals("ADMIN"))) {
            throw new IllegalArgumentException("Only professors or admins can create announcements.");
        }

        announcement.setCreatedBy(member);
        return announcementRepository.save(announcement);
    }

    /** ✅ 특정 강의 & 주차의 공지사항 조회 */
    public List<Announcement> getAnnouncementsByCourseAndWeek(Long courseId, int weekNumber) {
        return announcementRepository.findByCourseIdAndWeekWeekNumber(courseId, weekNumber);
    }

    /** ✅ 모든 공지사항 조회 */
    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    /** ✅ 특정 강의의 공지사항 조회 (DTO 변환 포함) */
    @Transactional(readOnly = true)
    public List<AnnouncementDto> getAllByCourse(Long courseId) {
        List<Announcement> announcements = announcementRepository.findByCourseId(courseId);

        return announcements.stream()
                .map(this::convertToDto) // DTO 변환
                .collect(Collectors.toList());
    }


    /** ✅ 모든 공지사항을 DTO로 변환하여 조회 */
    public List<AnnouncementDto> getAllDTOAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /** ✅ 특정 공지사항 조회 (findById 추가) */
    public Optional<Announcement> findById(Long announcementId) {
        return announcementRepository.findById(announcementId);
    }

    /** ✅ 공지사항 삭제 (본인이 작성한 것만 가능) */
    @Transactional
    public void deleteAnnouncement(Long courseId, Long announcementId, Long memberId) {
        Announcement announcement = findById(announcementId)
                .orElseThrow(() -> new RuntimeException("해당 공지사항이 존재하지 않습니다."));

        // 🔥 본인이 작성한 공지만 삭제 가능
        if (!announcement.getCreatedBy().getId().equals(memberId)) {
            throw new RuntimeException("본인이 작성한 공지사항만 삭제할 수 있습니다.");
        }

        announcementRepository.deleteById(announcementId);
    }
    
    // 공지사항 수정 (본인이 작성한 것만 가능)
    @Transactional
    public AnnouncementDto updateAnnouncement(Long courseId, Long announcementId, Long memberId, AnnouncementDto request) {
        // ✅ 공지사항 찾기
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        // ✅ 작성자 확인
        if (!announcement.getCreatedBy().getId().equals(memberId)) {
            throw new RuntimeException("공지사항을 수정할 권한이 없습니다.");
        }

        // ✅ 내용 업데이트
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());

        // ✅ 저장 후 DTO 반환
        announcementRepository.save(announcement);
        return new AnnouncementDto(announcement);
    }

    /** ✅ 특정 작성자가 만든 공지사항 조회 */
    public List<Announcement> getAnnouncementsByCreatedById(Long createdById) {
        return announcementRepository.findByCreatedById(createdById);
    }

    /** ✅ 제목으로 공지사항 검색 */
    public List<Announcement> getAnnouncementsByTitle(String title) {
        return announcementRepository.findByTitleContaining(title);
    }

    /** ✅ 공지사항 저장 */
    public Announcement saveAnnouncement(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    /** ✅ DTO 변환 메서드 */
    private AnnouncementDto convertToDto(Announcement announcement) {
        AnnouncementDto dto = new AnnouncementDto();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());

        // 작성자가 null이면 "알 수 없음" 처리
        dto.setAuthorName(announcement.getCreatedBy() != null 
                          ? announcement.getCreatedBy().getName() 
                          : "알 수 없음");

        dto.setCreatedAt(announcement.getCreatedAt());
        return dto;
    }
}