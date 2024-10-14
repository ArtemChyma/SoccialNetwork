package org.example.socialnetworka.repository;

import org.example.socialnetworka.users.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersChatsRepository extends JpaRepository<UserChat, Long> {
    List<UserChat> findAllByUserId(Long userId);
}
