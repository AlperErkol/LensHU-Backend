package com.example.service.abstracts;

import com.example.dto.UserDto;
import com.example.util.response.ResponseModel;
import com.example.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUser(String email);
    ResponseModel<UserDto> createUser(UserDto userDto);
    ResponseModel<UserDto> logInUser(UserDto userDto);
    UserDto getUserById(Long id);

}
