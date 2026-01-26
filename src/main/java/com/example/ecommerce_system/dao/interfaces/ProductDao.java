package com.example.ecommerce_system.dao.interfaces;


import com.example.ecommerce_system.dto.ProductFilter;
import com.example.ecommerce_system.exception.DaoException;
import com.example.ecommerce_system.model.Product;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductDao {

    /**
     * Find a product by id.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param productId product identifier
     * @return optional product when found
     * @throws DaoException on DAO errors
     */
    Optional<Product> findById(Connection connection, UUID productId) throws DaoException;

    /**
     * Find all products with paging.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param limit maximum results
     * @param offset zero-based offset
     * @return list of products
     * @throws DaoException on DAO errors
     */
    List<Product> findAll(Connection connection, int limit, int offset) throws DaoException;

    /**
     * Count all products.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @return total number of products
     * @throws DaoException on DAO errors
     */
    int countAll(Connection connection) throws DaoException;

    /**
     * Find products matching a {@link ProductFilter}.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param filter filtering criteria
     * @param limit maximum results
     * @param offset zero-based offset
     * @return list of matching products
     * @throws DaoException on DAO errors
     */
    List<Product> findFiltered(Connection connection, ProductFilter filter, int limit, int offset) throws DaoException;

    /**
     * Count products matching a {@link ProductFilter}.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param filter filtering criteria
     * @return number of matching products
     * @throws DaoException on DAO errors
     */
    int countFiltered(Connection connection, ProductFilter filter) throws DaoException;

    /**
     * Persist a new {@link Product}.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param product product to save
     * @throws DaoException on DAO errors
     */
    void save(Connection connection, Product product) throws DaoException;

    /**
     * Update an existing {@link Product}.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param product product to update
     * @throws DaoException on DAO errors
     */
    void update(Connection connection, Product product) throws DaoException;

    /**
     * Delete a product by id.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param productId product identifier to delete
     * @throws DaoException on DAO errors
     */
    void deleteById(Connection connection, UUID productId) throws DaoException;
}