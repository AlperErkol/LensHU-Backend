package com.example.controller;

import com.example.dto.UserDto;
import com.example.service.abstracts.VerificationTokenService;
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
    private VerificationTokenService verificationTokenService;

    public UserController(UserService userService, VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
    }

    @GetMapping("/users")
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

    @PostMapping("/user/email")
    public ResponseEntity<Payload<Boolean>> checkIfUserAvailableByEmail(@RequestBody String email){
        System.out.println(email);
        ResponseModel<Boolean> responseModel = this.userService.checkIfUserAvailableByEmail(email);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }

    @PostMapping("/user")
    public ResponseEntity<Payload<UserDto>> createUser(@RequestBody UserDto userDto){
        ResponseModel<UserDto> responseModel = this.userService.createUser(userDto);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }

    @PostMapping("/user/session")
    public ResponseEntity<Payload<UserDto>> logInUser(@RequestBody UserDto userDto){
        ResponseModel<UserDto> responseModel = this.userService.logInUser(userDto);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<Payload<UserDto>> updateUser(@PathVariable Long id, @RequestBody UserDto userDto){
        ResponseModel<UserDto> responseModel = this.userService.createUser(userDto);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }
}
