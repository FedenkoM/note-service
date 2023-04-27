package com.test.project.noteservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}