package com.example.demo.fingaurd_ai.repository;


import com.example.demo.fingaurd_ai.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
