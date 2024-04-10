package ru.maliutin.diesel.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сущность товара.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Product {

    private long productId;
    private String title;
    private String catalogueNumber;
    private int programNumber;
    private List<Technic> technics;
    private int balance;
    private BigDecimal price;
}
