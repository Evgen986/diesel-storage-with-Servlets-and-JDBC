package ru.maliutin.diesel.repository;

import java.sql.Connection;
import java.sql.SQLException;

public interface iDataBaseManager extends AutoCloseable{
    Connection getConnection() throws SQLException;
}
