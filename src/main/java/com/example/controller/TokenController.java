package com.example.controller;

import com.example.dto.EmailDto;
import com.example.dto.UserDto;
import com.example.service.abstracts.TokenService;
import com.example.service.abstracts.UserService;
import com.example.util.response.Payload;
import com.example.util.response.ResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class TokenController {
    private UserService userService;
    private TokenService tokenService;

    public TokenController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }
    @PostMapping("/verification/{tokenType}")
    public ResponseEntity<Payload<Boolean>> createToken(@RequestBody EmailDto emailDto, @PathVariable String tokenType){
        ResponseModel<Boolean> responseModel = this.tokenService.generateTokenAndSendEmail(emailDto.getEmail(), tokenType);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }
    @PostMapping("/verification/{token}/{type}")
    public ResponseEntity<Payload<Boolean>> verifyToken(@RequestBody UserDto userDto, @PathVariable String token,
                                                       @PathVariable String type){
        ResponseModel<Boolean> responseModel = this.tokenService.verifyToken(userDto, token, type);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }
}
