package ru.maliutin.diesel.dto;

import ru.maliutin.diesel.entity.Product;

public interface iMapperDto {

    Product toEntity(ProductDTO productDTO);

    ProductDTO toDto(Product product);
}
