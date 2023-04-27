package com.test.project.noteservice.repository;

import com.test.project.noteservice.entity.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface NoteRepository extends MongoRepository<Note, Long> {
    List<Note> findAllByOrderByCreatedDateDesc();
}
