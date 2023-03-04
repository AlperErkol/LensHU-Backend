package com.example.service.concretes;
import com.example.dto.RegisterUserDto;
import com.example.dto.UserDto;
import com.example.dto.ResetPasswordDto;
import com.example.model.PasswordResetToken;
import com.example.repository.PasswordResetRepository;
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
    private final PasswordResetRepository passwordResetRepository;
    private final VerificationTokenManager verificationTokenManager;
    private final ModelMapper modelMapper;
    private final PasswordConfig passwordConfig;

    @Autowired
    public UserManager(UserRepository userRepository, VerificationTokenManager verificationTokenManager, ModelMapper modelMapper, PasswordConfig passwordConfig, PasswordResetRepository passwordResetRepository) {
        super();
        this.userRepository = userRepository;
        this.verificationTokenManager = verificationTokenManager;
        this.modelMapper = modelMapper;
        this.passwordConfig = passwordConfig;
        this.passwordResetRepository = passwordResetRepository;
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
        return this.modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = this.userRepository.getUserById(id);
        if(user == null) return null;
        return this.modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = this.userRepository.getUserByEmail(email);
        return this.modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto findFirstByEmail(String email) {
        User user = this.userRepository.findFirstByEmail(email);
        return this.modelMapper.map(user, UserDto.class);
    }

    @Override
    public ResponseModel<Boolean> checkIfUserAvailableByEmail(String email) {
        User isUser = this.userRepository.getUserByEmail(email);

        if(isUser != null)
        {
            Payload<Boolean> payload = new Payload<>(null, false, ResponseMessage.EXISTING_USER_BY_EMAIL);
            return new ResponseModel<>(payload, HttpStatus.BAD_REQUEST);
        }

        Payload<Boolean> payload = new Payload<>(true, true, "OK!");
        return new ResponseModel<>(payload, HttpStatus.OK);

    }

    @Override
    public ResponseModel<UserDto> resetPassword(ResetPasswordDto resetPasswordDto) {
        String token = resetPasswordDto.getToken();
        PasswordResetToken passwordResetToken = this.passwordResetRepository.findByToken(token);
        User user = passwordResetToken.getUser();

        if(!passwordResetToken.isVerified())
        {
            Payload<UserDto> payload = new Payload<>(null, false, ResponseMessage.TOKEN_NOT_VERIFIED);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        if(user == null)
        {
            Payload<UserDto> payload = new Payload<>(null, false, ResponseMessage.USER_NOT_FOUND_BY_EMAIL);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        String oldPassword = user.getPassword();
        String newPassword = resetPasswordDto.getPassword();
        boolean isEqualOldPassword = this.passwordConfig.passwordEncoder().matches(newPassword, oldPassword);

        if(isEqualOldPassword)
        {
            Payload<UserDto> payload = new Payload<>(null, false, ResponseMessage.PASSWORD_SAME_OLD);
            return new ResponseModel<>(payload, HttpStatus.NOT_ACCEPTABLE);
        }

        boolean isPasswordsEqual = resetPasswordDto.checkIfPasswordsMatch();

        if(!isPasswordsEqual)
        {
            Payload<UserDto> payload = new Payload<>(null, false, ResponseMessage.PASSWORDS_DO_NOT_MATCH);
            return new ResponseModel<>(payload, HttpStatus.NOT_ACCEPTABLE);
        }

        String encodedPassword = this.passwordConfig.passwordEncoder().encode(newPassword);
        user.setPassword(encodedPassword);
        this.userRepository.save(user);
        UserDto userDto = modelMapper.map(user, UserDto.class);

        // Delete Token from DB.

        Payload<UserDto> payload = new Payload<>(userDto, true, ResponseMessage.PASSWORD_CHANGED);
        return new ResponseModel<>(payload, HttpStatus.CREATED);
    }

    @Override
    public ResponseModel<UserDto> createPasswordResetToken(String email) {
        User user = this.userRepository.findFirstByEmail(email);
        UserDto userDto = new UserDto(email);

        if(user == null)
        {

            Payload<UserDto> payload = new Payload<>(userDto, false, ResponseMessage.USER_NOT_FOUND_BY_EMAIL);
            return new ResponseModel<>(payload, HttpStatus.BAD_REQUEST);
        }

        PasswordResetToken passwordResetToken = new PasswordResetToken(user);
        this.passwordResetRepository.save(passwordResetToken);

        Payload<UserDto> payload = new Payload<>(userDto, true, ResponseMessage.TOKEN_SENT);
        return new ResponseModel<>(payload, HttpStatus.CREATED);
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
        verificationTokenManager.createVerificationToken(user);

        Payload<UserDto> payload = new Payload<>(userDto, true, ResponseMessage.USER_CREATED);
        return new ResponseModel<>(payload, HttpStatus.CREATED);

    }

    @Override
    public ResponseModel<UserDto> updateUser(UserDto userDto) {
        return null;
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
