package com.lms.project.LMS.controller;

import com.lms.project.LMS.Entity.Message;
import com.lms.project.LMS.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    // ✅ 메시지 저장 (쪽지 보내기)
    @PostMapping("/send")
    public Message sendMessage(@RequestBody Message message) {
        message.setTimestamp(java.time.LocalDateTime.now());
        message.setRead(false);
        
        Message savedMessage = messageRepository.save(message);
        System.out.println("✅ 저장된 메시지 ID: " + savedMessage.getId());
        return savedMessage;
    }


    // ✅ 특정 사용자의 전체 메시지 조회 (보낸 + 받은 메시지)
    @GetMapping("/messages/{studentId}")
    public List<Message> getMessages(@PathVariable String studentId) {
        return messageRepository.findByRecipientOrSender(studentId, studentId);
    }

    // ✅ 특정 사용자의 안 읽은 메시지 가져오기
    @GetMapping("/messages/unread/{recipient}")
    public List<Message> getUnreadMessages(@PathVariable String recipient) {
        return messageRepository.findByRecipientAndIsReadFalse(recipient);
    }

    // ✅ 메시지를 읽음 상태로 변경
    @PostMapping("/messages/read/{id}")
    public void markMessageAsRead(@PathVariable Long id) {
        messageRepository.findById(id).ifPresent(msg -> {
            msg.markAsRead();
            messageRepository.save(msg);
        });
    }
}
