package com.example.intershop.api;

import com.example.intershop.domain.*;
import com.example.intershop.service.UserBalanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
public class BalanceApiImpl implements BalanceApi {

    private final UserBalanceService userBalanceService;

    public BalanceApiImpl(UserBalanceService userBalanceService) {
        this.userBalanceService = userBalanceService;
    }

    @Override
    public Mono<ResponseEntity<BalanceUserIdGet200Response>> balanceUserIdGet(Long userId, ServerWebExchange exchange) {
        return userBalanceService.findById(userId)
                .map(userBalance -> {
                    var body = new BalanceUserIdGet200Response();
                    body.setBalance(userBalance.getBalance());
                    return ResponseEntity.ok(body);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> balanceUserIdPost(Long userId, Mono<BalanceUserIdPostRequest> balanceUserIdPostRequest, ServerWebExchange exchange) {
        return balanceUserIdPostRequest
                .flatMap(r -> userBalanceService.findById(userId).zipWith(Mono.just(r)))
                .flatMap(pair -> {
                    var userBalance = pair.getT1();
                    var sumObj = pair.getT2();
                    if (userBalance.getBalance().compareTo(sumObj.getSum()) >= 0) {
                        userBalance.setBalance(userBalance.getBalance().subtract(sumObj.getSum()));
                        return userBalanceService.save(userBalance)
                                .map(ub -> ResponseEntity.ok().build());
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build());
                    }
                });
    }
}
