package com.test.project.noteservice.service;

import com.test.project.noteservice.dto.PasswordDTO;
import com.test.project.noteservice.dto.SignupDTO;
import com.test.project.noteservice.entity.PasswordResetToken;
import com.test.project.noteservice.entity.User;
import com.test.project.noteservice.entity.VerificationToken;
import com.test.project.noteservice.repository.PasswordResetTokenRepository;
import com.test.project.noteservice.repository.UserRepository;
import com.test.project.noteservice.repository.VerificationTokenRepository;
import com.test.project.noteservice.utils.sequence.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserDetailsManager userDetailsManager;
    private final SequenceGeneratorService sequenceGenerator;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        var verificationToken = new VerificationToken(user, token);
        verificationToken.setId(sequenceGenerator.generateSequence(VerificationToken.SEQUENCE_NAME));
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public User registerUser(SignupDTO userModel) {
        var user = new User();
        user.setEmail(userModel.email());
        user.setFirstName(userModel.firstName());
        user.setLastName(userModel.lastName());
        user.setPassword(userModel.password());
        userDetailsManager.createUser(user);
        return user;
    }

    @Override
    public String validateVerificationToken(String token) {
        var verificationToken = verificationTokenRepository.findByToken(token);
        if (Objects.isNull(verificationToken)) {
            return "invalid";
        }

        var user = verificationToken.getUser();
        var calendar = Calendar.getInstance();

        if (isVerificationTokenExpired(verificationToken, calendar)) {
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }
        user.setEnabled(true);
        userDetailsManager.updateUser(user);
        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        var verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        var passwordResetToken = new PasswordResetToken(user,token);
        passwordResetToken.setId(sequenceGenerator.generateSequence(PasswordResetToken.SEQUENCE_NAME));
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        var optionalPasswordResetToken = passwordResetTokenRepository.findByToken(token);

        if (optionalPasswordResetToken.isEmpty()) {
            return "invalid";
        }
        var passwordResetToken = optionalPasswordResetToken.get();

        Calendar calendar = Calendar.getInstance();

        if (isPasswordResetTokenExpired(passwordResetToken, calendar)) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "expired";
        }
        return "valid";
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        var optionalPasswordResetToken = passwordResetTokenRepository
                .findByToken(token)
                .orElseThrow();
        return Optional.ofNullable(optionalPasswordResetToken.getUser());
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public String saveNewPassword(String token, PasswordDTO passwordDTO) {
        String result = validatePasswordResetToken(token);
        if (!result.equalsIgnoreCase("valid")) {
            return "Invalid Token";
        }
        Optional<User> user = getUserByPasswordResetToken(token);
        if (user.isPresent()) {
            changePassword(user.get(), passwordDTO.newPassword());
            return "Password Reset Successfully";
        } else {
            return "Invalid Token";
        }
    }

    @Override
    public void changePassword(User user, String newPassword) {
        var encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userDetailsManager.updateUser(user);
    }

    private static boolean isVerificationTokenExpired(VerificationToken verificationToken, Calendar cal) {
        return (verificationToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0;
    }

    private static boolean isPasswordResetTokenExpired(PasswordResetToken passwordResetToken, Calendar cal) {
        return (passwordResetToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0;
    }

}
