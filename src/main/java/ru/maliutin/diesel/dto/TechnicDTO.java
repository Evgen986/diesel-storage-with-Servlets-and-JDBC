package ru.maliutin.diesel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TechnicDTO(
        @NotNull(message = "Марка техники не может быть пустой!")
        @Size(min = 3, message = "Длинна названия техники, должна быть в диапазоне от 3 до 50 символов!")
        String title
) {
}
