package com.example.service.concretes;
import com.example.dto.ChangePasswordDto;
import com.example.dto.RegisterUserDto;
import com.example.dto.UserDto;
import com.example.model.Subscribe;
import com.example.repository.SubscribeRepository;
import com.example.service.abstracts.TokenService;
import com.example.util.response.Payload;
import com.example.util.response.ResponseMessage;
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

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final SubscribeRepository subscribeRepository;
    private final ModelMapper modelMapper;
    private final PasswordConfig passwordConfig;


    @Autowired
    public UserManager(UserRepository userRepository, ModelMapper modelMapper, PasswordConfig passwordConfig, SubscribeRepository subscribeRepository, TokenService tokenService) {
        super();
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordConfig = passwordConfig;
        this.subscribeRepository = subscribeRepository;
        this.tokenService = tokenService;
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
    public UserDto getUserById(Long id) {
        User user = this.userRepository.getUserById(id);
        if(user == null) return null;
        return this.modelMapper.map(user, UserDto.class);
    }
    @Override
    public ResponseModel<Boolean> checkIfUserAvailableByEmail(String email) {
        User isUser = this.userRepository.getUserByEmail(email);

        if(isUser != null)
        {
            Payload<Boolean> payload = new Payload<>(null, false, ResponseMessage.EXISTING_USER_BY_EMAIL);
            return new ResponseModel<>(payload, HttpStatus.OK);
        }

        Payload<Boolean> payload = new Payload<>(true, true, "OK!");
        return new ResponseModel<>(payload, HttpStatus.OK);
    }
    @Override
    public ResponseModel<Boolean> subscribe(Subscribe subscribe) {
        String email = subscribe.getEmail();
        Subscribe isSubscribed = this.subscribeRepository.getSubscribeByEmail(email);
        if(isSubscribed != null)
        {
            Payload<Boolean> payload = new Payload<>(null, false, ResponseMessage.ALREADY_SUBSCRIBE);
            return new ResponseModel<>(payload, HttpStatus.OK);
        }
        this.subscribeRepository.save(subscribe);
        Payload<Boolean> payload = new Payload<>(null, true, ResponseMessage.SUBSCRIBED_SUCCESSFULLY);
        return new ResponseModel<>(payload, HttpStatus.OK);
    }

    @Override
    public ResponseModel<Boolean> changePassword(ChangePasswordDto changePasswordDto, String changePasswordType) {
        Payload<Boolean> payload = null;
        String email = changePasswordDto.getEmail();
        String password = changePasswordDto.getPassword();
        User user = this.userRepository.getUserByEmail(email);
        boolean isNewPasswordsMatch = changePasswordDto.checkIfPasswordsMatch();
        boolean isNewPasswordSameWithOld = this.passwordConfig.passwordEncoder().matches(password, user.getPassword());
        if(!(changePasswordType.equals("reset") || changePasswordType.equals("change"))) {
            payload = new Payload<>(false, false, ResponseMessage.AN_ERROR_OCCURED);
            return new ResponseModel<>(payload, HttpStatus.OK);
        }

        String currentPassword = changePasswordDto.getCurrentPassword();
        boolean isCurrentPasswordMatchOld = false;
        if (changePasswordType.equals("reset")) isCurrentPasswordMatchOld = true;
        else isCurrentPasswordMatchOld = this.passwordConfig.passwordEncoder().matches(currentPassword, user.getPassword());

        if (isNewPasswordSameWithOld) {
            payload = new Payload<>(false, false, ResponseMessage.PASSWORD_SAME_OLD);
            return new ResponseModel<>(payload, HttpStatus.OK);
        }

        if (!isNewPasswordsMatch) {
            payload = new Payload<>(false, false, ResponseMessage.PASSWORDS_DO_NOT_MATCH);
            return new ResponseModel<>(payload, HttpStatus.OK);
        }

        if (!isCurrentPasswordMatchOld) {
            payload = new Payload<>(false, false, ResponseMessage.CURRENT_PASSWORD_DO_NOT_MATCH_WITH_OLD);
            return new ResponseModel<>(payload, HttpStatus.OK);
        }


        String encodedPassword = this.passwordConfig.passwordEncoder().encode(password);
        user.setPassword(encodedPassword);
        this.userRepository.save(user);
        payload = new Payload<>(true, true, ResponseMessage.PASSWORD_CHANGED);
        return new ResponseModel<>(payload, HttpStatus.OK);
    }

    @Override
    public ResponseModel<UserDto> createUser(RegisterUserDto registerUserDto) {
        String email = registerUserDto.getEmail();
        User hasUser = this.userRepository.findFirstByEmail(email);
        if(hasUser != null)
        {
            UserDto userDto = this.modelMapper.map(hasUser, UserDto.class);
            Payload<UserDto> payload = new Payload<>(userDto, false, ResponseMessage.EXISTING_USER_BY_EMAIL);
            return new ResponseModel<>(payload, HttpStatus.BAD_REQUEST);
        }
        UserDto userDto = this.modelMapper.map(registerUserDto, UserDto.class);
        String password = registerUserDto.getPassword();
        String encodedPassword = this.passwordConfig.passwordEncoder().encode(password);
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(encodedPassword);
        user.setActive(false);
        this.userRepository.save(user);
        tokenService.generateTokenAndSendEmail(user.getEmail(), "verify-account");

        Payload<UserDto> payload = new Payload<>(userDto, true, ResponseMessage.USER_CREATED);
        return new ResponseModel<>(payload, HttpStatus.CREATED);
    }
    @Override
    public ResponseModel<UserDto> updateUser(UserDto userDto) {
        String email = userDto.getEmail();
        User user = this.userRepository.getUserByEmail(email);
        if(user == null)
        {
            Payload<UserDto> payload = new Payload<UserDto>(null, false, ResponseMessage.USER_NOT_FOUND_BY_EMAIL);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }
        String updateName = userDto.getName();
        String updateSurname = userDto.getSurname();
        user.setName(updateName);
        user.setSurname(updateSurname);
        this.userRepository.save(user);
        Payload<UserDto> payload = new Payload<>(userDto, true, ResponseMessage.USER_UPDATED);
        return new ResponseModel<>(payload, HttpStatus.CREATED);
    }
    @Override
    public ResponseModel<UserDto> logInUser(UserDto userDto) {

        String email = userDto.getEmail();
        String password = userDto.getPassword();
        User searchUser = this.userRepository.findFirstByEmail(email);

        if(searchUser == null){
            Payload<UserDto> payload = new Payload<UserDto>(null, false, ResponseMessage.USER_NOT_FOUND_BY_EMAIL);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        UserDto mappedUserDto = this.modelMapper.map(searchUser, UserDto.class);

        boolean isPasswordMatches = this.passwordConfig.passwordEncoder().matches(password, searchUser.getPassword());
        boolean isActive = searchUser.isActive();

        if(!isPasswordMatches){
            Payload<UserDto> payload = new Payload<UserDto>(null, false, ResponseMessage.WRONG_CREDENTIALS);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }
        if(!isActive){
            Payload<UserDto> payload = new Payload<UserDto>(null, false, ResponseMessage.USER_NOT_ACTIVE);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        Payload<UserDto> payload = new Payload<UserDto>(mappedUserDto, true, "You're logged in");
        return new ResponseModel<>(payload, HttpStatus.OK);
    }

}
