package ru.maliutin.diesel.service;


import ru.maliutin.diesel.dto.ProductDTO;
import ru.maliutin.diesel.entity.Product;

import java.sql.SQLException;
import java.util.List;

public interface iProductService {
    /**
     * Получение товара по идентификатору.
     * @param id уникальный идентификатор товара.
     * @return объект товара.
     */
    ProductDTO getProductById(long id) throws SQLException;

    /**
     * Получение всех товаров.
     * @return список товаров.
     */
    List<ProductDTO> getAllProduct() throws SQLException;

    /**
     * Добавление нового товара.
     * @param productDTO объект передачи данных.
     * @return созданный товар.
     */
    ProductDTO addProduct(ProductDTO productDTO) throws SQLException;

    /**
     * Изменение товара.
     * @productId идентификатор товара.
     * @param productDTO объект передачи данных.
     * @return измененный товар.
     */
    ProductDTO editProduct(long productId, ProductDTO productDTO) throws SQLException;

    /**
     * Удаление товара.
     * @param id идентификатор товара.
     */
    void deleteProduct(long id) throws SQLException;
}
