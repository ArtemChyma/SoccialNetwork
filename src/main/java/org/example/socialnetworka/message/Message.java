package org.example.socialnetworka.message;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private Long senderId;
    private String messageContent;

    public Message(Long chatId, Long senderId, String messageContent) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.messageContent = messageContent;
    }

}
