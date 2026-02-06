package com.example.ecommerce_system.dao.impl;

import com.example.ecommerce_system.dao.interfaces.CartDao;
import com.example.ecommerce_system.exception.DaoException;
import com.example.ecommerce_system.model.Cart;
import com.example.ecommerce_system.model.CartItem;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CartJdbcDao implements CartDao {

    private static final String FIND_BY_CUSTOMER_ID = """
            SELECT cart_id, customer_id, created_at, updated_at
            FROM cart WHERE customer_id = ?
            """;

    private static final String SAVE_CART = """
            INSERT INTO cart (cart_id, customer_id, created_at, updated_at)
            VALUES (?, ?, ?, ?)
            """;

    private static final String ADD_ITEM = """
            INSERT INTO cart_item (cart_item_id, cart_id, product_id, quantity, added_at)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_ITEM_QTY = """
            UPDATE cart_item
            SET quantity = ?
            WHERE cart_item_id = ?
            """;

    private static final String FIND_CART_ITEM_BY_ID = """
            SELECT cart_item_id, cart_id, product_id, quantity, added_at
            FROM cart_item
            WHERE cart_item_id = ?
            """;

    private static final String DELETE_ITEM = """
            DELETE FROM cart_item WHERE cart_item_id = ?
            """;

    private static final String FIND_ITEMS_BY_CART_ID = """
            SELECT cart_item_id, cart_id, product_id, quantity, added_at
            FROM cart_item
            WHERE cart_id = ?
            ORDER BY added_at DESC
            """;

    @Override
    public Optional<Cart> findByCustomerId(Connection conn, UUID customerId) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(FIND_BY_CUSTOMER_ID)) {
            ps.setObject(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCart(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to find cart for customer " + customerId, e);
        }
        return Optional.empty();
    }

    private Cart mapRowToCart(ResultSet rs) throws SQLException {
        return new Cart(
                rs.getObject("cart_id", UUID.class),
                rs.getObject("customer_id", UUID.class),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant()
        );
    }

    @Override
    public void save(Connection conn, Cart cart) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(SAVE_CART)) {
            ps.setObject(1, cart.getCartId());
            ps.setObject(2, cart.getCustomerId());
            ps.setTimestamp(3, Timestamp.from(cart.getCreatedAt()));
            ps.setTimestamp(4, Timestamp.from(cart.getUpdatedAt()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error saving cart", e);
        }
    }

    @Override
    public void addItem(Connection conn, CartItem item) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(ADD_ITEM)) {
            ps.setObject(1, item.getCartItemId());
            ps.setObject(2, item.getCartId());
            ps.setObject(3, item.getProductId());
            ps.setInt(4, item.getQuantity());
            ps.setTimestamp(5, Timestamp.from(item.getAddedAt()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error adding item to cart", e);
        }
    }

    @Override
    public void updateItemQuantity(Connection conn, UUID cartItemId, int newQuantity) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(UPDATE_ITEM_QTY)) {
            ps.setInt(1, newQuantity);
            ps.setObject(2, cartItemId);
            int rows = ps.executeUpdate();
            if (rows == 0)
                throw new DaoException("Failed to update quantity for cart item: " + cartItemId);

        } catch (SQLException e) {
            throw new DaoException("Error updating cart item quantity", e);
        }
    }

    @Override
    public Optional<CartItem> findCartItemById(Connection conn, UUID cartItemId) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(FIND_CART_ITEM_BY_ID)) {
            ps.setObject(1, cartItemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRowToCartItem(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Failed to find cart item " + cartItemId, e);
        }
        return Optional.empty();
    }

    private CartItem mapRowToCartItem(ResultSet rs) throws SQLException {
        return new CartItem(
                rs.getObject("cart_item_id", UUID.class),
                rs.getObject("cart_id", UUID.class),
                rs.getObject("product_id", UUID.class),
                rs.getInt("quantity"),
                rs.getTimestamp("added_at").toInstant()
        );
    }

    @Override
    public void deleteItemById(Connection conn, UUID cartItemId) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(DELETE_ITEM)) {
            ps.setObject(1, cartItemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error deleting cart item", e);
        }
    }

    @Override
    public List<CartItem> findItemsByCartId(Connection conn, UUID cartId) throws DaoException {
        try (PreparedStatement ps = conn.prepareStatement(FIND_ITEMS_BY_CART_ID)) {
            ps.setObject(1, cartId);
            return executeQueryForItemList(ps);
        } catch (SQLException e) {
            throw new DaoException("Failed to load items for cart " + cartId, e);
        }
    }

    private List<CartItem> executeQueryForItemList(PreparedStatement ps) throws SQLException {
        List<CartItem> results = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                results.add(mapRowToCartItem(rs));
        }
        return results;
    }
}
