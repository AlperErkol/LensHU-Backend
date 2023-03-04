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
import com.example.util.email.EmailService;
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
    private final EmailService emailService;

    @Autowired
    public VerificationTokenManager(VerificationTokenRepository verificationTokenRepository, UserRepository userRepository, PasswordResetRepository passwordResetRepository, ModelMapper modelMapper, EmailService emailService){
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
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
        emailService.send(user.getEmail(), "Welcome to Lensa!", buildEmail(user.getName(), verificationToken.getToken()));
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

        if(type.equals("verify-account"))
        {
            System.out.println("şimdi yapayanlızım i wanna text you..");
            tokenModel = this.verificationTokenRepository.findVerificationTokenByUser(user);
            System.out.println(tokenModel);
        }
        else if(type.equals("reset-password"))
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

        if(type.equals("verify-account"))
        {
            user.setActive(true);
            this.userRepository.save(user);

            Long tokenId = tokenModel.getId();
            // Use it for logging.
            boolean isDeleted = deleteVerificationToken(tokenId);
        }

        else if(type.equals("reset-password"))
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

    private String buildEmail(String name, String token) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Verify your account</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please copy and paste the below code to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <p>" + token +"</p> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>\n <p>Team Lensa</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
