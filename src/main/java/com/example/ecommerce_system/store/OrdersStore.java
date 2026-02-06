package com.example.ecommerce_system.store;

import com.example.ecommerce_system.dao.interfaces.OrderItemDao;
import com.example.ecommerce_system.dao.interfaces.OrdersDao;
import com.example.ecommerce_system.exception.DaoException;
import com.example.ecommerce_system.exception.DatabaseConnectionException;
import com.example.ecommerce_system.exception.order.OrderCreationException;
import com.example.ecommerce_system.exception.order.OrderRetrievalException;
import com.example.ecommerce_system.exception.order.OrderUpdateException;
import com.example.ecommerce_system.exception.orderitem.OrderItemRetrievalException;
import com.example.ecommerce_system.model.OrderItem;
import com.example.ecommerce_system.model.Orders;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Repository
public class OrdersStore {
    private final DataSource dataSource;
    private final OrdersDao ordersDao;
    private final OrderItemDao orderItemDao;

    /**
     * Persist a new {@link com.example.ecommerce_system.model.Orders} inside a transaction.</p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.OrdersDao#save(java.sql.Connection, com.example.ecommerce_system.model.Orders)}.
     * On success this method evicts relevant entries in the "orders" cache via Spring Cache.
     */
    @CacheEvict(value = {"orders", "order_items"}, allEntries = true)
    public Orders createOrder(Orders order, List<OrderItem> items) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                this.ordersDao.save(conn, order);
                this.orderItemDao.saveBatch(conn, items);
                conn.commit();
                return order;
            } catch (DaoException e) {
                conn.rollback();
                throw new OrderCreationException(order.getOrderId().toString());
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Update an existing {@link com.example.ecommerce_system.model.Orders} inside a transaction.</p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.OrdersDao#update(java.sql.Connection, com.example.ecommerce_system.model.Orders)}.
     * On success this method evicts relevant entries in the "orders" cache via Spring Cache.
     */
    @CacheEvict(value = "orders", allEntries = true)
    public Orders updateOrder(Orders order) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                this.ordersDao.update(conn, order);
                conn.commit();
                return order;
            } catch (DaoException e) {
                conn.rollback();
                throw new OrderUpdateException(order.getOrderId().toString());
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Retrieve an order by id</p>
     * Uses {@link com.example.ecommerce_system.dao.interfaces.OrdersDao#findById(java.sql.Connection, java.util.UUID)}.
     * The returned value is cached in the "orders" cache using Spring's cache abstraction.
     */
    @Cacheable(value = "orders", key = "'order:' + #orderId")
    public Optional<Orders> getOrder(UUID orderId) {
        try (Connection conn = dataSource.getConnection()) {
            return this.ordersDao.findById(conn, orderId);
        } catch (DaoException e) {
            throw new OrderRetrievalException(orderId.toString());
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Retrieve all orders with pagination.<p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.OrdersDao#getAllOrders(java.sql.Connection, int, int)}.
     * Results are cached in the "orders" cache using Spring Cache.
     */
    @Cacheable(value = "orders", key = "'all:' + #limit + ':' + #offset")
    public List<Orders> getAllOrders(int limit, int offset) {
        try (Connection conn = dataSource.getConnection()) {
            return this.ordersDao.getAllOrders(conn, limit, offset);
        } catch (DaoException e) {
            throw new OrderRetrievalException("all");
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Retrieve all order items for a specific order.<p>
     * Uses {@link com.example.ecommerce_system.dao.interfaces.OrderItemDao#findByOrderId(java.sql.Connection, java.util.UUID)}.
     * The returned value is cached in the "order_items" cache using Spring's cache abstraction.
     */
    @Cacheable(value = "order_items", key = "'order:' + #orderId")
    public List<OrderItem> getOrderItemsByOrderId(UUID orderId) {
        try (Connection conn = dataSource.getConnection()) {
            return this.orderItemDao.findByOrderId(conn, orderId);
        } catch (DaoException e) {
            throw new OrderItemRetrievalException(orderId.toString());
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Retrieve orders for a specific customer with pagination.<p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.OrdersDao#getCustomerOrders(java.sql.Connection, java.util.UUID, int, int)}.
     * Results are cached in the "orders" cache using Spring Cache.
     */
    @Cacheable(value = "orders", key = "'customer:' + #customerId + ':' + #limit + ':' + #offset")
    public List<Orders> getCustomerOrders(UUID customerId, int limit, int offset) {
        try (Connection conn = dataSource.getConnection()) {
            return this.ordersDao.getCustomerOrders(conn, customerId, limit, offset);
        } catch (DaoException e) {
            throw new OrderRetrievalException("customer " + customerId);
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Check if a customer has a processed order containing a specific product.<p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.OrdersDao#hasProcessedOrderWithProduct(java.sql.Connection, java.util.UUID, java.util.UUID)}.
     */
    @Cacheable(value = "orders", key="'order:processed:' + #customerId + ':' + #productId")
    public boolean hasProcessedOrderWithProduct(UUID customerId, UUID productId) {
        try (Connection conn = dataSource.getConnection()) {
            return this.ordersDao.hasProcessedOrderWithProduct(conn, customerId, productId);
        } catch (DaoException e) {
            throw new OrderRetrievalException("processed order check for customer " + customerId);
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
