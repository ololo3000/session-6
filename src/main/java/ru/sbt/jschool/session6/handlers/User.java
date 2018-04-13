package ru.sbt.jschool.session6.handlers;

import java.io.Serializable;
import java.math.BigDecimal;

public class User implements Serializable {
    private int age;
    private String name;
    private BigDecimal salary;

    public User(int age, String name, BigDecimal salary) {
        this.age = age;
        this.name = name;
        this.salary = salary;
    }
}
