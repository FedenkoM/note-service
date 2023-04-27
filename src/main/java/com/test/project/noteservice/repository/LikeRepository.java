package com.test.project.noteservice.repository;

import com.test.project.noteservice.entity.Like;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LikeRepository extends MongoRepository<Like, Long> {
    Optional<Like> findByNoteIdAndUserId(Long noteId, Long userId);
    int countByNoteId(Long noteId);
}
