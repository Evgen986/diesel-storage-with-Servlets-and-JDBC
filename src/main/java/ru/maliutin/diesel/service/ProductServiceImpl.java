package ru.maliutin.diesel.service;

import lombok.SneakyThrows;
import ru.maliutin.diesel.dto.ProductDTO;
import ru.maliutin.diesel.dto.iMapperDto;
import ru.maliutin.diesel.entity.Product;
import ru.maliutin.diesel.exception.NoSuchProductException;
import ru.maliutin.diesel.exception.ValidationProductException;
import ru.maliutin.diesel.repository.iProductRepository;
import ru.maliutin.diesel.utils.validation.iValidationService;

import java.util.List;

/**
 * Сервис работы с товарами.
 */
public class ProductServiceImpl implements iProductService {

    private final iProductRepository productRepository;
    private final iMapperDto mapperDto;
    private final iValidationService<ProductDTO> validationService;

    public ProductServiceImpl(iProductRepository productRepository,
                              iMapperDto mapperDto,
                              iValidationService<ProductDTO> validationService) {
        this.productRepository = productRepository;
        this.mapperDto = mapperDto;
        this.validationService = validationService;
    }

    /**
     * Получение товара по idю
     * @param id уникальный идентификатор товара.
     * @return Найденный товар.
     * @throws NoSuchProductException исключение при отсутствии товара.
     */
    @Override
    @SneakyThrows
    public ProductDTO getProductById(long id) throws NoSuchProductException {
        Product product = productRepository.findProductById(id);
        return mapperDto.toDto(product);
    }

    /**
     * Получение всех товаров.
     * @return список товаров.
     */
    @Override
    @SneakyThrows
    public List<ProductDTO> getAllProduct() {
        return productRepository.findAllProduct()
                .stream().map(mapperDto::toDto).toList();
    }

    /**
     * Добавление нового товара.
     * @param productDTO объект передачи данных.
     * @return созданный товар.
     * @throws ValidationProductException исключение при валидации данных.
     */
    @Override
    @SneakyThrows
    public ProductDTO addProduct(ProductDTO productDTO) throws ValidationProductException {
        validationService.validation(productDTO);
        Product product = productRepository.create(mapperDto.toEntity(productDTO));
        return mapperDto.toDto(product);
    }

    /**
     * Изменение существующего товара.
     * @param productId идентификатор товара.
     * @param productDTO объект передачи данных.
     * @return обновленный товар.
     * @throws ValidationProductException исключение при валидации данных.
     */
    @Override
    @SneakyThrows
    public ProductDTO editProduct(long productId, ProductDTO productDTO) throws ValidationProductException, NoSuchProductException {
        getProductById(productId);
        validationService.validation(productDTO);
        Product product = productRepository.edit(productId, mapperDto.toEntity(productDTO));
        return mapperDto.toDto(product);
    }

    /**
     * Удаление товара.
     * @param id идентификатор товара.
     */
    @Override
    @SneakyThrows
    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

}
