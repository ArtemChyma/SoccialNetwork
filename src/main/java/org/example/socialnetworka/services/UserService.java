package org.example.socialnetworka.services;

import org.example.socialnetworka.users.User;
import org.example.socialnetworka.users.UserDAO;

import java.util.List;

public interface UserService {
    List<User> findAll();
}
