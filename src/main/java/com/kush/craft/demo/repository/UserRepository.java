package com.kush.craft.demo.repository;

import com.kush.craft.demo.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

}
