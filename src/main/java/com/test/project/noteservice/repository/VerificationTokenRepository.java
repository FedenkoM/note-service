package com.test.project.noteservice.repository;

import com.test.project.noteservice.entity.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
