package com.test.project.noteservice.security;

import com.test.project.noteservice.entity.Role;
import com.test.project.noteservice.entity.User;
import com.test.project.noteservice.repository.UserRepository;
import com.test.project.noteservice.utils.sequence.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class UserManager implements UserDetailsManager {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SequenceGeneratorService sequenceGenerator;

    @Override
    public void createUser(UserDetails user) {
        var userToSave = (User) user;
        userToSave.addRole(Role.ROLE_USER);
        userToSave.setPassword(passwordEncoder.encode(user.getPassword()));
        userToSave.setId(sequenceGenerator.generateSequence(User.SEQUENCE_NAME));
        userRepository.save(userToSave);
    }

    @Override
    public void updateUser(UserDetails user) {
        var updatedUser = (User) user;
        userRepository.save(updatedUser);
    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByEmail(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        MessageFormat.format("username {0} not found", username)
                ));
    }
}