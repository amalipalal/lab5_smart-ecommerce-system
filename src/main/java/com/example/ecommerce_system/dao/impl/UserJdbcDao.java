package com.example.ecommerce_system.dao.impl;

import com.example.ecommerce_system.dao.interfaces.UserDao;
import com.example.ecommerce_system.exception.DaoException;
import com.example.ecommerce_system.model.Role;
import com.example.ecommerce_system.model.User;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserJdbcDao implements UserDao {

    private static final String FIND_BY_EMAIL = """
            SELECT u.user_id, u.email, u.password_hash, r.role_name as role, u.created_at
            FROM users u
            JOIN roles r ON u.role_id = r.role_id
            WHERE u.email = ?
            """;

    private static final String SAVE = """
            INSERT INTO users (user_id, email, password_hash, role_id, created_at)
            VALUES (?, ?, ?, (SELECT role_id FROM roles WHERE role_name = ?), ?)
            """;

    @Override
    public Optional<User> findByEmail(Connection conn, String email) throws DaoException {
        try (PreparedStatement preparedStatement = conn.prepareStatement(FIND_BY_EMAIL)) {
            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to find user with email: " + email, e);
        }
        return Optional.empty();
    }

    private User mapRowToUser(ResultSet resultSet) throws SQLException {
        return User.builder()
                .userId(resultSet.getObject("user_id", UUID.class))
                .email(resultSet.getString("email"))
                .passwordHash(resultSet.getString("password_hash"))
                .role(Role.valueOf(resultSet.getString("role").toUpperCase()))
                .createdAt(resultSet.getTimestamp("created_at").toInstant())
                .build();
    }

    @Override
    public void save(Connection conn, User user) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(SAVE)) {
            ps.setObject(1, user.getUserId());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.setTimestamp(5, Timestamp.from(user.getCreatedAt()));

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[DEBUG]: " + e.getMessage());
            throw new DaoException("Error saving user", e);
        }
    }
}
