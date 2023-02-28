package com.example.service.concretes;

import com.example.dto.UserDto;
import com.example.model.PasswordResetToken;
import com.example.model.Token;
import com.example.model.User;
import com.example.model.VerificationToken;
import com.example.repository.PasswordResetRepository;
import com.example.repository.UserRepository;
import com.example.repository.VerificationTokenRepository;
import com.example.service.abstracts.VerificationTokenService;
import com.example.util.response.Payload;
import com.example.util.response.ResponseMessage;
import com.example.util.response.ResponseModel;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class VerificationTokenManager implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public VerificationTokenManager(VerificationTokenRepository verificationTokenRepository, UserRepository userRepository, PasswordResetRepository passwordResetRepository, ModelMapper modelMapper){
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseModel<String> createVerificationTokenExplicit(String email) {
        User user = this.userRepository.getUserByEmail(email);
        if(user == null)
        {
            Payload<String> payload = new Payload<>(null, false, ResponseMessage.USER_NOT_FOUND_BY_EMAIL);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        VerificationToken verificationToken = findVerificationTokenByUser(user);
        //boolean isValid = verificationToken.isExpirationValid();

        if(verificationToken != null)
        {
            Payload<String> payload = new Payload<>(verificationToken.getToken(), true, ResponseMessage.TOKEN_SENT);
            return new ResponseModel<>(payload, HttpStatus.OK);
        }

        VerificationToken createdVerificationToken = createVerificationToken(user);
        Payload<String> payload = new Payload<>(createdVerificationToken.getToken(), true, ResponseMessage.TOKEN_SENT);
        return new ResponseModel<>(payload, HttpStatus.CREATED);
    }

    @Override
    public VerificationToken createVerificationToken(User user) {
        VerificationToken verificationToken = new VerificationToken(user);
        System.out.println(verificationToken);
        return this.verificationTokenRepository.save(verificationToken);
    }

    @Override
    public VerificationToken findVerificationTokenByUser(User user) {
        return this.verificationTokenRepository.findVerificationTokenByUser(user);
    }

    @Override
    @Transactional
    public ResponseModel<String> verifyToken(UserDto userDto, String token, String type) {
        String email = userDto.getEmail();
        System.out.println(email);
        User user = this.userRepository.getUserByEmail(email);

        if(user == null)
        {
            Payload<String> payload = new Payload<>(null, false,
                    ResponseMessage.USER_NOT_FOUND_BY_EMAIL);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        Token tokenModel = null;

        if(type == "verify-account")
        {
            tokenModel = this.verificationTokenRepository.findVerificationTokenByUser(user);
            System.out.println(tokenModel);
        }
        else if(type == "reset-password" )
        {
            tokenModel = this.passwordResetRepository.findByToken(token);
        }
        else System.out.println("Wrong type while getting token.");

        if(tokenModel == null)
        {
            Payload<String> payload = new Payload<>(null, false, ResponseMessage.TOKEN_NOT_FOUND);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        String DBToken = tokenModel.getToken();

        if(!token.equals(DBToken))
        {
            Payload<String> payload = new Payload<>(null, false, ResponseMessage.WRONG_TOKEN);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        boolean isExpiryValid = tokenModel.isExpirationValid();

        if(!isExpiryValid)
        {
            Payload<String> payload = new Payload<>(null, false, ResponseMessage.TOKEN_EXPIRED);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        if(type == "verify-account")
        {
            user.setActive(true);
            this.userRepository.save(user);

            Long tokenId = tokenModel.getId();
            // Use it for logging.
            boolean isDeleted = deleteVerificationToken(tokenId);
        }

        else if(type == "reset-password")
        {
            tokenModel.setVerified(true);
            PasswordResetToken passwordResetToken = this.modelMapper.map(tokenModel, PasswordResetToken.class);
            this.passwordResetRepository.save(passwordResetToken);
        }

        Payload<String> payload = new Payload<>(token, true, ResponseMessage.USER_ACTIVATED);
        return new ResponseModel<>(payload, HttpStatus.OK);

    }

    @Override
    public Boolean deleteVerificationToken(Long verificationTokenId) {
        System.out.println(verificationTokenId);
        Long deletedId = this.verificationTokenRepository.deleteVerificationTokenById(verificationTokenId);
        return verificationTokenId.equals(deletedId);
    }
}
