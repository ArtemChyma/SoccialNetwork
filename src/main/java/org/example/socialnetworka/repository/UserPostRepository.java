package org.example.socialnetworka.repository;

import org.example.socialnetworka.users.UserPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPostRepository extends JpaRepository<UserPost, Long> {

    List<UserPost> findAllByPosterId(Long posterId);
}
