package ru.maliutin.diesel.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.maliutin.diesel.dto.MapperDtoImpl;
import ru.maliutin.diesel.dto.ProductDTO;
import ru.maliutin.diesel.entity.Product;
import ru.maliutin.diesel.exception.ValidationProductException;
import ru.maliutin.diesel.repository.ProductRepositoryImpl;
import ru.maliutin.diesel.utils.validation.ProductValidation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepositoryImpl productRepository;
    @Mock
    private MapperDtoImpl mapperDto;
    @Mock
    private ProductValidation productValidation;
    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    public void getProductById(){
        long productId = 1L;
        Product product = new Product();
        ProductDTO expectProductDTO = new ProductDTO();
        when(productRepository.findProductById(productId)).thenReturn(product);
        when(mapperDto.toDto(product)).thenReturn(expectProductDTO);
        ProductDTO actualProductDto = productService.getProductById(productId);

        assertEquals(expectProductDTO, actualProductDto);
        verify(productRepository).findProductById(productId);
        verify(mapperDto).toDto(product);
    }

    @Test
    public void getAllProducts(){
        Product product = new Product();
        List<Product> products = new ArrayList<>(
                Arrays.asList(product, product));
        ProductDTO productDTO = new ProductDTO();
        List<ProductDTO> expectProducts = new ArrayList<>(Arrays.asList(
                productDTO, productDTO));
        when(productRepository.findAllProduct()).thenReturn(products);
        when(mapperDto.toDto(product)).thenReturn(productDTO);

        List<ProductDTO> actualProducts = productService.getAllProduct();
        assertEquals(expectProducts, actualProducts);
        verify(productRepository).findAllProduct();
        verify(mapperDto, times(2)).toDto(product);
    }

    @Test
    public void addProductExpectProductDTO(){
        ProductDTO expectProduct = new ProductDTO();
        Product product = new Product();
        doNothing().when(productValidation).validation(expectProduct);
        when(mapperDto.toEntity(expectProduct)).thenReturn(product);
        when(productRepository.create(product)).thenReturn(product);
        when(mapperDto.toDto(product)).thenReturn(expectProduct);
        ProductDTO actualProduct = productService.addProduct(expectProduct);

        assertEquals(expectProduct, actualProduct);
        verify(productValidation).validation(expectProduct);
        verify(mapperDto).toEntity(expectProduct);
        verify(productRepository).create(product);
        verify(mapperDto).toDto(product);
    }

    @Test
    public void addProductExpectValidationProductException(){
        ProductDTO productDTO = new ProductDTO();
        String message = "Validation failed";
        List<String> messages = new ArrayList<>();
        doThrow(new ValidationProductException(message, messages))
                .when(productValidation).validation(productDTO);

        Assertions.assertThrows(ValidationProductException.class, () ->
                productService.addProduct(productDTO));
        verify(mapperDto, never()).toEntity(any(ProductDTO.class));
        verify(productRepository, never()).create(any(Product.class));
        verify(mapperDto, never()).toDto(any(Product.class));
    }

    @Test
    public void editProductExpectProductDTO(){
        long productId = 1L;
        ProductDTO exectProductDTO = new ProductDTO();
        Product product = new Product();

        doNothing().when(productValidation).validation(exectProductDTO);
        when(mapperDto.toEntity(exectProductDTO)).thenReturn(product);
        when(productRepository.edit(productId, product)).thenReturn(product);
        when(mapperDto.toDto(product)).thenReturn(exectProductDTO);

        ProductDTO actualProduct = productService.editProduct(productId, exectProductDTO);

        assertEquals(exectProductDTO, actualProduct);
        verify(productValidation).validation(exectProductDTO);
        verify(mapperDto).toEntity(exectProductDTO);
        verify(productRepository).edit(productId, product);
        verify(mapperDto).toDto(product);
    }

    @Test
    public void editProductExpectValidationProductException(){
        long productId = 1L;
        ProductDTO productDTO = new ProductDTO();
        String message = "Validation failed";
        List<String> messages = new ArrayList<>();
        doThrow(new ValidationProductException(message, messages))
                .when(productValidation).validation(productDTO);

        Assertions.assertThrows(ValidationProductException.class, () ->
                productService.editProduct(productId, productDTO));
        verify(mapperDto, never()).toEntity(any(ProductDTO.class));
        verify(productRepository, never()).create(any(Product.class));
        verify(mapperDto, never()).toDto(any(Product.class));
    }

    @Test
    public void deleteProduct(){
        long productId = 1L;
        doNothing().when(productRepository).deleteById(productId);
        productService.deleteProduct(productId);
        verify(productRepository).deleteById(productId);
    }
}
