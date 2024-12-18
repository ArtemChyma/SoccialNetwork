package org.example.socialnetworka.controllers;

import org.example.socialnetworka.message.ChatMessage;
import org.example.socialnetworka.message.Message;
import org.example.socialnetworka.repository.ChatRepository;
import org.example.socialnetworka.repository.MessageRepository;
import org.example.socialnetworka.repository.UserRepository;
import org.example.socialnetworka.repository.UsersChatsRepository;
import org.example.socialnetworka.users.User;
import org.example.socialnetworka.users.UserChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ChatController {

    private final UserRepository userRepository;
    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final UsersChatsRepository usersChatsRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public ChatController(SimpMessageSendingOperations messagingTemplate, UserRepository userRepository,
                          UsersChatsRepository usersChatsRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.simpMessageSendingOperations = messagingTemplate;
        this.usersChatsRepository = usersChatsRepository;
        this.messageRepository = messageRepository;
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        List<UserChat> allSubscribers = usersChatsRepository.findAllByChatId(chatMessage.getChatId());
        for (UserChat userChat : allSubscribers) {
            String destination = "/topic/users/" + userRepository.findUserById(userChat.getUserId()).getUsername();
            System.out.println(destination);
            simpMessageSendingOperations.convertAndSend(destination, chatMessage);

        }
        Message message = new Message(chatMessage.getChatId(),
                userRepository.findUserByUsername(chatMessage.getSender()).getId()
                , chatMessage.getContent());
        messageRepository.save(message);
    }

    @MessageMapping("/addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

}
