package com.example.controller;

import com.example.dto.UserDto;
import com.example.util.response.Payload;
import com.example.util.response.ResponseModel;
import com.example.service.abstracts.UserService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Payload<UserDto>> createUser(@RequestBody UserDto userDto){
        ResponseModel<UserDto> createdUser = this.userService.createUser(userDto);
        return new ResponseEntity<>(createdUser.getPayload(), createdUser.getHttpStatus());
    }

    @PostMapping("/user/session")
    public ResponseEntity<Payload<UserDto>> logInUser(@RequestBody UserDto userDto){
        ResponseModel<UserDto> loggedInUser = this.userService.logInUser(userDto);
        return new ResponseEntity<>(loggedInUser.getPayload(), loggedInUser.getHttpStatus());
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<Payload<UserDto>> updateUser(@PathVariable Long id, @RequestBody UserDto userDto){
        ResponseModel<UserDto> createdUser = this.userService.createUser(userDto);
        return new ResponseEntity<>(createdUser.getPayload(), createdUser.getHttpStatus());
    }

}
