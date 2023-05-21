package com.test.project.noteservice.service;

import com.test.project.noteservice.dto.PasswordDTO;
import com.test.project.noteservice.dto.SignupDTO;
import com.test.project.noteservice.entity.User;
import com.test.project.noteservice.entity.VerificationToken;

import java.util.Optional;

public interface UserService {
    User registerUser(SignupDTO userModel);

    String validateVerificationToken(String token);

    void saveVerificationTokenForUser(String token, User user);

    VerificationToken generateNewVerificationToken(String oldToken);

    Optional<User> findByEmail(String email);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    boolean checkIfValidOldPassword(User user, String oldPassword);

    String saveNewPassword(String token, PasswordDTO passwordDTO);

    void changePassword(User user, String newPassword);

    void createPasswordResetTokenForUser(User user, String token);
}
