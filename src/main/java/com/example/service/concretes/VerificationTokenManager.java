package com.example.service.concretes;

import com.example.dto.UserDto;
import com.example.model.User;
import com.example.model.VerificationToken;
import com.example.repository.UserRepository;
import com.example.repository.VerificationTokenRepository;
import com.example.service.abstracts.VerificationTokenService;
import com.example.util.response.Payload;
import com.example.util.response.ResponseMessage;
import com.example.util.response.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class VerificationTokenManager implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public VerificationTokenManager(VerificationTokenRepository verificationTokenRepository, UserRepository userRepository){
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
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
        boolean isValid = verificationToken.isExpirationValid();

        if(verificationToken != null && isValid)
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
        return this.verificationTokenRepository.save(verificationToken);
    }

    @Override
    public VerificationToken findVerificationTokenByUser(User user) {
        return this.verificationTokenRepository.findVerificationTokenByUser(user);
    }

    @Override
    public ResponseModel<String> verifyToken(UserDto userDto, String token) {
        String email = userDto.getEmail();
        User user = this.userRepository.getUserByEmail(email);

        if(user == null)
        {
            Payload<String> payload = new Payload<>(null, false,
                    ResponseMessage.USER_NOT_FOUND_BY_EMAIL);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        VerificationToken verificationToken = this.verificationTokenRepository.findVerificationTokenByUser(user);
        String DBToken = verificationToken.getToken();

        if(!token.equals(DBToken))
        {
            Payload<String> payload = new Payload<>(null, false, ResponseMessage.WRONG_TOKEN);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        boolean isExpiryValid = verificationToken.isExpirationValid();

        if(!isExpiryValid)
        {
            Payload<String> payload = new Payload<>(null, false, ResponseMessage.TOKEN_EXPIRED);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        user.setActive(true);
        this.userRepository.save(user);

        Long tokenId = verificationToken.getId();
        deleteVerificationToken(tokenId);

        Payload<String> payload = new Payload<>(token, true, ResponseMessage.USER_ACTIVATED);
        return new ResponseModel<>(payload, HttpStatus.OK);

    }

    @Override
    public Boolean deleteVerificationToken(Long verificationTokenId) {
        VerificationToken deletedVerificationToken = this.verificationTokenRepository
                                                                    .deleteVerificationTokenById(verificationTokenId);
        return deletedVerificationToken != null;
    }
}
