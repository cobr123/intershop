package com.example.intershop.repository;

import com.example.intershop.model.Account;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public AccountDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void create(String name) {
        var account = new Account(name);
        namedParameterJdbcTemplate.update(
                "INSERT INTO account (name, balance) VALUES (:name, :balance)",
                new BeanPropertySqlParameterSource(account)
        );
    }

    public void update(Account account) {
        namedParameterJdbcTemplate.update(
                "UPDATE account SET name = :name, balance = :balance WHERE id = :id",
                new BeanPropertySqlParameterSource(account)
        );
    }

    public List<Account> findAll() {
        return namedParameterJdbcTemplate.query(
                "SELECT id, name, balance FROM account",
                (result, rowNum) -> new Account(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getBigDecimal("balance")
                )
        );
    }

    public Account findByName(String name) {
        return namedParameterJdbcTemplate.queryForObject(
                "SELECT id, name, balance FROM account WHERE name = :name",
                new MapSqlParameterSource("name", name),
                (result, rowNum) -> new Account(
                        result.getInt("id"),
                        result.getString("name"),
                        result.getBigDecimal("balance")
                )
        );
    }
}
