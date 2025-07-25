package com.example.intershop.service;

import com.example.intershop.model.UserBalance;
import com.example.intershop.repository.UserBalanceRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserBalanceService {

    private final UserBalanceRepository repository;

    public UserBalanceService(UserBalanceRepository repository) {
        this.repository = repository;
    }

    public Mono<UserBalance> findByUserName(String userName) {
        return repository.findByUserName(userName);
    }

    public Mono<UserBalance> save(UserBalance userBalance) {
        return repository.save(userBalance);
    }
}
