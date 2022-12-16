package com.example.controller;

import com.example.dto.UserDto;
import com.example.model.User;
import com.example.service.abstracts.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/user")
    public List<UserDto> getAllUsers()
    {
        List<UserDto> userDtoList = this.userService.getAllUsers();
        return userDtoList;
    }

    @GetMapping("/user/{id}")
    public UserDto getUserById(@PathVariable Long id){
        UserDto userDto = this.userService.getUserById(id);
        return userDto;
    }

    @PostMapping("/user")
    public UserDto createUser(@RequestBody UserDto userDto){
        UserDto createdUser = this.userService.createUser(userDto);
        return createdUser;
    }

    @PostMapping("/user/session")
    public UserDto logInUser(@RequestBody UserDto userDto){
        UserDto loggedInUser = this.userService.logInUser(userDto);
        return loggedInUser;
    }

    @PutMapping("/user/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto){
        UserDto createdUser = this.userService.createUser(userDto);
        return createdUser;
    }

}
