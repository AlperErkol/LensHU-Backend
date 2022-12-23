package com.example.security.concretes;
import com.example.dto.UserDto;
import com.example.util.response.Payload;
import com.example.util.response.ResponseModel;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.security.PasswordConfig;
import com.example.service.abstracts.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@Component
public class UserManager implements UserService {

    private UserRepository userRepository;
    private VerificationTokenManager verificationTokenManager;
    private ModelMapper modelMapper;
    private PasswordConfig passwordConfig;

    @Autowired
    public UserManager(UserRepository userRepository, VerificationTokenManager verificationTokenManager, ModelMapper modelMapper, PasswordConfig passwordConfig) {
        super();
        this.userRepository = userRepository;
        this.verificationTokenManager = verificationTokenManager;
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

        if(user == null) return null;

        UserDto userDto = this.modelMapper.map(user, UserDto.class);
        return userDto;
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = this.userRepository.getUserById(id);
        if(user == null) return null;
        UserDto userDto = this.modelMapper.map(user, UserDto.class);
        return userDto;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = this.userRepository.getUserByEmail(email);
        UserDto userDto = this.modelMapper.map(user, UserDto.class);
        return userDto;
    }

    @Override
    public UserDto findFirstByEmail(String email) {
        User user = this.userRepository.findFirstByEmail(email);
        UserDto userDto = this.modelMapper.map(user, UserDto.class);
        return userDto;
    }

    @Override
    public ResponseModel<UserDto> createUser(UserDto userDto) {

        String email = userDto.getEmail();
        User hasUser = this.userRepository.findFirstByEmail(email);

        if(hasUser != null)
        {
            Payload<UserDto> payload = new Payload<>(userDto, false, "There is an existing account with the specified email address.");
            return new ResponseModel<>(payload, HttpStatus.BAD_REQUEST);
        }

        String password = userDto.getPassword();
        String encodedPassword = this.passwordConfig.passwordEncoder().encode(password);
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(encodedPassword);
        user.setActive(false);
        this.userRepository.save(user);
        verificationTokenManager.createVerificationTokenByUser(user);

        Payload<UserDto> payload = new Payload<>(userDto, true, "Your account has been successfully created.");
        return new ResponseModel<>(payload, HttpStatus.CREATED);

    }

    @Override
    public ResponseModel<UserDto> logInUser(UserDto userDto) {

        String email = userDto.getEmail();
        String password = userDto.getPassword();
        User searchUser = this.userRepository.findFirstByEmail(email);

        if(searchUser == null){
            Payload<UserDto> payload = new Payload<UserDto>(null, false, "There is no such user.");
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        UserDto mappedUserDto = this.modelMapper.map(searchUser, UserDto.class);

        boolean isPasswordMatches = this.passwordConfig.passwordEncoder().matches(password, searchUser.getPassword());
        boolean isActive = searchUser.isActive();

        if(!isPasswordMatches){
            Payload<UserDto> payload = new Payload<UserDto>(null, false, "Your credentials are wrong.");
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }
        if(!isActive){
            Payload<UserDto> payload = new Payload<UserDto>(null, false, "You're not an active user.");
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        Payload<UserDto> payload = new Payload<UserDto>(mappedUserDto, true, "You're logged in");
        return new ResponseModel<>(payload, HttpStatus.OK);
    }

}
