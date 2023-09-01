package com.kush.mongodb.repository;

import com.kush.mongodb.entity.Comment;
import com.kush.mongodb.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

}
