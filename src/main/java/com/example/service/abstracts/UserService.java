package com.example.service.abstracts;

import com.example.dto.RegisterUserDto;
import com.example.dto.UserDto;
import com.example.dto.ResetPasswordDto;
import com.example.util.response.ResponseModel;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUser(String email);
    ResponseModel<UserDto> createUser(RegisterUserDto registerUserDto);
    ResponseModel<UserDto> updateUser(UserDto userDto);
    ResponseModel<UserDto> logInUser(UserDto userDto);
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    UserDto findFirstByEmail(String email);
    ResponseModel<Boolean> checkIfUserAvailableByEmail(String email);
    ResponseModel<UserDto> resetPassword(ResetPasswordDto resetPasswordDto);
    ResponseModel<UserDto> createPasswordResetToken(String email);
}
