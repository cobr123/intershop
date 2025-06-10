package com.example.intershop.repository;

import com.example.intershop.IntershopApplication;
import com.example.intershop.IntershopApplicationTests;
import com.example.intershop.model.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IntershopApplication.class)
@Import(IntershopApplicationTests.class)
public class CrudOperationsTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testCreate() {
        var anatoly = accountRepository.save(new Account("Анатолий"));

        assertThat(anatoly)
                .isNotNull()
                .withFailMessage("Созданной записи должен был быть присвоен ID")
                .extracting(Account::getId)
                .isNotNull();
    }

    @Test
    public void testDelete() {
        var mariana = accountRepository.save(new Account("Мариана"));
        accountRepository.delete(mariana);

        assertThat(accountRepository.existsById(mariana.getId()))
                .withFailMessage("Удалённая запись не должна быть найдена")
                .isFalse();
    }
}