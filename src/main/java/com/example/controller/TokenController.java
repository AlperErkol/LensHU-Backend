package com.example.controller;

import com.example.dto.ChangePasswordDto;
import com.example.dto.UserDto;
import com.example.service.abstracts.UserService;
import com.example.service.abstracts.VerificationTokenService;
import com.example.util.response.Payload;
import com.example.util.response.ResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2")
@CrossOrigin
public class TokenController {
    private UserService userService;
    private VerificationTokenService verificationTokenService;

    public TokenController(UserService userService, VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping("/verification")
    public ResponseEntity<Payload<String>> createVerificationToken(@RequestBody String email){
        ResponseModel<String> responseModel = this.verificationTokenService.createVerificationTokenExplicit(email);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }

    @PostMapping("/verification/{token}/{type}")
    public ResponseEntity<Payload<String>> verifyToken(@RequestBody UserDto userDto, @PathVariable String token,
                                                       @PathVariable String type){
        ResponseModel<String> responseModel = this.verificationTokenService.verifyToken(userDto, token, type);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }

    @PostMapping("/reset/password")
    public ResponseEntity<Payload<UserDto>> createPasswordResetToken(@RequestBody String email){
        ResponseModel<UserDto> responseModel = this.userService.createPasswordResetToken(email);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }

    @PostMapping("/reset/passwords")
    public ResponseEntity<Payload<UserDto>> changePassword(@RequestBody ChangePasswordDto changePasswordDto){
        ResponseModel<UserDto> responseModel = this.userService.changePassword(changePasswordDto);
        return new ResponseEntity<>(responseModel.getPayload(), responseModel.getHttpStatus());
    }
}
