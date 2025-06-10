package com.example.intershop.service;

import com.example.intershop.model.Account;
import com.example.intershop.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

@Service
public class TransactionManagerService {
    private final PlatformTransactionManager transactionManager;
    private final AccountRepository accountRepository;
    private final TransactionTemplate transactionTemplate;

    public TransactionManagerService(PlatformTransactionManager transactionManager,
                                     AccountRepository accountRepository) {
        this.transactionManager = transactionManager;
        this.accountRepository = accountRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    public void transfer(Account source, Account target, BigDecimal amount) {
        transactionTemplate.executeWithoutResult(status -> {
            // Увеличиваем баланс получателя и сохраняем
            target.setBalance(target.getBalance().add(amount));
            accountRepository.save(target);

            // Уменьшаем баланс отправителя и сохраняем
            source.setBalance(source.getBalance().subtract(amount));
            accountRepository.save(source);
        });
    }
}