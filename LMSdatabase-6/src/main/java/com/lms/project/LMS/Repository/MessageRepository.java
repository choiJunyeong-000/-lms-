package com.lms.project.LMS.Repository;

import com.lms.project.LMS.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRecipientAndIsReadFalse(String recipient); // 안 읽은 메시지 조회
    List<Message> findByRecipientOrSender(String recipient, String sender); // 전체 메시지 조회
}
