package com.test.project.noteservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.project.noteservice.entity.Role;
import com.test.project.noteservice.entity.User;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtToUserConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    @Setter(onMethod_ = @Autowired)
    private ObjectMapper objectMapper;
    @Override
    @SneakyThrows
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        User user = new User();
        user.setEmail(jwt.getSubject());
        user.setId(jwt.getClaim("id"));

        List<Role> role = Arrays.asList(objectMapper.readValue(jwt.getClaims()
                        .get("role").toString(), Role[].class));
        user.addRoles(role);
        return new UsernamePasswordAuthenticationToken(user, jwt, user.getAuthorities());
    }
}