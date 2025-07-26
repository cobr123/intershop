package com.example.intershop.repository;

import com.example.intershop.model.UserBalance;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBalanceRepository extends ReactiveCrudRepository<UserBalance, Long> {
}
