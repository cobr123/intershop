package com.example.intershop.service;

import com.example.intershop.model.User;
import com.example.intershop.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Cacheable(value = "users", key = "#name")
    public Mono<User> findByName(String name) {
        return repository.findByName(name);
    }

    @CachePut(value = "users", key = "#user.name")
    public Mono<User> insert(User user) {
        return repository.save(user);
    }

    @CachePut(value = "users", key = "#user.name")
    public Mono<User> update(User user) {
        return repository.save(user);
    }

    @CacheEvict(value = "users", key = "#result.name")
    public Mono<User> deleteById(Long id) {
        return repository.findById(id)
                .flatMap(u -> repository.deleteById(id).thenReturn(u));
    }
}
