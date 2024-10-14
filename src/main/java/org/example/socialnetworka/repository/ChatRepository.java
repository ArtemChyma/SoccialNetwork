package org.example.socialnetworka.repository;

import org.example.socialnetworka.chats.Chat;
import org.example.socialnetworka.users.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat findChatByName(String mane);
    Chat findChatById(Long id);
}
