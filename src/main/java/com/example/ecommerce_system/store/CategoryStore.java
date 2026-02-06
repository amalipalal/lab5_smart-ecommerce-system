package com.example.ecommerce_system.store;

import com.example.ecommerce_system.dao.interfaces.CategoryDao;
import com.example.ecommerce_system.exception.category.*;
import com.example.ecommerce_system.model.Category;
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
public class CategoryStore {
    private final DataSource dataSource;
    private final CategoryDao categoryDao;

    /**
     * Persist a new {@link com.example.ecommerce_system.model.Category} within a transaction.
     * <p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CategoryDao#save(java.sql.Connection, com.example.ecommerce_system.model.Category)}.
     * On success this method evicts relevant entries in the "categories" cache via the Spring Cache abstraction
     * (see the {@link org.springframework.cache.annotation.CacheEvict} annotation applied to this method).
     */
    @CacheEvict(value = "categories", allEntries = true)
    public Category createCategory(Category category) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                categoryDao.save(conn, category);
                conn.commit();
                return category;
            } catch (DaoException e) {
                conn.rollback();
                throw new CategoryCreationException(category.getName());
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Update an existing {@link com.example.ecommerce_system.model.Category} inside a transaction.
     * <p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CategoryDao#update(java.sql.Connection, com.example.ecommerce_system.model.Category)}.
     * On success this method evicts relevant entries in the "categories" cache via the Spring Cache abstraction
     * (see the {@link org.springframework.cache.annotation.CacheEvict} annotation applied to this method).
     */
    @CacheEvict(value = "categories", allEntries = true)
    public Category updateCategory(Category category) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                categoryDao.update(conn, category);
                conn.commit();
                return category;
            } catch (DaoException e) {
                conn.rollback();
                throw new CategoryUpdateException(category.getCategoryId().toString());
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Load a category by id.
     * <p>
     * Uses {@link com.example.ecommerce_system.dao.interfaces.CategoryDao#findById(java.sql.Connection, java.util.UUID)}.
     * The returned value is cached in the "categories" cache using Spring's cache abstraction
     * (see the {@link org.springframework.cache.annotation.Cacheable} annotation applied to this method).
     */
    @Cacheable(value = "categories", key = "'category:' + #id")
    public Optional<Category> getCategory(UUID id) {
        try (Connection conn = dataSource.getConnection()) {
            return categoryDao.findById(conn, id);
        } catch (DaoException e) {
            throw new CategoryRetrievalException(id.toString());
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Load a category by name.
     * <p>
     * Uses {@link com.example.ecommerce_system.dao.interfaces.CategoryDao#findByName(java.sql.Connection, String)}.
     * The returned value is cached in the "categories" cache using Spring's cache abstraction
     * (see the {@link org.springframework.cache.annotation.Cacheable} annotation applied to this method).
     */
    @Cacheable(value = "categories", key = "'name:' + #name")
    public Optional<Category> getCategoryByName(String name) {
        try (Connection conn = dataSource.getConnection()) {
            return categoryDao.findByName(conn, name);
        } catch (DaoException e) {
            throw new CategoryRetrievalException(name);
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Search categories by name with simple paging.
     * <p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CategoryDao#searchByName(java.sql.Connection, String, int, int)}.
     * Results are cached in the "categories" cache using Spring Cache (@Cacheable) keyed by the search parameters.
     */
    @Cacheable(value = "categories", key = "'search:' + #query + ':' + #limit + ':' + #offset")
    public List<Category> searchByName(String query, int limit, int offset) {
        try (Connection conn = dataSource.getConnection()) {
            return categoryDao.searchByName(conn, query, limit, offset);
        } catch (DaoException e) {
            throw new CategorySearchException("Failed to search categories");
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Retrieve a page of all categories.
     * <p>
     * Results are loaded via {@link com.example.ecommerce_system.dao.interfaces.CategoryDao#findAll(java.sql.Connection, int, int)}.
     * The returned page is cached in the "categories" cache using Spring Cache (@Cacheable).
     */
    @Cacheable(value = "categories", key = "'all:' + #limit + ':' + #offset")
    public List<Category> findAll(int limit, int offset) {
        try (Connection conn = dataSource.getConnection()) {
            return categoryDao.findAll(conn, limit, offset);
        } catch (DaoException e) {
            throw new CategoryRetrievalException("all");
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Delete a category by id inside a transaction.
     * <p>
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CategoryDao#delete(java.sql.Connection, java.util.UUID)}.
     * On success this method evicts relevant entries in the "categories" cache via Spring Cache.
     */
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(UUID id) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                categoryDao.delete(conn, id);
                conn.commit();
            } catch (CategoryDeletionException e) {
                try { conn.rollback(); } catch (SQLException ignore) {}
                throw e;
            } catch (DaoException e) {
                try { conn.rollback(); } catch (SQLException ignore) {}
                throw new CategoryDeletionException(id.toString(), e);
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
