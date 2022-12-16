package com.example.service.concretes;
import com.example.dto.UserDto;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.security.PasswordConfig;
import com.example.service.abstracts.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@Component
public class UserManager implements UserService {

    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private PasswordConfig passwordConfig;

    @Autowired
    public UserManager(UserRepository userRepository, ModelMapper modelMapper, PasswordConfig passwordConfig) {
        super();
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordConfig = passwordConfig;
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = this.userRepository.findAll();
        List<UserDto> userList = new ArrayList<>();
        users.stream().forEach(user -> {
            UserDto userDto = this.modelMapper.map(user, UserDto.class);
            userList.add(userDto);
        });

        return userList;
    }

    @Override
    public UserDto getUser(String email) {
        User user = this.userRepository.getUserByEmail(email);
        if(user != null)
        {
            UserDto userDto = this.modelMapper.map(user, UserDto.class);
            return userDto;
        }
        else
        {
            return null;
        }
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        String email = userDto.getEmail();

        User hasUser = this.userRepository.findFirstByEmail(email);

        if(hasUser == null)
        {
            String password = userDto.getPassword();
            String encodedPassword = this.passwordConfig.passwordEncoder().encode(password);
            User user = modelMapper.map(userDto, User.class);
            user.setPassword(encodedPassword);
            this.userRepository.save(user);
            return userDto;
        }
        // There is a user with same email address.
        else
        {
            return null;
        }
    }

    @Override
    public UserDto logInUser(UserDto userDto) {
        String email = userDto.getEmail();
        String password = userDto.getPassword();

        User searchUser = this.userRepository.findFirstByEmail(email);
        if (searchUser != null) {
            boolean isPasswordMatches = this.passwordConfig.passwordEncoder().matches(password, searchUser.getPassword());
            // Success, create JWT Token
            if (isPasswordMatches)
            {
                return userDto;
            }
            // Incorrect Password
            else
            {
                return null;
            }
        }
        // There is no user with the specified email address.
        else
        {
            return null;
        }
    }
}
