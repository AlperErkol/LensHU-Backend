package com.example.service.abstracts;

import com.example.dto.RegisterUserDto;
import com.example.dto.UserDto;
import com.example.model.Subscribe;
import com.example.util.response.ResponseModel;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    ResponseModel<UserDto> createUser(RegisterUserDto registerUserDto);
    ResponseModel<UserDto> updateUser(UserDto userDto);
    ResponseModel<UserDto> logInUser(UserDto userDto);
    UserDto getUserById(Long id);
    ResponseModel<Boolean> checkIfUserAvailableByEmail(String email);
    ResponseModel<Boolean> subscribe(Subscribe subscribe);
}
