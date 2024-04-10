package ru.maliutin.diesel.repository;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.maliutin.diesel.entity.Product;
import ru.maliutin.diesel.entity.Technic;
import ru.maliutin.diesel.exception.NoSuchProductException;
import ru.maliutin.diesel.repository.DataBaseManager;
import ru.maliutin.diesel.repository.ProductMapper;
import ru.maliutin.diesel.repository.ProductRepositoryImpl;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static ru.maliutin.diesel.repository.ProductRepositoryImpl.SQL.*;

@ExtendWith(MockitoExtension.class)
public class ProductRepositoryImplTest {
    @Mock
    private DataBaseManager dataBaseManager;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement ps;
    @Mock
    private ResultSet rs;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private ProductRepositoryImpl productRepository;

    @Test
    @SneakyThrows
    public void findProductByIdExpectProduct(){
        long productId = 1;
        Product expectProduct = new Product();

        when(dataBaseManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(FIND_PRODUCT_BY_ID.QUERY)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(productMapper.toEntity(rs)).thenReturn(Optional.of(expectProduct));

        Product actualProduct = productRepository.findProductById(productId);

        assertEquals(expectProduct, actualProduct);

        verify(dataBaseManager).getConnection();
        verify(connection).prepareStatement(FIND_PRODUCT_BY_ID.QUERY);
        verify(ps).setLong(1, productId);
        verify(ps).executeQuery();
        verify(productMapper).toEntity(rs);
    }

    @Test
    @SneakyThrows
    public void findProductByIdExpectNoSuchProductException(){
        long productId = 1;

        when(dataBaseManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(FIND_PRODUCT_BY_ID.QUERY)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(productMapper.toEntity(rs)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchProductException.class, () ->
                productRepository.findProductById(productId));

        verify(dataBaseManager).getConnection();
        verify(connection).prepareStatement(FIND_PRODUCT_BY_ID.QUERY);
        verify(ps).setLong(1, productId);
        verify(ps).executeQuery();
        verify(productMapper).toEntity(rs);
    }

    @Test
    @SneakyThrows
    public void findAllProduct(){
        List<Product> expectProducts = List.of(new Product());

        when(dataBaseManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(FIND_ALL_PRODUCT.QUERY)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(productMapper.toListEntity(rs)).thenReturn(expectProducts);

        List<Product> actualProducts = productRepository.findAllProduct();

        assertEquals(expectProducts, actualProducts);

        verify(dataBaseManager).getConnection();
        verify(connection).prepareStatement(FIND_ALL_PRODUCT.QUERY);
        verify(ps).executeQuery();
        verify(productMapper).toListEntity(rs);
    }

    @Test
    @SneakyThrows
    public void createExpectProductExpectTechnicExist(){
        Product product = new Product();
        product.setTechnics(List.of(new Technic("МАЗ")));
        long productId = 1L;

        when(dataBaseManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(INSERT_PRODUCT.QUERY)).thenReturn(ps);
        when(connection.prepareStatement(INSERT_PRODUCT_BALANCE.QUERY)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getLong("product_id")).thenReturn(productId);
        when(connection.prepareStatement(FIND_APPLICABILITY_BY_PRODUCT_ID.QUERY)).thenReturn(ps);
        when(rs.getString("c_title")).thenReturn("МАЗ");
        when(rs.getLong("technic_id")).thenReturn(1L);

        Product expectProduct = new Product();
        expectProduct.setProductId(productId);
        expectProduct.setTechnics(List.of(new Technic("МАЗ")));

        Product actualProduct = productRepository.create(product);

        assertEquals(expectProduct, actualProduct);

        verify(dataBaseManager).getConnection();
        verify(connection).setAutoCommit(false);
        verify(connection).setSavepoint();
        verify(connection).prepareStatement(INSERT_PRODUCT.QUERY);
        verify(connection).prepareStatement(INSERT_PRODUCT_BALANCE.QUERY);
        verify(ps, times(2)).setString(any(Integer.class), any());
        verify(ps, times(2)).setInt(any(Integer.class), any(Integer.class));
        verify(ps, times(2)).executeQuery();
        verify(rs, times(3)).next();
        verify(rs, times(2)).getLong(any(String.class));
        verify(connection).prepareStatement(FIND_APPLICABILITY_BY_PRODUCT_ID.QUERY);
        verify(ps, times(2)).setLong(any(Integer.class), any(Long.class));
        verify(rs).getString(any(String.class));
        verify(ps).setBigDecimal(any(Integer.class), any());
        verify(ps).executeUpdate();
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
    }

    @Test
    @SneakyThrows
    public void createExpectProductExpectTechnicNotExist(){
        Product product = new Product();
        product.setTechnics(List.of(new Technic("New Technic")));
        long productId = 1L;

        when(dataBaseManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(INSERT_PRODUCT.QUERY)).thenReturn(ps);
        when(connection.prepareStatement(INSERT_PRODUCT_BALANCE.QUERY)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false, false, true);
        when(rs.getLong("product_id")).thenReturn(productId);
        when(connection.prepareStatement(FIND_APPLICABILITY_BY_PRODUCT_ID.QUERY)).thenReturn(ps);
        when(rs.getLong("technic_id")).thenReturn(1L);
        when(connection.prepareStatement(INSERT_APPLICABILITY.QUERY)).thenReturn(ps);
        when(connection.prepareStatement(FIND_TECHNIC_ID_BY_TITLE.QUERY)).thenReturn(ps);
        when(connection.prepareStatement(INSERT_TECHNIC.QUERY)).thenReturn(ps);



        Product expectProduct = new Product();
        expectProduct.setProductId(productId);
        expectProduct.setTechnics(List.of(new Technic("New Technic")));

        Product actualProduct = productRepository.create(product);

        assertEquals(expectProduct, actualProduct);

        verify(dataBaseManager).getConnection();
        verify(connection).setAutoCommit(false);
        verify(connection).setSavepoint();
        verify(connection).prepareStatement(INSERT_PRODUCT.QUERY);
        verify(connection).prepareStatement(INSERT_PRODUCT_BALANCE.QUERY);
        verify(ps, times(4)).setString(any(Integer.class), any());
        verify(ps, times(2)).setInt(any(Integer.class), any(Integer.class));
        verify(ps, times(4)).executeQuery();
        verify(rs, times(4)).next();
        verify(rs, times(2)).getLong(any(String.class));
        verify(connection).prepareStatement(FIND_APPLICABILITY_BY_PRODUCT_ID.QUERY);
        verify(ps, times(4)).setLong(any(Integer.class), any(Long.class));
        verify(ps).setBigDecimal(any(Integer.class), any());
        verify(ps).executeUpdate();
        verify(ps).execute();
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
    }

    @Test
    @SneakyThrows
    public void createExpectProductExpectTechnicDeleteFromProduct(){
        Product product = new Product();
        product.setTechnics(List.of(new Technic("Not delete technic")));
        long productId = 1L;

        when(dataBaseManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(INSERT_PRODUCT.QUERY)).thenReturn(ps);
        when(connection.prepareStatement(INSERT_PRODUCT_BALANCE.QUERY)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, true, false);
        when(rs.getString("c_title")).thenReturn("МАЗ").thenReturn("Not delete technic");
        when(rs.getLong("product_id")).thenReturn(productId);
        when(connection.prepareStatement(FIND_APPLICABILITY_BY_PRODUCT_ID.QUERY)).thenReturn(ps);
        when(rs.getLong("technic_id")).thenReturn(1L);
        when(connection.prepareStatement(DELETE_APPLICABILITY_BY_PRODUCT_ID_AND_TECHNIC_ID.QUERY)).thenReturn(ps);



        Product expectProduct = new Product();
        expectProduct.setProductId(productId);
        expectProduct.setTechnics(List.of(new Technic("Not delete technic")));

        Product actualProduct = productRepository.create(product);

        assertEquals(expectProduct, actualProduct);

        verify(dataBaseManager).getConnection();
        verify(connection).setAutoCommit(false);
        verify(connection).setSavepoint();
        verify(connection).prepareStatement(INSERT_PRODUCT.QUERY);
        verify(connection).prepareStatement(INSERT_PRODUCT_BALANCE.QUERY);
        verify(ps, times(2)).setString(any(Integer.class), any());
        verify(ps, times(2)).setInt(any(Integer.class), any(Integer.class));
        verify(ps, times(2)).executeQuery();
        verify(rs, times(4)).next();
        verify(rs, times(3)).getLong(any(String.class));
        verify(connection).prepareStatement(FIND_APPLICABILITY_BY_PRODUCT_ID.QUERY);
        verify(ps, times(4)).setLong(any(Integer.class), any(Long.class));
        verify(ps).setBigDecimal(any(Integer.class), any());
        verify(ps).executeUpdate();
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
    }

    @Test
    @SneakyThrows
    public void creatExpectExceptionAndRollback(){
        Product product = new Product();


        when(dataBaseManager.getConnection()).thenReturn(connection);
        doThrow(new SQLException()).when(connection).prepareStatement(INSERT_PRODUCT.QUERY);

        productRepository.create(product);

        verify(dataBaseManager).getConnection();
        verify(connection).setAutoCommit(false);
        verify(connection).setSavepoint();
        verify(connection).rollback(any());
        verify(connection).setAutoCommit(true);
    }

    @Test
    @SneakyThrows
    public void editProductExpectProduct(){
        long productId = 1L;
        Product editProduct = new Product();
        editProduct.setTechnics(List.of());
        Product baseProduct = new Product();

        when(dataBaseManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(FIND_PRODUCT_BY_ID.QUERY)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(productMapper.toEntity(rs)).thenReturn(Optional.of(baseProduct));

        when(connection.prepareStatement(UPDATE_PRODUCT_BY_ID.QUERY)).thenReturn(ps);
        when(connection.prepareStatement(UPDATE_PRODUCT_BALANCE.QUERY)).thenReturn(ps);
        when(connection.prepareStatement(FIND_APPLICABILITY_BY_PRODUCT_ID.QUERY)).thenReturn(ps);
        when(rs.next()).thenReturn(false);

        Product expectProduct = new Product();
        expectProduct.setProductId(productId);
        expectProduct.setTechnics(List.of());
        Product actualProduct = productRepository.edit(productId, editProduct);

        assertEquals(expectProduct, actualProduct);

        verify(dataBaseManager, times(2)).getConnection();
        verify(connection).prepareStatement(FIND_PRODUCT_BY_ID.QUERY);
        verify(ps, times(2)).executeQuery();
        verify(productMapper).toEntity(rs);
        verify(connection).setAutoCommit(false);
        verify(connection).setSavepoint();
        verify(connection).prepareStatement(UPDATE_PRODUCT_BY_ID.QUERY);
        verify(connection).prepareStatement(UPDATE_PRODUCT_BALANCE.QUERY);
        verify(ps, times(2)).setInt(any(Integer.class), any(Integer.class));
        verify(ps, times(4)).setLong(any(Integer.class), any(Long.class));
        verify(ps, times(2)).executeUpdate();
        verify(connection).prepareStatement(FIND_APPLICABILITY_BY_PRODUCT_ID.QUERY);
        verify(rs).next();
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
    }

    @Test
    @SneakyThrows
    public void editExceptExceptionAndRollback(){
        long productId = 1L;
        Product editProduct = new Product();
        editProduct.setTechnics(List.of());
        Product baseProduct = new Product();

        when(dataBaseManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(FIND_PRODUCT_BY_ID.QUERY)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(productMapper.toEntity(rs)).thenReturn(Optional.of(baseProduct));

        doThrow(new SQLException()).when(connection).prepareStatement(UPDATE_PRODUCT_BY_ID.QUERY);

        productRepository.edit(productId, editProduct);

        verify(dataBaseManager, times(2)).getConnection();
        verify(connection).prepareStatement(FIND_PRODUCT_BY_ID.QUERY);
        verify(ps).executeQuery();
        verify(productMapper).toEntity(rs);
        verify(connection).setAutoCommit(false);
        verify(connection).setSavepoint();
        verify(connection).rollback(any());
        verify(connection).setAutoCommit(true);
    }

    @Test
    @SneakyThrows
    public void deleteById(){
        long productId = 1L;

        when(dataBaseManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(DELETE_PRODUCT_BY_ID.QUERY)).thenReturn(ps);

        productRepository.deleteById(productId);

        verify(dataBaseManager).getConnection();
        verify(connection).prepareStatement(DELETE_PRODUCT_BY_ID.QUERY);
        verify(ps).setLong(any(Integer.class), any(Long.class));
        verify(ps).execute();
    }
}
