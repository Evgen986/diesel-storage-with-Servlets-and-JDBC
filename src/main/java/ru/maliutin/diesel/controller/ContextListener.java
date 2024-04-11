package ru.maliutin.diesel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.maliutin.diesel.dto.MapperDtoImpl;
import ru.maliutin.diesel.dto.ProductDTO;
import ru.maliutin.diesel.dto.iMapperDto;
import ru.maliutin.diesel.repository.*;
import ru.maliutin.diesel.service.ProductServiceImpl;
import ru.maliutin.diesel.service.iProductService;
import ru.maliutin.diesel.utils.validation.ProductValidation;
import ru.maliutin.diesel.utils.validation.iValidationService;

/**
 * Конфигурационный класс контекста приложения.
 */
@WebListener
public class ContextListener implements ServletContextListener {

    /**
     * Инициализация контекста приложения.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final ServletContext servletContext = sce.getServletContext();
        final iDataBaseManager dataBaseManager = new DataBaseManager();
        final ProductMapper productMapper = new ProductMapper();
        final iProductRepository productRepository = new ProductRepositoryImpl(dataBaseManager, productMapper);
        final iValidationService<ProductDTO> validationService = new ProductValidation();
        final iMapperDto mapperDto = new MapperDtoImpl();
        iProductService productService = new ProductServiceImpl(productRepository, mapperDto, validationService);
        servletContext.setAttribute("productService", productService);
        ObjectMapper objectMapper = new ObjectMapper();
        servletContext.setAttribute("objectMapper", objectMapper);
    }
}
