package com.example.ecommerce_system;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDataSourceIsHikariCP() {
        assertNotNull(dataSource, "DataSource should be auto-configured");
        assertInstanceOf(HikariDataSource.class, dataSource, "DataSource should be HikariCP");

        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        System.out.println("✓ HikariCP DataSource configured successfully");
        System.out.println("  Pool Name: " + hikariDataSource.getPoolName());
        System.out.println("  Max Pool Size: " + hikariDataSource.getMaximumPoolSize());
        System.out.println("  Min Idle: " + hikariDataSource.getMinimumIdle());
    }

    @Test
    void testConnectionAcquisition() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            assertNotNull(conn, "Connection should not be null");
            assertFalse(conn.isClosed(), "Connection should be open");

            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("✓ Connection acquired successfully");
            System.out.println("  Database: " + metaData.getDatabaseProductName());
            System.out.println("  Version: " + metaData.getDatabaseProductVersion());
            System.out.println("  URL: " + metaData.getURL());
            System.out.println("  Driver: " + metaData.getDriverName());
        }
    }

    @Test
    void testMultipleConnections() throws SQLException {
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();

        System.out.println("Initial Pool State:");
        System.out.println("  Active Connections: " + poolMXBean.getActiveConnections());
        System.out.println("  Idle Connections: " + poolMXBean.getIdleConnections());
        System.out.println("  Total Connections: " + poolMXBean.getTotalConnections());

        try (Connection conn1 = dataSource.getConnection();
             Connection conn2 = dataSource.getConnection();
             Connection conn3 = dataSource.getConnection()) {

            System.out.println("\n✓ Acquired 3 connections from pool");
            System.out.println("  Active Connections: " + poolMXBean.getActiveConnections());
            System.out.println("  Idle Connections: " + poolMXBean.getIdleConnections());

            assertTrue(poolMXBean.getActiveConnections() >= 3, "Should have at least 3 active connections");
        }

        System.out.println("\nAfter releasing connections:");
        System.out.println("  Active Connections: " + poolMXBean.getActiveConnections());
        System.out.println("  Idle Connections: " + poolMXBean.getIdleConnections());
    }

    @Test
    void testDatabaseQuery() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            // Test a simple query
            var statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT 1 as test_value");

            assertTrue(rs.next(), "Query should return a result");
            assertEquals(1, rs.getInt("test_value"));

            System.out.println("✓ Database query executed successfully");
        }
    }

    @Test
    void testConnectionValidation() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            assertTrue(conn.isValid(5), "Connection should be valid");
            System.out.println("✓ Connection validation passed");
        }
    }
}
