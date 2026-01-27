package com.example.ecommerce_system.service;

import com.example.ecommerce_system.dto.category.CategoryRequestDto;
import com.example.ecommerce_system.dto.category.CategoryResponseDto;
import com.example.ecommerce_system.exception.category.CategoryNotFoundException;
import com.example.ecommerce_system.exception.category.CategoryDeletionException;
import com.example.ecommerce_system.exception.category.DuplicateCategoryException;
import com.example.ecommerce_system.model.Category;
import com.example.ecommerce_system.store.CategoryStore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryStore categoryStore;

    /**
     * Create a new category from the provided {@link CategoryRequestDto}
     * and persist it via {@link com.example.ecommerce_system.store.CategoryStore}.
     *
     * @param request request DTO containing name and description
     * @return created {@link CategoryResponseDto}
     * @throws DuplicateCategoryException if a category with the same name exists
     */
    public CategoryResponseDto createCategory(CategoryRequestDto request) {
        Optional<Category> existing = categoryStore.getCategoryByName(request.getName());
        if (existing.isPresent()) throw new DuplicateCategoryException(request.getName());
        Category category = new Category(
                UUID.randomUUID(),
                request.getName(),
                request.getDescription(),
                Instant.now(),
                Instant.now()
        );
        Category saved = categoryStore.createCategory(category);
        return map(saved);
    }

    private CategoryResponseDto map(Category category) {
        return CategoryResponseDto.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    /**
     * Update the category identified by {@code id} with values from the given
     * {@link CategoryRequestDto} via {@link com.example.ecommerce_system.store.CategoryStore}.
     *
     * @param id      identifier of the category to update
     * @param request request DTO with updated values
     * @return updated {@link CategoryResponseDto}
     * @throws CategoryNotFoundException if the category does not exist
     * @throws com.example.ecommerce_system.exception.category.DuplicateCategoryException if the new name conflicts with an existing category
     */
    public CategoryResponseDto updateCategory(UUID id, CategoryRequestDto request) {
        Category existingOption = categoryStore.getCategory(id).orElseThrow(
                () -> new CategoryNotFoundException(id.toString()));

        boolean isDuplicate = categoryStore.getCategoryByName(request.getName()).isPresent();
        if (isDuplicate) throw new DuplicateCategoryException(request.getName());

        Category updated = new Category(
                existingOption.getCategoryId(),
                request.getName() == null ? existingOption.getName() : request.getName(),
                request.getDescription() == null ? existingOption.getDescription() : request.getDescription(),
                existingOption.getCreatedAt(),
                Instant.now()
        );
        Category saved = categoryStore.updateCategory(updated);
        return map(saved);
    }

    /**
     * Retrieve a category by its UUID via {@link com.example.ecommerce_system.store.CategoryStore}.
     *
     * @param id category UUID
     * @return {@link CategoryResponseDto}
     * @throws CategoryNotFoundException if not found
     */
    public CategoryResponseDto getCategory(UUID id) {
        Category category = categoryStore.getCategory(id)
                .orElseThrow(() -> new CategoryNotFoundException(id.toString()));
        return map(category);
    }

    /**
     * Retrieve a category by its name via {@link com.example.ecommerce_system.store.CategoryStore}.
     *
     * @param name category name
     * @return {@link CategoryResponseDto}
     * @throws com.example.ecommerce_system.exception.category.CategoryNotFoundException if not found
     */
    public CategoryResponseDto getCategory(String name) {
        Category category = categoryStore.getCategoryByName(name)
                .orElseThrow(() -> new CategoryNotFoundException(name));
        return map(category);
    }

    /**
     * Search categories by name (substring) with paging via {@link com.example.ecommerce_system.store.CategoryStore}.
     *
     * @param query  substring to search for
     * @param limit  maximum results
     * @param offset zero-based offset
     * @return list of {@link CategoryResponseDto}
     */
    public List<CategoryResponseDto> getCategories(String query, int limit, int offset) {
        List<Category> categories = categoryStore.searchByName(query, limit, offset);
        return categories.stream().map(this::map).toList();
    }

    /**
     * Retrieve a page of all categories via {@link com.example.ecommerce_system.store.CategoryStore}.
     *
     * @param limit  maximum number of categories to return
     * @param offset zero-based offset for paging
     * @return list of {@link CategoryResponseDto}
     */
    public List<CategoryResponseDto> getAllCategories(int limit, int offset) {
        List<Category> categories = categoryStore.findAll(limit, offset);
        return categories.stream().map(this::map).toList();
    }

    /**
     * Delete a category by id.
     *
     * Ensures the category exists, then delegates to {@link com.example.ecommerce_system.store.CategoryStore#deleteCategory(java.util.UUID)}.
     *
     * @param id category UUID
     * @throws com.example.ecommerce_system.exception.category.CategoryNotFoundException if not found
     * @throws CategoryDeletionException when deletion is not possible
     */
    public void deleteCategory(UUID id) {
        categoryStore.getCategory(id).orElseThrow(() -> new CategoryNotFoundException(id.toString()));
        categoryStore.deleteCategory(id);
    }
}
