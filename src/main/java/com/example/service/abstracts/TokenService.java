package com.example.service.abstracts;


import com.example.dto.UserDto;
import com.example.util.response.ResponseModel;

public interface TokenService {

    ResponseModel<Boolean> generateTokenAndSendEmail(String email, String tokenType);
    ResponseModel<Boolean> verifyToken(UserDto userDto, String email, String token);

}
