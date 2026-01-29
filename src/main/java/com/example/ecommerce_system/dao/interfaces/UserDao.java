package com.example.ecommerce_system.dao.interfaces;

import com.example.ecommerce_system.exception.DaoException;
import com.example.ecommerce_system.model.User;

import java.sql.Connection;
import java.util.Optional;

public interface UserDao {

    /**
     * Persist a new {@link User}.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param user user to save
     * @throws DaoException on DAO errors
     */
    void save(Connection connection, User user) throws DaoException;

    /**
     * Find a user by email.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param email user email
     * @return optional user when found
     * @throws DaoException on DAO errors
     */
    Optional<User> findByEmail(Connection connection, String email) throws DaoException;
}
