package com.example.intershop.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table(name = "users")
public class UserUi {
    @Id
    private Long id;
    @Column
    private String name;
    @Column
    private String password;
    @Column
    private BigDecimal balance;

    public UserUi() {
    }

    public UserUi(Long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public UserUi(String name, String password, BigDecimal balance) {
        this.name = name;
        this.password = password;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
