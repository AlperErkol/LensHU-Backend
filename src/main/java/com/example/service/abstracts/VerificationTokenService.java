package com.example.service.abstracts;

import com.example.dto.UserDto;
import com.example.model.User;
import com.example.model.VerificationToken;
import com.example.util.response.ResponseModel;

public interface VerificationTokenService {

    ResponseModel<String> createVerificationTokenExplicit(String email);
    VerificationToken createVerificationToken(User user);
    VerificationToken findVerificationTokenByUser(User user);
    ResponseModel<String> verifyToken(UserDto userDto, String token);
    Boolean deleteVerificationToken(Long tokenId);


}
