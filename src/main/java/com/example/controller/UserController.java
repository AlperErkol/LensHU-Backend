package com.example.controller;

import com.example.dto.ChangePasswordDto;
import com.example.dto.EmailDto;
import com.example.dto.RegisterUserDto;
import com.example.dto.UserDto;
import com.example.model.Subscribe;
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

    @GetMapping("/users")
    public List<UserDto> getAllUsers(){
        List<UserDto> userDtoList = this.userService.getAllUsers();
        return userDtoList;
    }
    @GetMapping("/user/{id}")
    public UserDto getUserById(@PathVariable Long id){
        UserDto userDto = this.userService.getUserById(id);
        return userDto;
    }
    @PostMapping("/user/email")
    public ResponseEntity<Payload<Boolean>> checkIfUserAvailableByEmail(@RequestBody EmailDto emailDto){
        ResponseModel<Boolean> responseModel = this.userService.checkIfUserAvailableByEmail(emailDto.getEmail());
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }
    @PutMapping("/user")
    public ResponseEntity<Payload<UserDto>> updateUser(@RequestBody UserDto userDto){
        ResponseModel<UserDto> responseModel = this.userService.updateUser(userDto);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }
    @PostMapping("/user")
    public ResponseEntity<Payload<UserDto>> createUser(@RequestBody RegisterUserDto registerUserDto){
        ResponseModel<UserDto> responseModel = this.userService.createUser(registerUserDto);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }
    @PostMapping("/user/session")
    public ResponseEntity<Payload<UserDto>> logInUser(@RequestBody UserDto userDto){
        ResponseModel<UserDto> responseModel = this.userService.logInUser(userDto);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }
    @PostMapping("/user/subscription")
    public ResponseEntity<Payload<Boolean>> subscribe(@RequestBody Subscribe subscribe){
        ResponseModel<Boolean> responseModel = this.userService.subscribe(subscribe);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }
    @PostMapping("/user/password")
    public ResponseEntity<Payload<Boolean>> changePassword(@RequestBody ChangePasswordDto changePasswordDto){
        ResponseModel<Boolean> responseModel = this.userService.changePassword(changePasswordDto);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }
}
