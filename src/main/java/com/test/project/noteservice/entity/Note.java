package com.test.project.noteservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Document(collection = "notes")
public class Note {
    @Transient
    public static final String SEQUENCE_NAME = "notes_sequence";

    @Id
    private Long id;

    private String content;

    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime lastModifiedDate;

    @Setter(AccessLevel.PRIVATE)
    private int likes;

    private Long userId;

    public void incrementLikes() {
        likes++;
    }

    public void decrementLikes() {
        if (likes > 0) {
            likes--;
        }
    }
}
