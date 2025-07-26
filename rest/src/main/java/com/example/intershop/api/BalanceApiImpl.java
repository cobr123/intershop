package com.example.intershop.api;

import com.example.intershop.domain.BalanceGetRequest;
import com.example.intershop.domain.BalanceGet200Response;
import com.example.intershop.domain.BalancePostRequest;
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
    public Mono<ResponseEntity<BalanceGet200Response>> balanceGet(Mono<BalanceGetRequest> balanceGetRequest, ServerWebExchange exchange) {
        return balanceGetRequest
                .map(BalanceGetRequest::getId)
                .flatMap(userBalanceService::findById)
                .map(userBalance -> {
                    var body = new BalanceGet200Response();
                    body.setBalance(userBalance.getBalance());
                    return ResponseEntity.ok(body);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> balancePost(Mono<BalancePostRequest> balancePostRequest, ServerWebExchange exchange) {
        return balancePostRequest
                .flatMap(r -> userBalanceService.findById(r.getId()).zipWith(Mono.just(r)))
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
