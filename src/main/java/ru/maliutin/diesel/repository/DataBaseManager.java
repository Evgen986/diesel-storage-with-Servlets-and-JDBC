package ru.maliutin.diesel.repository;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Менеджер соединения с БД.
 */
public class DataBaseManager implements iDataBaseManager, AutoCloseable{
    private final String pathProperties;

    private Connection connection;

    public DataBaseManager(String pathProperties) {
        this.pathProperties = pathProperties;
    }

    /**
     * Получение соединения с БД.
     * @return объект соединения.
     */
    @Override
    public Connection getConnection() throws SQLException{
        Properties properties = new Properties();
        try(FileInputStream inputStream = new FileInputStream(pathProperties)){
            properties.load(inputStream);
        }catch (IOException e){
            e.printStackTrace();
        }
        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        try{
            Class.forName("org.postgresql.Driver"); // TODO почему то требуется подгрузить класс
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        this.connection = DriverManager.getConnection(url, username, password);
        return this.connection;
    }

    /**
     * Закрытие соединения с БД.
     * @throws Exception исключение при закрытии соединения.
     */
    @Override
    public void close() throws Exception {
        if (connection != null){
            this.connection.close();
        }
    }
}
