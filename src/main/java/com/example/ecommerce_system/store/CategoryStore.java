package com.example.ecommerce_system.store;

import com.example.ecommerce_system.dao.interfaces.CategoryDao;
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
     *
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CategoryDao#save(java.sql.Connection, com.example.ecommerce_system.model.Category)}.
     * On success this method evicts relevant entries in the "categories" cache via the Spring Cache abstraction
     * (see the {@link org.springframework.cache.annotation.CacheEvict} annotation applied to this method).
     *
     * @param category the category to create
     * @return the persisted {@link com.example.ecommerce_system.model.Category}
     * @throws com.example.ecommerce_system.exception.CategoryCreationException when Dao persistence fails
     * @throws com.example.ecommerce_system.exception.DatabaseConnectionException when a DB connection cannot be obtained
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
     *
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CategoryDao#update(java.sql.Connection, com.example.ecommerce_system.model.Category)}.
     * On success this method evicts relevant entries in the "categories" cache via the Spring Cache abstraction
     * (see the {@link org.springframework.cache.annotation.CacheEvict} annotation applied to this method).
     *
     * @param category the category with updated fields
     * @return the updated {@link com.example.ecommerce_system.model.Category}
     * @throws com.example.ecommerce_system.exception.CategoryUpdateException when DAO update fails
     * @throws com.example.ecommerce_system.exception.DatabaseConnectionException when a DB connection cannot be obtained
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
     *
     * Uses {@link com.example.ecommerce_system.dao.interfaces.CategoryDao#findById(java.sql.Connection, java.util.UUID)}.
     * The returned value is cached in the "categories" cache using Spring's cache abstraction
     * (see the {@link org.springframework.cache.annotation.Cacheable} annotation applied to this method).
     *
     * @param id category identifier
     * @return an {@link Optional} containing the Category when found
     * @throws com.example.ecommerce_system.exception.CategoryRetrievalException when DAO retrieval fails
     * @throws com.example.ecommerce_system.exception.DatabaseConnectionException when a DB connection cannot be obtained
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
     *
     * Uses {@link com.example.ecommerce_system.dao.interfaces.CategoryDao#findByName(java.sql.Connection, String)}.
     * The returned value is cached in the "categories" cache using Spring's cache abstraction
     * (see the {@link org.springframework.cache.annotation.Cacheable} annotation applied to this method).
     *
     * @param name category name
     * @return an {@link Optional} containing the Category when found
     * @throws com.example.ecommerce_system.exception.CategoryRetrievalException when DAO retrieval fails
     * @throws com.example.ecommerce_system.exception.DatabaseConnectionException when a DB connection cannot be obtained
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
     *
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CategoryDao#searchByName(java.sql.Connection, String, int, int)}.
     * Results are cached in the "categories" cache using Spring Cache (@Cacheable) keyed by the search parameters.
     *
     * @param query substring to search for
     * @param limit maximum results
     * @param offset zero-based offset
     * @return list of matching {@link com.example.ecommerce_system.model.Category}
     * @throws com.example.ecommerce_system.exception.CategorySearchException when DAO search fails
     * @throws com.example.ecommerce_system.exception.DatabaseConnectionException when a DB connection cannot be obtained
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
     *
     * Results are loaded via {@link com.example.ecommerce_system.dao.interfaces.CategoryDao#findAll(java.sql.Connection, int, int)}.
     * The returned page is cached in the "categories" cache using Spring Cache (@Cacheable).
     *
     * @param limit  maximum number of categories to return
     * @param offset zero-based offset for paging
     * @return list of {@link com.example.ecommerce_system.model.Category} for the requested page
     * @throws com.example.ecommerce_system.exception.CategoryRetrievalException when DAO retrieval fails
     * @throws com.example.ecommerce_system.exception.DatabaseConnectionException when a DB connection cannot be obtained
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

    @Cacheable(value = "categories", key = "'count'")
    public int count() {
        try (Connection conn = dataSource.getConnection()) {
            return categoryDao.count(conn);
        } catch (DaoException e) {
            throw new CategorySearchException("Failed to count categories");
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    @Cacheable(value = "categories", key = "'count' + #query")
    public int countByName(String query) {
        try (Connection conn = dataSource.getConnection()) {
            return categoryDao.countByName(conn, query);
        } catch (DaoException e) {
            throw new CategorySearchException("Failed to count categories by name");
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Delete a category by id inside a transaction.
     *
     * Delegates to {@link com.example.ecommerce_system.dao.interfaces.CategoryDao#delete(java.sql.Connection, java.util.UUID)}.
     * On success this method evicts relevant entries in the "categories" cache via Spring Cache.
     *
     * @param id identifier of the category to delete
     * @throws com.example.ecommerce_system.exception.CategoryDeletionException when the category has products or deletion fails
     * @throws com.example.ecommerce_system.exception.DatabaseConnectionException when a DB connection cannot be obtained
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
