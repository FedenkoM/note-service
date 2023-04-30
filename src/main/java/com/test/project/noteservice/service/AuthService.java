package com.test.project.noteservice.service;

import com.test.project.noteservice.dto.LoginDTO;
import com.test.project.noteservice.dto.SignupDTO;
import com.test.project.noteservice.dto.TokenDTO;
import com.test.project.noteservice.entity.User;
import com.test.project.noteservice.exception.NotFoundException;
import com.test.project.noteservice.repository.UserRepository;
import com.test.project.noteservice.security.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserDetailsManager userDetailsManager;
    private final TokenGenerator tokenGenerator;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    @Qualifier("jwtRefreshTokenAuthProvider")
    private final JwtAuthenticationProvider refreshTokenAuthProvider;
    private final UserRepository userRepository;

    public TokenDTO registerUser(SignupDTO signupDTO) {
        var user = new User();
        user.setEmail(signupDTO.email());
        user.setPassword(signupDTO.password());
        user.setFirstName(signupDTO.firstName());
        user.setLastName(signupDTO.lastName());

        userDetailsManager.createUser(user);
        var authentication = UsernamePasswordAuthenticationToken.authenticated(user, signupDTO.password(), user.getAuthorities());
        return tokenGenerator.createToken(authentication);
    }

    public TokenDTO loginUser(LoginDTO loginDTO) {
        if (userRepository.existsByEmail(loginDTO.email())) {
            var authentication = daoAuthenticationProvider
                    .authenticate(UsernamePasswordAuthenticationToken
                            .unauthenticated(loginDTO.email(), loginDTO.password()));
            return tokenGenerator.createToken(authentication);
        }
        throw new NotFoundException("Not registered User: " + loginDTO.email());
    }
}
