package com.example.ecommerce_system.dao.interfaces;

import com.example.ecommerce_system.exception.DaoException;
import com.example.ecommerce_system.model.Review;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public interface ReviewDao {
    /**
     * Persist a new {@link Review}.
     */
    void save(Connection connection, Review review) throws DaoException;

    /**
     * Load a page of {@link Review} for a product.
     */
    List<Review> findByProduct(Connection connection, UUID productId, int limit, int offset) throws DaoException;
}
