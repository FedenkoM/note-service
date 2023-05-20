package com.test.project.noteservice.event.listener;

import com.test.project.noteservice.entity.User;
import com.test.project.noteservice.event.RegistrationCompleteEvent;
import com.test.project.noteservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements
        ApplicationListener<RegistrationCompleteEvent> {

    private static final String VERIFICATION_URL = "%s/register/verifyRegistration?token=%s";
    private final UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        //Create the Verification Token for the User with Link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token,user);
        //Send Mail to user
        String url = String.format(VERIFICATION_URL, event.getApplicationUrl(), token);

        //sendVerificationEmail()
        log.info("Click the link to verify your account: {}", url);
    }
}