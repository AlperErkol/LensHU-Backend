package com.example.controller;

import com.example.dto.UserDto;
import com.example.model.User;
import com.example.service.abstracts.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public UserDto getUser(@RequestBody String email){
        UserDto userDto = this.userService.getUser(email);
        return userDto;
    }

    @PostMapping("/user")
    public UserDto createUser(@RequestBody UserDto userDto){
        UserDto createdUser = this.userService.createUser(userDto);
        return createdUser;
    }





}
