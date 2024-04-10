package ru.maliutin.diesel.utils.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import ru.maliutin.diesel.dto.ProductDTO;
import ru.maliutin.diesel.exception.ValidationProductException;

import java.util.Set;

/**
 * Валидация товара.
 */
public class ProductValidation implements iValidationService<ProductDTO>{

    @Override
    public void validation(ProductDTO objectValidation) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(objectValidation);
        if (!violations.isEmpty()){
            throw new ValidationProductException("Validation filed!", violations
                    .stream().map(ConstraintViolation::getMessage).toList());
        }
    }
}
