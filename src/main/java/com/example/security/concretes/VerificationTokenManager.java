package com.example.security.concretes;

import com.example.dto.UserDto;
import com.example.model.User;
import com.example.model.VerificationToken;
import com.example.repository.UserRepository;
import com.example.repository.VerificationTokenRepository;
import com.example.service.abstracts.VerificationTokenService;
import com.example.util.response.Payload;
import com.example.util.response.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Component
public class VerificationTokenManager implements VerificationTokenService {

    private VerificationTokenRepository verificationTokenRepository;
    private UserRepository userRepository;

    @Autowired
    public VerificationTokenManager(VerificationTokenRepository verificationTokenRepository, UserRepository userRepository){
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseModel<String> createVerificationToken(String email) {
        User user = this.userRepository.getUserByEmail(email);
        if(user == null)
        {
            Payload<String> payload = new Payload<>(null, false, "Could not found the user with the specified email.");
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        VerificationToken verificationToken = findVerificationTokenByUser(user);

        boolean isValid = verificationToken.isExpirationValid();

        if(verificationToken != null && isValid)
        {
            Payload<String> payload = new Payload<>(verificationToken.getToken(), true, "Sent old verification token to your email.");
            return new ResponseModel<>(payload, HttpStatus.OK);
        }

        VerificationToken tokenModel = new VerificationToken(user);
        this.verificationTokenRepository.save(tokenModel);
        Payload<String> payload = new Payload<>(tokenModel.getToken(), true, "Verification token is successfully created and sent your email.");
        return new ResponseModel<>(payload, HttpStatus.CREATED);
    }

    @Override
    public VerificationToken createVerificationTokenByUser(User user) {
        VerificationToken verificationToken = new VerificationToken(user);
        return this.verificationTokenRepository.save(verificationToken);
    }

    @Override
    public VerificationToken findVerificationTokenByUser(User user) {
        VerificationToken verificationToken = this.verificationTokenRepository.findVerificationTokenByUser(user);
        return verificationToken;
    }

    @Override
    public ResponseModel<String> verifyToken(UserDto userDto, String token) {
        String email = userDto.getEmail();
        User user = this.userRepository.getUserByEmail(email);

        if(user == null)
        {
            Payload<String> payload = new Payload<>(null, false, "There is no such a user with the specified email.");
            ResponseModel<String> responseModel = new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
            return responseModel;
        }

        VerificationToken verificationToken = this.verificationTokenRepository.findVerificationTokenByUser(user);
        String DBToken = verificationToken.getToken();
        Date expiryDate = verificationToken.getExpiryDate();

        if(!token.equals(DBToken))
        {
            Payload<String> payload = new Payload<>(null, false, "You've entered incorrect token.");
            ResponseModel<String> responseModel = new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
            return responseModel;
        }

        boolean isExpiryValid = verificationToken.isExpirationValid();

        if(!isExpiryValid)
        {
            Payload<String> payload = new Payload<>(null, false, "Your verification token is expired. New one sent.");
            ResponseModel<String> responseModel = new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
            return responseModel;
        }

        Payload<String> payload = new Payload<>(token, true, "Your account has been verified.");
        ResponseModel<String> responseModel = new ResponseModel<>(payload, HttpStatus.OK);
        return responseModel;

    }
}
