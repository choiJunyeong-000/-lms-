package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 강의 주차 정보를 관리하는 엔티티 - 강의의 주차별 내용을 저장합니다.
 */
@Entity
@Table(name = "weeks", uniqueConstraints = {
    @UniqueConstraint(name = "unique_course_week", columnNames = {"course_id", "week_number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Week {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int weekNumber;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
