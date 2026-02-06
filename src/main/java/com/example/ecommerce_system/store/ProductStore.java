package com.example.ecommerce_system.store;

import com.example.ecommerce_system.dao.interfaces.ProductDao;
import com.example.ecommerce_system.exception.product.*;
import com.example.ecommerce_system.model.Product;
import com.example.ecommerce_system.dto.product.ProductFilter;
import com.example.ecommerce_system.exception.*;
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
public class ProductStore {
    private final DataSource dataSource;
    private final ProductDao productDao;

    /**
     * Persist a new {@link com.example.ecommerce_system.model.Product} inside a transaction.
     * <p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.ProductDao#save(java.sql.Connection, com.example.ecommerce_system.model.Product)}.
     * On success this method evicts relevant entries in the "products" cache via Spring Cache.
     */
    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(Product product) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                this.productDao.save(conn, product);
                conn.commit();
                return product;
            } catch (DaoException e) {
                System.out.println(e.getMessage());
                conn.rollback();
                throw new ProductCreationException(product.getName());
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Update an existing {@link com.example.ecommerce_system.model.Product} inside a transaction.
     * <p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.ProductDao#update(java.sql.Connection, com.example.ecommerce_system.model.Product)}.
     * On success this method evicts relevant entries in the "products" cache via Spring Cache.
     */
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(Product product) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                this.productDao.update(conn, product);
                conn.commit();
                return product;
            } catch (DaoException e) {
                conn.rollback();
                throw new ProductUpdateException(product.getProductId().toString());
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Delete a product by id inside a transaction.
     * <p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.ProductDao#deleteById(java.sql.Connection, java.util.UUID)}.
     * On success this method evicts relevant entries in the "products" cache via Spring Cache.
     */
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(UUID productId) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                this.productDao.deleteById(conn, productId);
                conn.commit();
            } catch (DaoException e) {
                conn.rollback();
                throw new DeleteProductException(productId.toString());
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Load a product by id.
     * <p>
     * Uses {@link com.example.ecommerce_system.dao.interfaces.ProductDao#findById(java.sql.Connection, java.util.UUID)}.
     * Results are cached in the "products" cache using Spring Cache.
     */
    @Cacheable(value = "products", key = "'product:' + #productId")
    public Optional<Product> getProduct(UUID productId) {
        try (Connection conn = dataSource.getConnection()) {
            return this.productDao.findById(conn, productId);
        } catch (DaoException e) {
            throw new ProductRetrievalException(productId.toString());
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Retrieve all products with pagination.
     * <p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.ProductDao#findAll(java.sql.Connection, int, int)}.
     * Results are cached in the "products" cache using Spring Cache.
     */
    @Cacheable(value = "products", key = "'all:' + #limit + ':' + #offset")
    public List<Product> getAllProducts(int limit, int offset) {
        try (Connection conn = dataSource.getConnection()) {
            return this.productDao.findAll(conn, limit, offset);
        } catch (DaoException e) {
            throw new ProductRetrievalException("all");
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Search products using a {@link ProductFilter} with paging.
     * <p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.ProductDao#findFiltered(java.sql.Connection, ProductFilter, int, int)}.
     * Results are cached in the "products" cache using Spring Cache.
     */
    @Cacheable(value = "products", key = "'search:' + #filter.hashCode() + ':' + #limit + ':' + #offset")
    public List<Product> searchProducts(ProductFilter filter, int limit, int offset) {
        try (Connection conn = dataSource.getConnection()) {
            return this.productDao.findFiltered(conn, filter, limit, offset);
        } catch (DaoException e) {
            throw new ProductSearchException("Failed to search with filter");
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Count products matching a filter.
     * <p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.ProductDao#countFiltered(java.sql.Connection, ProductFilter)}.
     * Results are cached in the "products" cache using Spring Cache.
     */
    @Cacheable(value = "products", key = "'count:' + #filter.hashCode()")
    public int countProductsByFilter(ProductFilter filter) {
        try (Connection conn = dataSource.getConnection()) {
            return this.productDao.countFiltered(conn, filter);
        } catch (DaoException e) {
            throw new ProductSearchException("Failed to count search results with filter");
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Update stock for multiple products inside a transaction.
     */
    @CacheEvict(value = "products", allEntries = true)
    public void updateProductStocks(List<UUID> productIds, List<Integer> stockChanges) {
        if (productIds.size() != stockChanges.size()) {
            throw new IllegalArgumentException("Product IDs and stock changes must have the same size");
        }

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (int i = 0; i < productIds.size(); i++) {
                    UUID productId = productIds.get(i);
                    int newStock = stockChanges.get(i);
                    this.productDao.updateStock(conn, productId, newStock);
                }
                conn.commit();
            } catch (DaoException e) {
                conn.rollback();
                throw new ProductUpdateException("Failed to update product stocks: " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}