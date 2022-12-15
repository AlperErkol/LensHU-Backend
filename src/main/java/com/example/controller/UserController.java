package com.example.controller;

import com.example.model.User;
import com.example.service.abstracts.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/user")
    public User getUser(@RequestBody String email){
        User user = this.userService.getUser(email);
        return user;
    }
    @PostMapping("/user")
    public User createUser(@RequestBody User user){
        User createdUser = this.userService.createUser(user);
        return createdUser;
    }

}
