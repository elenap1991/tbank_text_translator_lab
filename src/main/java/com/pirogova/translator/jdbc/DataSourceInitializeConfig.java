package com.pirogova.translator.jdbc;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DataSourceInitializeConfig {

    private final DataSource dataSource;

    public DataSourceInitializeConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    private void init() {
        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS requests(id INTEGER PRIMARY KEY, ip VARCHAR(100),input VARCHAR(100),result VARCHAR(100) )");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

