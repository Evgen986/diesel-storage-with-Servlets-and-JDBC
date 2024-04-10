package ru.maliutin.diesel.repository;

import lombok.SneakyThrows;
import ru.maliutin.diesel.entity.Product;
import ru.maliutin.diesel.entity.Technic;
import ru.maliutin.diesel.exception.NoSuchProductException;

import java.sql.*;
import java.util.*;

/**
 * Репозиторий сущности товара.
 */
public class ProductRepositoryImpl implements iProductRepository {

    private final iDataBaseManager dataBaseManager;

    private final ProductMapper productMapper;

    public ProductRepositoryImpl(iDataBaseManager dataBaseManager, ProductMapper productMapper) {
        this.dataBaseManager = dataBaseManager;
        this.productMapper = productMapper;
    }

    /**
     * Получение товара по id.
     * @param productId идентификатор товара.
     * @return найденный товар.
     */
    @Override
    @SneakyThrows
    public Product findProductById(long productId) {
        try (Connection connection = dataBaseManager.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(SQL.FIND_PRODUCT_BY_ID.QUERY);
            ps.setLong(1, productId);
            ResultSet rs = ps.executeQuery();
            Optional<Product> product = productMapper.toEntity(rs);
            if (product.isEmpty())
                throw new NoSuchProductException("Product by id = %d not found!".formatted(productId));
            return product.get();
        }
    }

    /**
     * Получение всех товаров.
     * @return список товаров.
     */
    @Override
    @SneakyThrows
    public List<Product> findAllProduct() {
        try (Connection connection = dataBaseManager.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(SQL.FIND_ALL_PRODUCT.QUERY);
            ResultSet rs = ps.executeQuery();
            return productMapper.toListEntity(rs);
        }
    }

    /**
     * Сохранение нового товара.
     * @param product сущность товара.
     * @return сохраненный товар.
     */
    @Override
    @SneakyThrows
    public Product create(Product product) {
        try (Connection connection = dataBaseManager.getConnection()) {
            connection.setAutoCommit(false);
            Savepoint beforeSave = connection.setSavepoint();
            try {
                PreparedStatement productStatement = connection.prepareStatement(SQL.INSERT_PRODUCT.QUERY);
                PreparedStatement balanceStatement = connection.prepareStatement(SQL.INSERT_PRODUCT_BALANCE.QUERY);

                // Заполнение данных для запроса сохранения товара
                productStatement.setString(1, product.getTitle());
                productStatement.setString(2, product.getCatalogueNumber());
                productStatement.setInt(3, product.getProgramNumber());

                // Выполнение запроса для вставки товара и получение product_id
                ResultSet productResult = productStatement.executeQuery();
                if (productResult.next()) {
                    long productId = productResult.getLong("product_id");
                    product.setProductId(productId);

                    compareTechnic(connection, productId, product.getTechnics());

                    // Вставка записи в таблицу product_balance
                    balanceStatement.setLong(1, productId);
                    balanceStatement.setInt(2, product.getBalance());
                    balanceStatement.setBigDecimal(3, product.getPrice());
                    balanceStatement.executeUpdate();

                    connection.commit();
                    connection.setAutoCommit(true);
                }
            }catch (Exception e){
                connection.rollback(beforeSave);
                connection.setAutoCommit(true);
            }
        }
        return product;
    }

    /**
     * Изменение существующего товара.
     * @param productId идентификатор товара.
     * @param product сущность товара.
     * @return измененный товар.
     */
    @Override
    @SneakyThrows
    public Product edit(long productId, Product product) {
        try (Connection connection = dataBaseManager.getConnection()) {
            // Проверка существования товара
            findProductById(productId);
            connection.setAutoCommit(false);
            Savepoint beforeEditProduct = connection.setSavepoint();
            try {
                PreparedStatement productStatement = connection.prepareStatement(SQL.UPDATE_PRODUCT_BY_ID.QUERY);
                PreparedStatement balanceStatement = connection.prepareStatement(SQL.UPDATE_PRODUCT_BALANCE.QUERY);

                // Заполнение данных для запроса сохранения товара
                productStatement.setString(1, product.getTitle());
                productStatement.setString(2, product.getCatalogueNumber());
                productStatement.setInt(3, product.getProgramNumber());
                productStatement.setLong(4, productId);

                productStatement.executeUpdate();

                product.setProductId(productId);

                // Проверка применяемости техники
                compareTechnic(connection, productId, product.getTechnics());

                // Вставка записи в таблицу product_balance
                balanceStatement.setInt(1, product.getBalance());
                balanceStatement.setBigDecimal(2, product.getPrice());
                balanceStatement.setLong(3, productId);
                balanceStatement.executeUpdate();

                connection.commit();
                connection.setAutoCommit(true);
            }catch (Exception e){
                connection.rollback(beforeEditProduct);
                connection.setAutoCommit(true);
            }
        }
        return product;
    }

    /**
     * Удаление товара.
     * @param productId идентификатор товара.
     */
    @Override
    @SneakyThrows
    public void deleteById(long productId) {
        try (Connection connection = dataBaseManager.getConnection()) {
            PreparedStatement deleteStatement = connection.prepareStatement(SQL.DELETE_PRODUCT_BY_ID.QUERY);
            deleteStatement.setLong(1, productId);
            deleteStatement.execute();
        }
    }

    /**
     * Служебный метод проверки применяемости товара к технике.
     * @param connection объект соединения с БД.
     * @param productId идентификатор товара.
     * @param technics список техники товара.
     */
    @SneakyThrows
    private void compareTechnic(Connection connection, long productId, List<Technic> technics) {
        PreparedStatement compareStatement = connection.prepareStatement(SQL.FIND_APPLICABILITY_BY_PRODUCT_ID.QUERY);
        compareStatement.setLong(1, productId);
        Map<String, Long> productTechnics = new HashMap<>();
        ResultSet rs = compareStatement.executeQuery();
        while (rs.next()) {
            productTechnics.put(rs.getString("c_title"), rs.getLong("technic_id"));
        }
        // Проверяем имеющуюся технику у товара
        for (Technic technic : technics) {
            // Если техника не назначена товару
            if (!productTechnics.containsKey(technic.title())) {
                PreparedStatement addApplicability = connection.prepareStatement(SQL.INSERT_APPLICABILITY.QUERY);
                PreparedStatement getIdTechnic = connection.prepareStatement(SQL.FIND_TECHNIC_ID_BY_TITLE.QUERY);
                getIdTechnic.setString(1, technic.title());
                long technicId;
                ResultSet findTechnic = getIdTechnic.executeQuery();
                // Если техника существует
                if (findTechnic.next()) {
                    technicId = findTechnic.getLong("technic_id");
                } else {
                    // Иначе создаем новую технику
                    PreparedStatement createTechnic = connection.prepareStatement(SQL.INSERT_TECHNIC.QUERY);
                    createTechnic.setString(1, technic.title());
                    ResultSet idNewTechnic = createTechnic.executeQuery();
                    idNewTechnic.next();
                    technicId = idNewTechnic.getLong("technic_id");
                }
                addApplicability.setLong(1, productId);
                addApplicability.setLong(2, technicId);
                addApplicability.execute();
            }
            productTechnics.remove(technic.title());
        }
        // Проверяем возможное удаление ранее сохраненной техники
        for (Map.Entry<String, Long> technic : productTechnics.entrySet()) {
            PreparedStatement removeApplicability = connection.prepareStatement(SQL.DELETE_APPLICABILITY_BY_PRODUCT_ID_AND_TECHNIC_ID.QUERY);
            removeApplicability.setLong(1, productId);
            removeApplicability.setLong(2, technic.getValue());
            removeApplicability.execute();
        }
    }

    public enum SQL {
        DELETE_APPLICABILITY_BY_PRODUCT_ID_AND_TECHNIC_ID("""
                DELETE FROM product.t_applicability
                WHERE product_id = ? AND technic_id = ?
                """),
        DELETE_PRODUCT_BY_ID("""
                DELETE FROM product.t_product
                WHERE product_id = ?
                """),
        INSERT_APPLICABILITY("""
                INSERT INTO product.t_applicability (product_id, technic_id)
                VALUES (?, ?)
                """),
        INSERT_PRODUCT("""
                INSERT INTO product.t_product (c_title, c_catalogue_number, c_program_number)
                VALUES (?, ?, ?) RETURNING product_id
                """),
        INSERT_PRODUCT_BALANCE("""
                INSERT INTO product.t_product_balance (product_id, balance, price)
                VALUES (?, ?, ?)
                """),
        INSERT_TECHNIC("""
                INSERT INTO product.t_technic (c_title)
                VALUES (?) RETURNING technic_id
                """),
        FIND_ALL_PRODUCT("""
                SELECT p.product_id, p.c_title, p.c_catalogue_number, p.c_program_number, b.balance, b.price, t.c_title AS technic_title
                FROM product.t_product p
                JOIN product.t_product_balance b ON p.product_id = b.product_id
                LEFT JOIN product.t_applicability a ON p.product_id = a.product_id
                LEFT JOIN product.t_technic t ON a.technic_id = t.technic_id
                """),
        FIND_APPLICABILITY_BY_PRODUCT_ID("""
                SELECT t.technic_id, c_title
                FROM product.t_applicability t JOIN product.t_technic tt on t.technic_id = tt.technic_id
                WHERE t.product_id = ?
                """),
        FIND_PRODUCT_BY_ID("""
                SELECT p.product_id, p.c_title, p.c_catalogue_number, p.c_program_number,
                                 b.balance, b.price, t.c_title AS technic_title
                                 FROM product.t_product p
                                 JOIN product.t_product_balance b ON p.product_id = b.product_id
                                 LEFT JOIN product.t_applicability a ON p.product_id = a.product_id
                                 LEFT JOIN product.t_technic t ON a.technic_id = t.technic_id
                                 WHERE p.product_id = ?
                """),
        FIND_TECHNIC_ID_BY_TITLE("""
                SELECT technic_id FROM product.t_technic
                WHERE c_title = ?
                """),

        UPDATE_PRODUCT_BY_ID("""
                UPDATE product.t_product SET c_title = ?, c_catalogue_number = ?, c_program_number = ?
                WHERE product_id = ?
                """),
        UPDATE_PRODUCT_BALANCE("""
                UPDATE product.t_product_balance SET balance = ?, price = ?
                WHERE product_id = ?
                """),

        CHECK_APPLICABILITY("""
                SELECT * FROM product.t_applicability
                WHERE product_id = ? AND technic_id = ?
                """);

        final String QUERY;

        SQL(String QUERY) {
            this.QUERY = QUERY;
        }

    }
}
