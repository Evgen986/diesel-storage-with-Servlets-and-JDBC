package ru.maliutin.diesel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Объект передачи данных товара.
 */

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ProductDTO implements Serializable {

    @NotNull(message = "Название товара не может быть пустым!")
    @Size(min = 3, message = "Длинна названия техники, должна быть в диапазоне от 3 до 200 символов!")
    private String title;
    private String catalogueNumber;
    @NotNull(message = "Внутренний номер не может быть пустым!")
    @Min(value = 1, message = "Внутренний номер должен быть больше нуля!")
    private int programNumber;
    private List<TechnicDTO> technics;
    @Min(value = 0, message = "Остаток товара должен быть больше или равен нулю!")
    private int balance;
    @Min(value = 1, message = "Цена товара должна быть больше нуля!")
    private BigDecimal price;

}
