package com.example.service.concretes;

import com.example.dto.UserDto;
import com.example.model.PasswordResetToken;
import com.example.model.Token;
import com.example.model.User;
import com.example.model.VerificationToken;
import com.example.repository.PasswordResetRepository;
import com.example.repository.UserRepository;
import com.example.repository.VerificationTokenRepository;
import com.example.service.abstracts.TokenService;
import com.example.util.email.EmailService;
import com.example.util.response.Payload;
import com.example.util.response.ResponseMessage;
import com.example.util.response.ResponseModel;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class TokenManager implements TokenService {
    private UserRepository userRepository;
    private VerificationTokenRepository verificationTokenRepository;
    private PasswordResetRepository passwordResetRepository;
    private EmailService emailService;
    @Autowired
    public TokenManager(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, PasswordResetRepository passwordResetRepository, EmailService emailService){
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.emailService = emailService;
    }
    @Override
    public ResponseModel<Boolean> generateTokenAndSendEmail(String email, String tokenType) {
        User user = this.userRepository.getUserByEmail(email);
        Payload<Boolean> payload = null;
        if(user == null)
        {
            payload = new Payload<>(null, false, ResponseMessage.USER_NOT_FOUND_BY_EMAIL);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        if(tokenType.equals("verify-account")) {
            VerificationToken token = new VerificationToken(user);
            this.verificationTokenRepository.save(token);
            sendToken(user, token);
        }
        else
        {
            PasswordResetToken token = new PasswordResetToken(user);
            this.passwordResetRepository.save(token);
            sendToken(user, token);
        }
        payload = new Payload<>(null, true, ResponseMessage.TOKEN_SENT);
        return new ResponseModel<>(payload, HttpStatus.CREATED);
    }
    @Override
    @Transactional
    public ResponseModel<Boolean> verifyToken(UserDto userDto, String token, String tokenType) {
        String email = userDto.getEmail();
        User user = this.userRepository.getUserByEmail(email);
        if(user == null)
        {
            Payload<Boolean> payload = new Payload<>(null, false,
                    ResponseMessage.USER_NOT_FOUND_BY_EMAIL);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }
        Token tokenModel = null;
        if(tokenType.equals("verify-account"))
        {
            tokenModel = this.verificationTokenRepository.findVerificationTokenByUser(user);
        }
        else
        {
            tokenModel = this.passwordResetRepository.findPasswordResetTokenByUser(user);
        }

        if(tokenModel == null)
        {
            Payload<Boolean> payload = new Payload<>(null, false, ResponseMessage.TOKEN_NOT_FOUND);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        String DBToken = tokenModel.getToken();
        boolean isExpiryValid = tokenModel.isExpirationValid();
        Long tokenId = tokenModel.getId();

        if(!token.equals(DBToken))
        {
            Payload<Boolean> payload = new Payload<>(null, false, ResponseMessage.WRONG_TOKEN);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }
        if(!isExpiryValid)
        {
            Payload<Boolean> payload = new Payload<>(null, false, ResponseMessage.TOKEN_EXPIRED);
            return new ResponseModel<>(payload, HttpStatus.NOT_FOUND);
        }

        if(tokenType.equals("verify-account"))
        {
            user.setActive(true);
            this.userRepository.save(user);
            this.verificationTokenRepository.deleteVerificationTokenById(tokenId);
        }
        else if(tokenType.equals("password-reset"))
        {
            tokenModel.setVerified(true);
            this.passwordResetRepository.deletePasswordResetTokenById(tokenId);
        }

        Payload<Boolean> payload = new Payload<>(true, true, ResponseMessage.USER_ACTIVATED);
        return new ResponseModel<>(payload, HttpStatus.OK);
    }
    private String buildEmail(String name, String token) {
        return "     <div>\n" +
                "      <table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "        <tbody>\n" +
                "          <tr>\n" +
                "            <td valign=\"top\" width=\"100%\">\n" +
                "              <table\n" +
                "                width=\"100%\"\n" +
                "                role=\"content-container\"\n" +
                "                cellpadding=\"0\"\n" +
                "                cellspacing=\"0\"\n" +
                "              >\n" +
                "                <tbody>\n" +
                "                  <tr>\n" +
                "                    <td width=\"100%\">\n" +
                "                      <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                "                        <tbody>\n" +
                "                          <tr>\n" +
                "                            <td>\n" +
                "                              <table\n" +
                "                                width=\"100%\"\n" +
                "                                cellpadding=\"0\"\n" +
                "                                cellspacing=\"0\"\n" +
                "                                style=\"width: 100%; max-width: 600px\"\n" +
                "                                align=\"center\"\n" +
                "                              >\n" +
                "                                <tbody>\n" +
                "                                  <tr>\n" +
                "                                    <td\n" +
                "                                      role=\"modules-container\"\n" +
                "                                      style=\"\n" +
                "                                        padding: 0px 0px 0px 0px;\n" +
                "                                        color: #000000;\n" +
                "                                        text-align: left;\n" +
                "                                      \"\n" +
                "                                      width=\"100%\"\n" +
                "                                    >\n" +
                "                                      <table\n" +
                "                                        cellpadding=\"0\"\n" +
                "                                        cellspacing=\"0\"\n" +
                "                                        width=\"100%\"\n" +
                "                                        role=\"module\"\n" +
                "                                        data-type=\"columns\"\n" +
                "                                        style=\"padding: 30px 20px\"\n" +
                "                                        bgcolor=\"#424549\"\n" +
                "                                      >\n" +
                "                                        <tbody>\n" +
                "                                          <tr role=\"module-content\">\n" +
                "                                            <td height=\"100%\" valign=\"top\">\n" +
                "                                              <table\n" +
                "                                                class=\"column\"\n" +
                "                                                width=\"540\"\n" +
                "                                                style=\"\n" +
                "                                                  width: 540px;\n" +
                "                                                  border-spacing: 0;\n" +
                "                                                  border-collapse: collapse;\n" +
                "                                                  margin: 0px 10px 0px 10px;\n" +
                "                                                \"\n" +
                "                                                cellpadding=\"0\"\n" +
                "                                                cellspacing=\"0\"\n" +
                "                                              >\n" +
                "                                                <tbody>\n" +
                "                                                  <tr>\n" +
                "                                                    <td\n" +
                "                                                      style=\"\n" +
                "                                                        padding: 0px;\n" +
                "                                                        margin: 0px;\n" +
                "                                                        border-spacing: 0;\n" +
                "                                                      \"\n" +
                "                                                    >\n" +
                "                                                      <table\n" +
                "                                                        class=\"wrapper\"\n" +
                "                                                        role=\"module\"\n" +
                "                                                        data-type=\"image\"\n" +
                "                                                        cellpadding=\"0\"\n" +
                "                                                        cellspacing=\"0\"\n" +
                "                                                        width=\"100%\"\n" +
                "                                                        style=\"\n" +
                "                                                          table-layout: fixed;\n" +
                "                                                        \"\n" +
                "                                                      >\n" +
                "                                                        <tbody>\n" +
                "                                                          <tr>\n" +
                "                                                            <td\n" +
                "                                                              style=\"\n" +
                "                                                                font-size: 6px;\n" +
                "                                                                line-height: 10px;\n" +
                "                                                              \"\n" +
                "                                                              valign=\"top\"\n" +
                "                                                              align=\"center\"\n" +
                "                                                            >\n" +
                "                                                              <a\n" +
                "                                                                href=\"https://lensly.io/\"\n" +
                "                                                                target=\"_blank\"\n" +
                "                                                                rel=\"noreferrer\"\n" +
                "                                                              >\n" +
                "                                                                <img\n" +
                "                                                                  class=\"max-width\"\n" +
                "                                                                  border=\"0\"\n" +
                "                                                                  style=\"\n" +
                "                                                                    display: block;\n" +
                "                                                                    color: #000000;\n" +
                "                                                                    text-decoration: none;\n" +
                "                                                                    font-size: 16px;\n" +
                "                                                                    padding-bottom: 30px;\n" +
                "                                                                  \"\n" +
                "                                                                  width=\"80\"\n" +
                "                                                                  alt=\"Lensly-logo\"\n" +
                "                                                                  data-proportionally-constrained=\"true\"\n" +
                "                                                                  data-responsive=\"false\"\n" +
                "                                                                  src=\"Asset8.png\"\n" +
                "                                                                  height=\"80\"\n" +
                "                                                              /></a>\n" +
                "                                                            </td>\n" +
                "                                                          </tr>\n" +
                "                                                        </tbody>\n" +
                "                                                      </table>\n" +
                "                                                      <table\n" +
                "                                                        class=\"module\"\n" +
                "                                                        role=\"module\"\n" +
                "                                                        data-type=\"text\"\n" +
                "                                                        cellpadding=\"0\"\n" +
                "                                                        cellspacing=\"0\"\n" +
                "                                                        width=\"100%\"\n" +
                "                                                        style=\"\n" +
                "                                                          table-layout: fixed;\n" +
                "                                                        \"\n" +
                "                                                      >\n" +
                "                                                        <tbody>\n" +
                "                                                          <tr>\n" +
                "                                                            <td\n" +
                "                                                              style=\"\n" +
                "                                                                padding: 50px\n" +
                "                                                                  30px 18px 30px;\n" +
                "                                                                line-height: 36px;\n" +
                "                                                                text-align: inherit;\n" +
                "                                                                background-color: #ffffff;\n" +
                "                                                              \"\n" +
                "                                                              height=\"100%\"\n" +
                "                                                              valign=\"top\"\n" +
                "                                                              role=\"module-content\"\n" +
                "                                                            >\n" +
                "                                                              <div>\n" +
                "                                                                <div\n" +
                "                                                                  style=\"\n" +
                "                                                                    font-family: inherit;\n" +
                "                                                                    text-align: center;\n" +
                "                                                                  \"\n" +
                "                                                                >\n" +
                "                                                                  <span\n" +
                "                                                                    style=\"\n" +
                "                                                                      font-size: 43px;\n" +
                "                                                                    \"\n" +
                "                                                                    >Thanks for\n" +
                "                                                                    signing up,\n" + name + "&nbsp;</span\n" +
                "                                                                  >\n" +
                "                                                                </div>\n" +
                "                                                                <div></div>\n" +
                "                                                              </div>\n" +
                "                                                            </td>\n" +
                "                                                          </tr>\n" +
                "                                                        </tbody>\n" +
                "                                                      </table>\n" +
                "                                                      <table\n" +
                "                                                        class=\"module\"\n" +
                "                                                        role=\"module\"\n" +
                "                                                        data-type=\"text\"\n" +
                "                                                        cellpadding=\"0\"\n" +
                "                                                        cellspacing=\"0\"\n" +
                "                                                        width=\"100%\"\n" +
                "                                                        style=\"\n" +
                "                                                          table-layout: fixed;\n" +
                "                                                        \"\n" +
                "                                                        dat#fa-muid=\"a10dcb57-ad22-4f4d-b765-1d427dfddb4e\"\n" +
                "                                                        data-mc-module-version=\"2019-10-22\"\n" +
                "                                                      >\n" +
                "                                                        <tbody>\n" +
                "                                                          <tr>\n" +
                "                                                            <td\n" +
                "                                                              style=\"\n" +
                "                                                                padding: 18px\n" +
                "                                                                  30px 18px 30px;\n" +
                "                                                                line-height: 22px;\n" +
                "                                                                text-align: inherit;\n" +
                "                                                                background-color: #ffffff;\n" +
                "                                                              \"\n" +
                "                                                              height=\"100%\"\n" +
                "                                                              valign=\"top\"\n" +
                "                                                              role=\"module-content\"\n" +
                "                                                            >\n" +
                "                                                              <div>\n" +
                "                                                                <div\n" +
                "                                                                  style=\"\n" +
                "                                                                    font-family: inherit;\n" +
                "                                                                    text-align: center;\n" +
                "                                                                  \"\n" +
                "                                                                >\n" +
                "                                                                  <span\n" +
                "                                                                    style=\"\n" +
                "                                                                      font-size: 18px;\n" +
                "                                                                    \"\n" +
                "                                                                  >\n" +
                "                                                                    Welcome to\n" +
                "                                                                    <span\n" +
                "                                                                      style=\"\n" +
                "                                                                        color: #0aad5b;\n" +
                "                                                                        font-size: 18px;\n" +
                "                                                                        font-family: arial,\n" +
                "                                                                          helvetica,\n" +
                "                                                                          sans-serif;\n" +
                "                                                                        font-weight: 600;\n" +
                "                                                                      \"\n" +
                "                                                                    >\n" +
                "                                                                      Lensly!</span\n" +
                "                                                                    >\n" +
                "                                                                    Please\n" +
                "                                                                    verify your\n" +
                "                                                                    account by\n" +
                "                                                                    using the\n" +
                "                                                                    6-digit\n" +
                "                                                                    verification\n" +
                "                                                                    code.\n" +
                "                                                                    <br />\n" +
                "                                                                    <br />\n" +
                "                                                                    Once\n" +
                "                                                                    verified,\n" +
                "                                                                    you'll have\n" +
                "                                                                    full access\n" +
                "                                                                    to all our\n" +
                "                                                                    features.</span\n" +
                "                                                                  >\n" +
                "                                                                </div>\n" +
                "                                                                <div\n" +
                "                                                                  style=\"\n" +
                "                                                                    font-family: inherit;\n" +
                "                                                                    text-align: center;\n" +
                "                                                                  \"\n" +
                "                                                                >\n" +
                "                                                                  <span\n" +
                "                                                                    style=\"\n" +
                "                                                                      color: #0aad5b;\n" +
                "                                                                      font-size: 18px;\n" +
                "                                                                      font-weight: 600;\n" +
                "                                                                    \"\n" +
                "                                                                  >\n" +
                "                                                                    Thank\n" +
                "                                                                    you!&nbsp;\n" +
                "                                                                  </span>\n" +
                "                                                                </div>\n" +
                "                                                                <div></div>\n" +
                "                                                              </div>\n" +
                "                                                            </td>\n" +
                "                                                          </tr>\n" +
                "                                                        </tbody>\n" +
                "                                                      </table>\n" +
                "                                                      <table\n" +
                "                                                        class=\"module\"\n" +
                "                                                        role=\"module\"\n" +
                "                                                        data-type=\"spacer\"\n" +
                "                                                        cellpadding=\"0\"\n" +
                "                                                        cellspacing=\"0\"\n" +
                "                                                        width=\"100%\"\n" +
                "                                                        style=\"\n" +
                "                                                          table-layout: fixed;\n" +
                "                                                        \"\n" +
                "                                                        data-muid=\"7770fdab-634a-4f62-a277-1c66b2646d8d\"\n" +
                "                                                      >\n" +
                "                                                        <tbody>\n" +
                "                                                          <tr>\n" +
                "                                                            <td\n" +
                "                                                              style=\"\n" +
                "                                                                padding: 0px 0px\n" +
                "                                                                  0px 0px;\n" +
                "                                                              \"\n" +
                "                                                              role=\"module-content\"\n" +
                "                                                            ></td>\n" +
                "                                                          </tr>\n" +
                "                                                        </tbody>\n" +
                "                                                      </table>\n" +
                "                                                      <table\n" +
                "                                                        bgcolor=\"#FFF\"\n" +
                "                                                        cellpadding=\"0\"\n" +
                "                                                        cellspacing=\"0\"\n" +
                "                                                        class=\"module\"\n" +
                "                                                        data-role=\"module-button\"\n" +
                "                                                        data-type=\"button\"\n" +
                "                                                        role=\"module\"\n" +
                "                                                        style=\"\n" +
                "                                                          padding-top: 20px;\n" +
                "                                                          padding-bottom: 50px;\n" +
                "                                                          table-layout: fixed;\n" +
                "                                                        \"\n" +
                "                                                        width=\"100%\"\n" +
                "                                                        data-muid=\"d050540f-4672-4f31-80d9-b395dc08abe1\"\n" +
                "                                                      >\n" +
                "                                                        <tbody>\n" +
                "                                                          <tr>\n" +
                "                                                            <td\n" +
                "                                                              align=\"center\"\n" +
                "                                                              class=\"outer-td\"\n" +
                "                                                            >\n" +
                "                                                              <table\n" +
                "                                                                border=\"0\"\n" +
                "                                                                cellpadding=\"0\"\n" +
                "                                                                cellspacing=\"0\"\n" +
                "                                                                class=\"wrapper-mobile\"\n" +
                "                                                                style=\"\n" +
                "                                                                  text-align: center;\n" +
                "                                                                \"\n" +
                "                                                              >\n" +
                "                                                                <tbody>\n" +
                "                                                                  <tr>\n" +
                "                                                                    <td\n" +
                "                                                                      align=\"center\"\n" +
                "                                                                      bgcolor=\"#0AAD5B\"\n" +
                "                                                                      class=\"inner-td\"\n" +
                "                                                                      style=\"\n" +
                "                                                                        border-radius: 6px;\n" +
                "                                                                        font-size: 16px;\n" +
                "                                                                        text-align: center;\n" +
                "                                                                        background-color: inherit;\n" +
                "                                                                      \"\n" +
                "                                                                    >\n" +
                "                                                                      <div\n" +
                "                                                                        style=\"\n" +
                "                                                                          background-color: #0aad5b;\n" +
                "                                                                          color: #000000;\n" +
                "                                                                          display: block;\n" +
                "\n" +
                "                                                                          padding: 12px\n" +
                "                                                                            32px\n" +
                "                                                                            12px\n" +
                "                                                                            40px;\n" +
                "                                                                          text-align: center;\n" +
                "                                                                        \"\n" +
                "                                                                        target=\"_blank\"\n" +
                "                                                                      >\n" +
                "                                                                        <p\n" +
                "                                                                          style=\"\n" +
                "                                                                            font-size: 28px;\n" +
                "                                                                            letter-spacing: 8px;\n" +
                "                                                                            margin: 0;\n" +
                "                                                                            padding: 0;\n" +
                "                                                                          \"\n" +
                "                                                                        >\n" + token + "\n" +
                "                                                                        </p>\n" +
                "                                                                      </div>\n" +
                "                                                                      <small\n" +
                "                                                                        ><p\n" +
                "                                                                          style=\"\n" +
                "                                                                            color: #5d5d5d;\n" +
                "                                                                            margin-top: 5px;\n" +
                "                                                                          \"\n" +
                "                                                                        >\n" +
                "                                                                          Link\n" +
                "                                                                          will\n" +
                "                                                                          expire\n" +
                "                                                                          in 60\n" +
                "                                                                          minutes.\n" +
                "                                                                        </p></small\n" +
                "                                                                      >\n" +
                "                                                                    </td>\n" +
                "                                                                  </tr>\n" +
                "                                                                </tbody>\n" +
                "                                                              </table>\n" +
                "                                                            </td>\n" +
                "                                                          </tr>\n" +
                "                                                        </tbody>\n" +
                "                                                      </table>\n" +
                "                                                    </td>\n" +
                "                                                  </tr>\n" +
                "                                                </tbody>\n" +
                "                                                <table\n" +
                "                                                  cellpadding=\"0\"\n" +
                "                                                  cellspacing=\"0\"\n" +
                "                                                  class=\"module\"\n" +
                "                                                  data-role=\"module-button\"\n" +
                "                                                  data-type=\"button\"\n" +
                "                                                  role=\"module\"\n" +
                "                                                  style=\"table-layout: fixed\"\n" +
                "                                                  width=\"100%\"\n" +
                "                                                  data-muid=\"550f60a9-c478-496c-b705-077cf7b1ba9a\"\n" +
                "                                                >\n" +
                "                                                  <tbody>\n" +
                "                                                    <tr>\n" +
                "                                                      <td\n" +
                "                                                        align=\"center\"\n" +
                "                                                        bgcolor=\"\"\n" +
                "                                                        class=\"outer-td\"\n" +
                "                                                      >\n" +
                "                                                        <table\n" +
                "                                                          border=\"0\"\n" +
                "                                                          cellpadding=\"20\"\n" +
                "                                                          cellspacing=\"0\"\n" +
                "                                                          class=\"wrapper-mobile\"\n" +
                "                                                          style=\"\n" +
                "                                                            text-align: center;\n" +
                "                                                          \"\n" +
                "                                                        >\n" +
                "                                                          <tbody>\n" +
                "                                                            <tr>\n" +
                "                                                              <td\n" +
                "                                                                class=\"inner-td\"\n" +
                "                                                                style=\"\n" +
                "                                                                  border-radius: 22px;\n" +
                "                                                                  font-size: 16px;\n" +
                "                                                                  text-align: center;\n" +
                "                                                                  background-color: inherit;\n" +
                "                                                                  font-family: inherit;\n" +
                "                                                                \"\n" +
                "                                                              >\n" +
                "                                                                <a\n" +
                "                                                                  href=\"https://lensly.io/\"\n" +
                "                                                                  style=\"\n" +
                "                                                                    color: #0aad5b;\n" +
                "                                                                    display: inline-block;\n" +
                "                                                                    font-size: 18px;\n" +
                "                                                                    font-weight: small;\n" +
                "                                                                    letter-spacing: 5px;\n" +
                "                                                                    line-height: normal;\n" +
                "                                                                    padding: 5px\n" +
                "                                                                      18px;\n" +
                "                                                                    text-align: center;\n" +
                "                                                                    text-decoration: none;\n" +
                "                                                                  \"\n" +
                "                                                                  target=\"_blank\"\n" +
                "                                                                  rel=\"noreferrer\"\n" +
                "                                                                  >LENSLY</a\n" +
                "                                                                >\n" +
                "                                                              </td>\n" +
                "                                                            </tr>\n" +
                "                                                          </tbody>\n" +
                "                                                        </table>\n" +
                "                                                      </td>\n" +
                "                                                    </tr>\n" +
                "                                                  </tbody>\n" +
                "                                                </table>\n" +
                "                                              </table>\n" +
                "                                            </td>\n" +
                "                                          </tr>\n" +
                "                                        </tbody>\n" +
                "                                      </table>\n" +
                "                                    </td>\n" +
                "                                  </tr>\n" +
                "                                </tbody>\n" +
                "                              </table>\n" +
                "                            </td>\n" +
                "                          </tr>\n" +
                "                        </tbody>\n" +
                "                      </table>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody>\n" +
                "              </table>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody>\n" +
                "      </table>\n" +
                "    </div> ";
    }
    private Boolean sendToken(User user, Token token) {
        if(token instanceof VerificationToken) emailService.send(user.getEmail(), "Welcome to Lensa!", buildEmail(user.getName(), token.getToken()));
        if(token instanceof PasswordResetToken) emailService.send(user.getEmail(), "Welcome to Lensa [PASSWORD-RESET]!", buildEmail(user.getName(), token.getToken()));
        return true;
    }
}
