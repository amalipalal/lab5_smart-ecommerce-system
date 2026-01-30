package com.example.ecommerce_system.store;

import com.example.ecommerce_system.dao.interfaces.CustomerDao;
import com.example.ecommerce_system.dao.interfaces.UserDao;
import com.example.ecommerce_system.exception.DaoException;
import com.example.ecommerce_system.exception.DatabaseConnectionException;
import com.example.ecommerce_system.exception.user.UserCreationException;
import com.example.ecommerce_system.exception.user.UserRetrievalException;
import com.example.ecommerce_system.model.Customer;
import com.example.ecommerce_system.model.User;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class UserStore {
    private final DataSource dataSource;
    private final UserDao userDao;
    private final CustomerDao customerDao;

    /**
     * Persist a new {@link com.example.ecommerce_system.model.User} within a transaction.
     *
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.UserDao#save(java.sql.Connection, com.example.ecommerce_system.model.User)}.
     *
     * @param user the user to create
     * @return the persisted {@link com.example.ecommerce_system.model.User}
     * @throws UserCreationException when Dao persistence fails
     * @throws com.example.ecommerce_system.exception.DatabaseConnectionException when a DB connection cannot be obtained
     */
    @Caching(
        put = {
            @CachePut(value = "users", key = "'email:' + #user.email"),
        },
        evict = @CacheEvict(value = "customers", allEntries = true)
    )
    public User createUser(User user, Customer customer) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                userDao.save(conn, user);
                customerDao.save(conn, user.getUserId(), customer);
                conn.commit();
                return user;
            } catch (DaoException e) {
                conn.rollback();
                throw new UserCreationException(user.getEmail());
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Load a user by email.
     *
     * Uses {@link com.example.ecommerce_system.dao.interfaces.UserDao#findByEmail(java.sql.Connection, String)}.
     * The returned value is cached in the "users" cache using Spring's cache abstraction
     * (see the {@link org.springframework.cache.annotation.Cacheable} annotation applied to this method).
     *
     * @param email user email
     * @return an {@link Optional} containing the User when found
     * @throws UserRetrievalException when DAO retrieval fails
     * @throws com.example.ecommerce_system.exception.DatabaseConnectionException when a DB connection cannot be obtained
     */
    @Cacheable(value = "users", key = "'email:' + #email")
    public Optional<User> getUserByEmail(String email) {
        try (Connection conn = dataSource.getConnection()) {
            return userDao.findByEmail(conn, email);
        } catch (DaoException e) {
            throw new UserRetrievalException(email);
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
