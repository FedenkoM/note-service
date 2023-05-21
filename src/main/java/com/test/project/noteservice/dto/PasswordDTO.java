package com.test.project.noteservice.dto;

public record PasswordDTO(String email,
                          String oldPassword,
                          String newPassword) {
}