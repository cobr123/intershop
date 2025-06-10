package com.example.intershop.repository;

import com.example.intershop.IntershopApplication;
import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.model.Account;
import com.example.intershop.service.AccountService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IntershopApplication.class)
@Import(IntershopApplicationTests.class)
public class AccountServiceTest   {

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountDao accountDao;

  @Test
  public void testSuccessfullSqlQueries() {
      // Инициализируем пользователей (изначальный баланс — 10000)
      accountDao.create("Пётр");
      accountDao.create("Василий");
      var petrAccount = accountDao.findByName("Пётр");
      var vasilyAccount = accountDao.findByName("Василий");
      var initialBalance = petrAccount.getBalance();

      // Переводим от Василия Петру 100000 (возникает ошибка ограничения на баланс)
      Assertions.assertThrows(
          DataIntegrityViolationException.class,
          () -> accountService.transfer(vasilyAccount, petrAccount, BigDecimal.valueOf(100_000L))
      );

      // Проверяем, что транзакция откатилась
      // Не должно возникнуть ситуации, что Петру деньги начислились, а с Василия не списались
      assertThat(accountDao.findAll())
          .isNotEmpty()
          .withFailMessage("При возникновении ошибки во время транзакции " +
              "балансы обоих пользователей должны вернуться к изначальным значениям")
          .map(Account::getBalance)
          .allMatch(it -> it.compareTo(initialBalance) == 0);
  }
}