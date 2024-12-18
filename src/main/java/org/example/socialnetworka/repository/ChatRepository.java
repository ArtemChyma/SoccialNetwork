package org.example.socialnetworka.repository;

import org.example.socialnetworka.chats.Chat;
import org.example.socialnetworka.users.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat findChatByName(String name);
    Chat findChatById(Long id);
    void deleteChatById(Long id);
}
