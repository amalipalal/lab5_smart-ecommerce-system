package com.example.ecommerce_system.dao.interfaces;

import com.example.ecommerce_system.exception.DaoException;
import com.example.ecommerce_system.model.Cart;
import com.example.ecommerce_system.model.CartItem;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartDao {

    /**
     * Find a cart by customer id (1:1 relation expected).
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param customerId customer identifier
     * @return optional cart when found
     * @throws DaoException on DAO errors
     */
    Optional<Cart> findByCustomerId(Connection connection, UUID customerId) throws DaoException;

    /**
     * Persist a new {@link Cart}.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param cart cart to save
     * @throws DaoException on DAO errors
     */
    void save(Connection connection, Cart cart) throws DaoException;

    /**
     * Add a {@link CartItem} to a cart.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param item the cart item to add
     * @throws DaoException on DAO errors
     */
    void addItem(Connection connection, CartItem item) throws DaoException;

    /**
     * Update quantity for a cart item.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param cartItemId cart item identifier
     * @param newQuantity new quantity
     * @throws DaoException on DAO errors
     */
    void updateItemQuantity(Connection connection, UUID cartItemId, int newQuantity) throws DaoException;

    /**
     * Find a cart item by its id.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param cartItemId cart item identifier
     * @return optional cart item when found
     * @throws DaoException on DAO errors
     */
    Optional<CartItem> findCartItemById(Connection connection, UUID cartItemId) throws DaoException;

    /**
     * Delete a cart item by id.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param cartItemId cart item identifier to delete
     * @throws DaoException on DAO errors
     */
    void deleteItemById(Connection connection, UUID cartItemId) throws DaoException;

    /**
     * Get all items for a given cart.
     *
     * @param connection the {@link java.sql.Connection} to use
     * @param cartId cart identifier
     * @return list of cart items
     * @throws DaoException on DAO errors
     */
    List<CartItem> findItemsByCartId(Connection connection, UUID cartId) throws DaoException;
}
