package ru.maliutin.diesel.repository;

import ru.maliutin.diesel.entity.Product;

import java.sql.SQLException;
import java.util.List;

public interface iProductRepository {
    /**
     * Получение товара по id.
     * @param productId идентификатор товара.
     * @return сущность товара.
     * @throws SQLException исключения при работе с БД.
     */
    Product findProductById(long productId) throws SQLException;

    /**
     * Получение всех товаров.
     * @return список товаров.
     * @throws SQLException исключения при работе с БД.
     */
    List<Product> findAllProduct() throws SQLException;

    /**
     * Сохранение нового товара.
     * @param product сущность товара.
     * @return сохраненный товар.
     * @throws SQLException исключения при работе с БД.
     */
    Product create(Product product) throws SQLException;

    /**
     * Изменение существующего товара.
     * @param productId идентификатор товара.
     * @param product сущность товара.
     * @return измененный товар.
     * @throws SQLException исключения при работе с БД.
     */
    Product edit(long productId, Product product) throws SQLException;

    /**
     * Удаление товара.
     * @param productId идентификатор товара.
     */
    void deleteById(long productId) throws SQLException;
}
