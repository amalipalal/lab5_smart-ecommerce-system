package com.example.ecommerce_system.store;

import com.example.ecommerce_system.dao.interfaces.CartDao;
import com.example.ecommerce_system.exception.DaoException;
import com.example.ecommerce_system.exception.DatabaseConnectionException;
import com.example.ecommerce_system.exception.cart.CartCreationException;
import com.example.ecommerce_system.exception.cart.CartItemAddException;
import com.example.ecommerce_system.exception.cart.CartItemRemoveException;
import com.example.ecommerce_system.exception.cart.CartRetrievalException;
import com.example.ecommerce_system.exception.cart.CartUpdateException;
import com.example.ecommerce_system.model.Cart;
import com.example.ecommerce_system.model.CartItem;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Repository
public class CartStore {
    private final DataSource dataSource;
    private final CartDao cartDao;
    private final CacheManager cacheManager;

    /**
     * Create/Persist a new Cart inside a transaction.
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CartDao#save(java.sql.Connection, com.example.ecommerce_system.model.Cart)}.
     */
    @Caching(evict = {
            @CacheEvict(value = "carts", key = "'customer:' + #cart.customerId"),
            @CacheEvict(value = "carts", key = "'cart:' + #cart.cartId")
    })
    public Cart createCart(Cart cart) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                this.cartDao.save(conn, cart);
                conn.commit();
                return cart;
            } catch (DaoException e) {
                try { conn.rollback(); } catch (SQLException ignored) {}
                throw new CartCreationException(String.valueOf(cart.getCartId()));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Add a new item to a cart inside a transaction.
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CartDao#addItem(java.sql.Connection, com.example.ecommerce_system.model.CartItem)}.
     */
    @CacheEvict(value = "carts", key = "'cart:' + #item.cartId")
    public void addCartItem(CartItem item) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                this.cartDao.addItem(conn, item);
                conn.commit();
            } catch (DaoException e) {
                try { conn.rollback(); } catch (SQLException ignored) {}
                throw new CartItemAddException(String.valueOf(item.getCartItemId()));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Remove a cart item by its id inside a transaction.
     * evict entire cache after this is done successfully
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CartDao#deleteItemById(java.sql.Connection, java.util.UUID)}.
     */
    @Cacheable(value = "carts", key = "'item:' + #cartItemId")
    public void removeCartItem(UUID cartItemId) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // First, find the cart item to get the cart ID for cache eviction
                Optional<CartItem> cartItem = this.cartDao.findCartItemById(conn, cartItemId);
                this.cartDao.deleteItemById(conn, cartItemId);
                conn.commit();

                cartItem.ifPresent(item ->
                    evictCartCache(item.getCartId())
                );
            } catch (DaoException e) {
                try { conn.rollback(); } catch (SQLException ignored) {}
                throw new CartItemRemoveException(String.valueOf(cartItemId));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    private void evictCartCache(UUID cartId) {
        var cache = cacheManager.getCache("carts");
        if (cache != null) {
            cache.evict("cart:" + cartId);
            cache.evict("items:" + cartId);
        }
    }

    /**
     * Update quantity of a cart item inside a transaction.
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CartDao#updateItemQuantity(java.sql.Connection, java.util.UUID, int)}.
     */
    @CacheEvict(value = "carts", key = "'item' + #cartItemId")
    public void updateCartItem(UUID cartItemId, int newQuantity) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // First, find the cart item to get the cart ID for cache eviction
                Optional<CartItem> cartItem = this.cartDao.findCartItemById(conn, cartItemId);
                this.cartDao.updateItemQuantity(conn, cartItemId, newQuantity);
                conn.commit();

                cartItem.ifPresent(item ->
                    evictCartCache(item.getCartId())
                );
            } catch (DaoException e) {
                try { conn.rollback(); } catch (SQLException ignored) {}
                throw new CartUpdateException(String.valueOf(cartItemId));
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Get all items in a cart.
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CartDao#findItemsByCartId(java.sql.Connection, java.util.UUID)}.
     */
    @Cacheable(value = "carts", key = "'items:' + #cartId")
    public List<CartItem> getCartItems(UUID cartId) {
        try (Connection conn = dataSource.getConnection()) {
            return this.cartDao.findItemsByCartId(conn, cartId);
        } catch (DaoException e) {
            throw new CartRetrievalException(String.valueOf(cartId));
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Retrieve a cart by customer id.
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CartDao#findByCustomerId(java.sql.Connection, java.util.UUID)}.
     */
    @Cacheable(value = "carts", key = "'customer:' + #customerId")
    public Optional<Cart> getCartByCustomerId(UUID customerId) {
        try (Connection conn = dataSource.getConnection()) {
            return this.cartDao.findByCustomerId(conn, customerId);
        } catch (DaoException e) {
            throw new CartRetrievalException(String.valueOf(customerId));
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Retrieve a single cart item by its ID.
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CartDao#findCartItemById(java.sql.Connection, java.util.UUID)}.
     */
    @Cacheable(value = "carts", key = "'item' + #cartItemId")
    public Optional<CartItem> getCartItem(UUID cartItemId) {
        try (Connection conn = dataSource.getConnection()) {
            return this.cartDao.findCartItemById(conn, cartItemId);
        } catch (DaoException e) {
            throw new CartRetrievalException(String.valueOf(cartItemId));
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
