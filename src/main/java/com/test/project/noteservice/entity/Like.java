package com.test.project.noteservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "likes")
public class Like {
    @Transient
    public static final String SEQUENCE_NAME = "likes_sequence";

    @Id
    private Long id;

    private Long noteId;

    private Long userId;
}
