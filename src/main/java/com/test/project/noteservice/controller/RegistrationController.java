package com.test.project.noteservice.controller;

import com.test.project.noteservice.dto.PasswordDTO;
import com.test.project.noteservice.dto.SignupDTO;
import com.test.project.noteservice.entity.User;
import com.test.project.noteservice.entity.VerificationToken;
import com.test.project.noteservice.event.RegistrationCompleteEvent;
import com.test.project.noteservice.exception.NotFoundException;
import com.test.project.noteservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String registerUser(@RequestBody SignupDTO userModel, final HttpServletRequest request) {
        var optionalUser = userService.findByEmail(userModel.email());
        var user = optionalUser.orElseGet(() -> userService.registerUser(userModel));
        if (user.isEnabled()) {
            return String.format("User with email: [%s] already registered in application", user.getEmail());
        }
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)
        ));
        return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User Verified Successfully";
        }
        return "Bad User";
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken,
                                          HttpServletRequest request) {
        var verificationToken = userService.generateNewVerificationToken(oldToken);
        User user = verificationToken.getUser();
        resendVerificationTokenMail(user, applicationUrl(request), verificationToken);
        return "Verification Link Sent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordDTO passwordDTO, HttpServletRequest request) {
        var user = userService.findByEmail(passwordDTO.email());
        String url = "";
        if (user.isPresent()) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user.get(), token);
            url = passwordResetTokenMail(user.get(), applicationUrl(request), token);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token,
                               @RequestBody PasswordDTO passwordDTO) {
        log.info("New password was saved.");
        return userService.saveNewPassword(token, passwordDTO);
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordDTO passwordDTO) {
        User user = userService.findByEmail(passwordDTO.email())
                .orElseThrow(() ->
                        new NotFoundException("User not found with email: " + passwordDTO.email()));
        if (!userService.checkIfValidOldPassword(user, passwordDTO.oldPassword())) {
            return "Invalid Old Password";
        }
        //Save New Password
        userService.changePassword(user, passwordDTO.newPassword());
        return "Password Changed Successfully";
    }

    private String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url = applicationUrl + "register/savePassword?token=" + token;
        //sendVerificationEmail()
        log.info("Click the link to Reset your Password: {}", url);
        return url;
    }

    private void resendVerificationTokenMail(User user, String applicationUrl, VerificationToken verificationToken) {
        String url = applicationUrl + "/register/verifyRegistration?token=" + verificationToken.getToken();
        //sendVerificationEmail()
        log.info("Click the link to verify your account: {}",
                url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}