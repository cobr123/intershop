package com.example.intershop.repository;

import com.example.intershop.model.UserBalance;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserBalanceRepository extends ReactiveCrudRepository<UserBalance, Long> {
    @Query("select ub.id, ub.user_id, ub.balance user_balance ub join users u on u.id = ub.user_id where u.name = :userName")
    Mono<UserBalance> findByUserName(String userName);
}
