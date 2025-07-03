package com.example.intershop.api;

import com.example.intershop.domain.BalanceGet200Response;
import com.example.intershop.domain.BalancePostRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

@Controller
public class BalanceApiImpl implements BalanceApi {
    private final AtomicReference<BigDecimal> balanceRef = new AtomicReference<>(new BigDecimal(1000));

    @Override
    public Mono<ResponseEntity<BalanceGet200Response>> balanceGet(ServerWebExchange exchange) {
        var body = new BalanceGet200Response();
        body.setBalance(balanceRef.get());
        return Mono.just(ResponseEntity.ok(body));
    }

    @Override
    public Mono<ResponseEntity<Void>> balancePost(Mono<BalancePostRequest> balancePostRequest, ServerWebExchange exchange) {
        return balancePostRequest.map(sumObj -> {
            var oldValue = balanceRef.getAndUpdate(balance -> {
                if (balance.compareTo(sumObj.getSum()) >= 0) {
                    return balance.subtract(sumObj.getSum());
                } else {
                    return balance;
                }
            });
            if (oldValue.compareTo(sumObj.getSum()) >= 0) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        });
    }
}
