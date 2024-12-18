package org.example.socialnetworka.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usersPosts")
@Getter
@Setter
public class UserPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long posterId;
    private String posterUsername;
    private String postContent;
    private String ImagePath;
    private String postingDate;
//    private String usersPostPageId;
    public UserPost(){}

    public UserPost(Long posterId, String posterUsername, String postContent, String ImagePath) {
        this.posterId = posterId;
        this.posterUsername = posterUsername;
        this.postContent = postContent;
        this.ImagePath = ImagePath;
    }
}
