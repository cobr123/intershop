package com.example.intershop.service;

import com.example.intershop.IntershopApplication;
import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.model.Account;
import com.example.intershop.repository.AccountRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IntershopApplication.class)
@Import(IntershopApplicationTests.class)
public class TransactionManagerServiceTest {

    @Autowired
    TransactionManagerService transactionManagerService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void testTransactionManager() {
        // Инициализируем пользователей (изначальный баланс — 10000)
        var petr = accountRepository.save(new Account("Пётр"));
        var vasily = accountRepository.save(new Account("Василий"));
        var initialBalance = petr.getBalance();

        // Переводим от Василия Петру 100000 (возникает ошибка ограничения на баланс)
        Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> transactionManagerService.transfer(vasily, petr, BigDecimal.valueOf(100_000L))
        );

        // Проверяем, что транзакция откатилась. Не возникло ситуации, что Петру деньги начислились, а с Василия не списались
        assertThat(accountRepository.findAllById(List.of(petr.getId(), vasily.getId())))
                .isNotEmpty()
                .withFailMessage("При возникновении ошибки во время транзакции " +
                        "балансы обоих пользователей должны вернуться к изначальным значениям")
                .map(Account::getBalance)
                .allMatch(it -> it.compareTo(initialBalance) == 0);
    }

}