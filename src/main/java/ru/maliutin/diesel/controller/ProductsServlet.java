package ru.maliutin.diesel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.maliutin.diesel.dto.ProductDTO;
import ru.maliutin.diesel.exception.ValidationProductException;
import ru.maliutin.diesel.service.ProductServiceImpl;
import ru.maliutin.diesel.service.iProductService;

import java.io.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Сервлет обработки запросов товаров.
 */
@WebServlet(urlPatterns = "/product")
public class ProductsServlet extends HttpServlet implements ReadBodyRequest {

    private iProductService productService;
    private ObjectMapper objectMapper;

    /**
     * Инициализация сервлета.
     */
    @Override
    public void init(ServletConfig config) {
        final Object productService = config.getServletContext().getAttribute("productService");
        this.productService = (ProductServiceImpl) productService;
        final Object objectMapper = config.getServletContext().getAttribute("objectMapper");
        this.objectMapper = (ObjectMapper) objectMapper;
    }

    /**
     * Получение списка товаров.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter pw = resp.getWriter();
        try {
            List<ProductDTO> products = productService.getAllProduct();
            resp.setContentType("application/json; charset=UTF-8");
            resp.setStatus(200);
            String response = objectMapper.writeValueAsString(products);
            pw.println(response);
        }catch (SQLException e){
            resp.setStatus(500);
            pw.println(e.getMessage());
        }
    }

    /**
     * Создание нового товара.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ProductDTO productDTO = objectMapper.readValue(readBody(req), ProductDTO.class);
        PrintWriter pw = resp.getWriter();
        resp.setContentType("application/json; charset=UTF-8");
        try {
            productDTO = productService.addProduct(productDTO);
            resp.setStatus(200);
            pw.println(objectMapper.writeValueAsString(productDTO));
        }catch (ValidationProductException e){
            resp.setStatus(400);
            pw.println(objectMapper.writeValueAsString(e.getMessage()));
            pw.println(objectMapper.writeValueAsString(e.getMessages()));
        }catch (SQLException e){
            resp.setStatus(500);
            pw.println(e.getMessage());
        }
    }
}
