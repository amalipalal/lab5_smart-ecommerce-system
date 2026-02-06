package com.example.ecommerce_system.service;

import com.example.ecommerce_system.dto.category.CategoryRequestDto;
import com.example.ecommerce_system.dto.category.CategoryResponseDto;
import com.example.ecommerce_system.exception.category.CategoryNotFoundException;
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
     * Create a new category with the provided name and description.
     * Validates that no category with the same name already exists before creation.
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
     * Update the category identified by the given ID with new values.
     * Validates that the category exists and the new name doesn't conflict with existing categories.
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

    public CategoryResponseDto getCategory(UUID id) {
        Category category = categoryStore.getCategory(id)
                .orElseThrow(() -> new CategoryNotFoundException(id.toString()));
        return map(category);
    }

    public CategoryResponseDto getCategory(String name) {
        Category category = categoryStore.getCategoryByName(name)
                .orElseThrow(() -> new CategoryNotFoundException(name));
        return map(category);
    }

    public List<CategoryResponseDto> getCategories(String query, int limit, int offset) {
        List<Category> categories = categoryStore.searchByName(query, limit, offset);
        return categories.stream().map(this::map).toList();
    }

    public List<CategoryResponseDto> getAllCategories(int limit, int offset) {
        List<Category> categories = categoryStore.findAll(limit, offset);
        return categories.stream().map(this::map).toList();
    }

    /**
     * Delete a category by ID.
     * Validates that the category exists before deletion.
     */
    public void deleteCategory(UUID id) {
        categoryStore.getCategory(id).orElseThrow(() -> new CategoryNotFoundException(id.toString()));
        categoryStore.deleteCategory(id);
    }
}
