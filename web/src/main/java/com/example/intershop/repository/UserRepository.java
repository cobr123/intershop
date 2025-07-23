package com.example.intershop.repository;

import com.example.intershop.model.UserUi;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserUi, Long> {
    Mono<UserUi> findByName(String name);
}
