package com.example.ecommerce_system.dao.interfaces;


import com.example.ecommerce_system.exception.DaoException;
import com.example.ecommerce_system.model.Category;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryDao {

    /**
     * Find a category by id.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param id identifier
     * @return optional category when found
     * @throws DaoException on Dao errors
     */
    Optional<Category> findById(Connection connection, UUID id) throws DaoException;

    /**
     * Search categories by name with paging.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param query substring to search for
     * @param limit maximum results
     * @param offset zero-based offset
     * @return list of matching objects
     * @throws DaoException on DAO errors
     */
    List<Category> searchByName(Connection connection, String query, int limit, int offset) throws DaoException;

    /**
     * Persist a new {@link Category}.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param category object to save
     * @throws DaoException on Dao errors
     */
    void save(Connection connection, Category category) throws DaoException;

    /**
     * Update an existing {@link Category}.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param category object to update
     * @throws DaoException on Dao errors
     */
    void update(Connection connection, Category category) throws DaoException;

    /**
     * Find a category by name.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param name category name
     * @return optional category when found
     * @throws DaoException on Dao errors
     */
    Optional<Category> findByName(Connection connection, String name) throws DaoException;

    /**
     * Count categories matching a name query.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param query name substring
     * @return number of matching categories
     * @throws DaoException on Dao errors
     */
    int countByName(Connection connection, String query) throws DaoException;

    /**
     * Retrieve all categories with paging.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param limit maximum results
     * @param offset zero-based offset
     * @return list of categories
     * @throws DaoException on Dao errors
     */
    List<Category> findAll(Connection connection, int limit, int offset) throws DaoException;

    /**
     * Count all categories.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @return total number of categories
     * @throws DaoException on Dao errors
     */
    int count(Connection connection) throws DaoException;

    /**
     * Delete a category by id.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param id identifier of the category to delete
     * @throws DaoException on Dao errors
     */
    void delete(Connection connection, UUID id) throws DaoException;
}