package com.example.service.abstracts;

import com.example.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUser(String email);
    User createUser(User user);
}
