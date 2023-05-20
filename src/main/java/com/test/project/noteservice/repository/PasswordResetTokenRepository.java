package com.test.project.noteservice.repository;

import com.test.project.noteservice.entity.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken,Long> {
    Optional<PasswordResetToken> findByToken(String token);
}