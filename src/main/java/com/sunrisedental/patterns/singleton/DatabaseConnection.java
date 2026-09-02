package com.sunrisedental.patterns.singleton;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton Design Pattern Implementation
 * ----------------------------------------
 * Ensures only ONE instance of the database connection pool
 * exists throughout the application's lifecycle.
 *
 * Why Singleton here:
 * - Creating a new connection pool per request would be expensive
 *   and could exhaust database resources.
 * - A single shared HikariCP pool efficiently manages and reuses
 *   connections across all DAO classes.
 *
 * Thread-safety:
 * - Uses the "Bill Pugh Singleton" (static inner holder class) approach,
 *   which is lazy-loaded and inherently thread-safe without needing
 *   synchronized blocks (better performance than double-checked locking).
 */
public class DatabaseConnection {

    private static HikariDataSource dataSource;

    // Private constructor prevents external instantiation
    private DatabaseConnection() {
        try {
            Properties props = new Properties();
            InputStream input = DatabaseConnection.class
                    .getClassLoader()
                    .getResourceAsStream("db.properties");

            if (input == null) {
                throw new RuntimeException("db.properties not found in classpath");
            }
            props.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));
            config.setDriverClassName(props.getProperty("db.driver"));

            config.setMaximumPoolSize(
                    Integer.parseInt(props.getProperty("db.pool.maxSize", "10")));
            config.setMinimumIdle(
                    Integer.parseInt(props.getProperty("db.pool.minIdle", "2")));
            config.setConnectionTimeout(
                    Long.parseLong(props.getProperty("db.pool.connectionTimeout", "30000")));

            dataSource = new HikariDataSource(config);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    /**
     * Static inner holder class - loaded only when getInstance() is called.
     * This is what makes the Singleton "lazy" and thread-safe by default
     * (JVM guarantees class initialization is thread-safe).
     */
    private static class Holder {
        private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    }

    public static DatabaseConnection getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Returns a connection from the pool.
     * Caller is responsible for closing it (try-with-resources recommended),
     * which returns it to the pool rather than actually closing it.
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}