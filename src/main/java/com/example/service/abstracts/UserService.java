package com.example.service.abstracts;

import com.example.dto.UserDto;
import com.example.dto.ChangePasswordDto;
import com.example.util.response.ResponseModel;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUser(String email);
    ResponseModel<UserDto> createUser(UserDto userDto);
    ResponseModel<UserDto> logInUser(UserDto userDto);
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    UserDto findFirstByEmail(String email);
    ResponseModel<Boolean> checkIfUserAvailableByEmail(String email);
    ResponseModel<UserDto> changePassword(ChangePasswordDto changePasswordDto, String token);

}
