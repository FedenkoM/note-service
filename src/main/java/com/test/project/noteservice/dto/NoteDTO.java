package com.test.project.noteservice.dto;

import lombok.Data;

@Data
public class NoteDTO {
    private String content;
    private Long userId;
}
