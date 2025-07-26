package com.example.intershop.repository;

import com.example.intershop.model.UserBalance;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserBalanceRepository extends ReactiveCrudRepository<UserBalance, Long> {
    @Query("select u.id, u.balance from users u where u.name = :userName")
    Mono<UserBalance> findByUserName(String userName);
}
