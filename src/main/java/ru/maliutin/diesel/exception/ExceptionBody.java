package ru.maliutin.diesel.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Обертка ответов об исключениях.
 */
@Getter
@Setter
@AllArgsConstructor
public class ExceptionBody {

    private String message;


}
