package ru.maliutin.diesel.repository;

import ru.maliutin.diesel.entity.Product;
import ru.maliutin.diesel.entity.Technic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Маппер преобразования ответов БД в сущности товара.
 */
public class ProductMapper {
    /**
     * Преобразование в сущность товара.
     * @param rs результат запроса к БД.
     * @return optional, который может содержать объект товара.
     * @throws SQLException исключения при при обработке ответа из БД.
     */
    public Optional<Product> toEntity (ResultSet rs) throws SQLException {
        Product product = null;
        while (rs.next()){
            if (product == null){
                product = new Product();
                product.setProductId(rs.getLong("product_id"));
                product.setTitle(rs.getString("c_title"));
                product.setCatalogueNumber(rs.getString("c_catalogue_number"));
                product.setProgramNumber(rs.getInt("c_program_number"));
                product.setBalance(rs.getInt("balance"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setTechnics(new ArrayList<>());
            }
            String technicTitle = rs.getString("technic_title");
            if (technicTitle != null){
                product.getTechnics().add(new Technic(technicTitle));
            }
        }
        if (product == null)
            return Optional.empty();
        return Optional.of(product);
    }

    /**
     * Преобразование в список товаров.
     * @param rs результат запроса к БД.
     * @return список товаров.
     * @throws SQLException исключения при обработке ответа из БД.
     */
    public List<Product> toListEntity(ResultSet rs) throws SQLException{
        Map<Long, Product> products = new HashMap<>();
        while (rs.next()){
            Long productId = rs.getLong("product_id");
            if (products.containsKey(productId)){
                products.get(productId)
                        .getTechnics()
                        .add(new Technic(
                                rs.getString("technic_title")));
            }else {
                Product product = new Product();
                product.setProductId(productId);
                product.setTitle(rs.getString("c_title"));
                product.setCatalogueNumber(rs.getString("c_catalogue_number"));
                product.setProgramNumber(rs.getInt("c_program_number"));
                product.setBalance(rs.getInt("balance"));
                product.setPrice(rs.getBigDecimal("price"));

                List<Technic> technics = new ArrayList<>();
                technics.add(new Technic(rs.getString("technic_title")));
                product.setTechnics(technics);
                products.put(productId, product);
            }
        }
        return products.values().stream().toList();
    }

}
