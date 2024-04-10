package ru.maliutin.diesel.repository;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.maliutin.diesel.entity.Product;
import ru.maliutin.diesel.entity.Technic;
import ru.maliutin.diesel.repository.ProductMapper;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductMapperTest {

    private ResultSet rs;

    @BeforeEach
    public void init(){
        rs = mock(ResultSet.class);
    }
    @Test
    @SneakyThrows
    public void toEntityExceptOptionalEmpty(){
        when(rs.next()).thenReturn(false);
        ProductMapper productMapper = new ProductMapper();

        Optional<Product> result = productMapper.toEntity(rs);

        assertTrue(result.isEmpty());
    }

    @Test
    @SneakyThrows
    public void toEntityExceptProduct(){
        long productId = 1L;
        String title = "test product";
        String technicTitle = "test technic";

        when(rs.next()).thenReturn(true, false);
        when(rs.getLong("product_id")).thenReturn(productId);
        when(rs.getString("c_title")).thenReturn(title);
        when(rs.getString("technic_title")).thenReturn(technicTitle);

        Product expectProduct = new Product();
        expectProduct.setProductId(productId);
        expectProduct.setTitle(title);
        expectProduct.setTechnics(List.of(new Technic(technicTitle)));

        ProductMapper productMapper = new ProductMapper();

        Optional<Product> actualProduct = productMapper.toEntity(rs);

        assertEquals(expectProduct, actualProduct.get());
    }

    @Test
    @SneakyThrows
    public void toListEntityExpectListProducts(){
        long productId = 1L;
        String title = "test product";
        String technicTitleOne = "test technic_1";
        String technicTitleTwo = "test technic_2";
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getLong("product_id")).thenReturn(productId);
        when(rs.getString("c_title")).thenReturn(title);
        when(rs.getString("technic_title"))
                .thenReturn(technicTitleOne).thenReturn(technicTitleTwo);
        Product expectProduct = new Product();
        expectProduct.setProductId(productId);
        expectProduct.setTitle(title);
        expectProduct.setTechnics(
                List.of(new Technic(technicTitleOne),
                        new Technic(technicTitleTwo)));

        List<Product> expectProducts = List.of(expectProduct);

        ProductMapper productMapper = new ProductMapper();
        List<Product> actualProduct = productMapper.toListEntity(rs);

        assertEquals(expectProducts, actualProduct);
    }

}
