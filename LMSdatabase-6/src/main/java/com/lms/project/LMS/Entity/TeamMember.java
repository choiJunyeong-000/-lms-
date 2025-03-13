package com.lms.project.LMS.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.project.LMS.Enum.Role; // ✅ Role Enum 임포트

@Entity
@Getter
@Setter
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    @JsonIgnore
    private Team team;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    
    

    @Enumerated(EnumType.STRING) // ✅ Enum을 String으로 저장하도록 설정
    @Column(nullable = false)
    private Role role;

    private LocalDateTime joinedAt = LocalDateTime.now();
}
