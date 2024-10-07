package org.example.socialnetworka.repository;

import org.example.socialnetworka.friends.FriendShip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendShipRepository extends JpaRepository<FriendShip, Long> {
    FriendShip findFriendShipByUserIdAndFriendId(Long userId, Long friendId);
    FriendShip findFriendShipByUserIdAndFriendIdAndStatus(Long userId, Long friendId, String Status);
    List<FriendShip> findAllByUserId(Long userId);
    List<FriendShip> findAllByFriendId(Long friendId);
    List<FriendShip> findAllByFriendIdAndStatus(Long friendId, String status);
    List<FriendShip> findAllByUserIdAndStatus(Long friendId, String status);
    List<FriendShip> findAllByUserIdAndFriendId(Long userId, Long friendId);
    List<FriendShip> findAllByFriendIdOrUserIdAndStatus(Long friendId, Long userId, String status);
}
