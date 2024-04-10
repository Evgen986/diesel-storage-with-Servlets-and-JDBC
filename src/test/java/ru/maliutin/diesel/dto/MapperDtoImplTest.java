package ru.maliutin.diesel.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.maliutin.diesel.dto.MapperDtoImpl;
import ru.maliutin.diesel.dto.ProductDTO;
import ru.maliutin.diesel.dto.TechnicDTO;
import ru.maliutin.diesel.dto.iMapperDto;
import ru.maliutin.diesel.entity.Product;
import ru.maliutin.diesel.entity.Technic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MapperDtoImplTest {

    private iMapperDto mapperDto;

    @BeforeEach
    public void init(){
        mapperDto = new MapperDtoImpl();
    }

    @Test
    public void toEntity(){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("title");
        productDTO.setCatalogueNumber("123");
        productDTO.setProgramNumber(123);
        productDTO.setTechnics(List.of(new TechnicDTO("title")));
        productDTO.setBalance(1);
        productDTO.setPrice(new BigDecimal(1));

        Product expectProduct = new Product();
        expectProduct.setTitle("title");
        expectProduct.setCatalogueNumber("123");
        expectProduct.setProgramNumber(123);
        expectProduct.setTechnics(List.of(new Technic("title")));
        expectProduct.setBalance(1);
        expectProduct.setPrice(new BigDecimal(1));

        Product actualProduct = mapperDto.toEntity(productDTO);

        Assertions.assertEquals(expectProduct, actualProduct);

    }

    @Test
    public void toDto(){

        Product product = new Product();
        product.setTitle("title");
        product.setCatalogueNumber("123");
        product.setProgramNumber(123);
        product.setTechnics(List.of(new Technic("title")));
        product.setBalance(1);
        product.setPrice(new BigDecimal(1));

        ProductDTO expectProductDTO = new ProductDTO();
        expectProductDTO.setTitle("title");
        expectProductDTO.setCatalogueNumber("123");
        expectProductDTO.setProgramNumber(123);
        expectProductDTO.setTechnics(List.of(new TechnicDTO("title")));
        expectProductDTO.setBalance(1);
        expectProductDTO.setPrice(new BigDecimal(1));

        ProductDTO actualProduct = mapperDto.toDto(product);

        Assertions.assertEquals(expectProductDTO, actualProduct);

    }
}
