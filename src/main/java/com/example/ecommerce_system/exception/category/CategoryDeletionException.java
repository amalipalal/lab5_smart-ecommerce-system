package com.example.ecommerce_system.exception.category;

/**
 * Thrown when a category cannot be deleted, e.g. due to existing products referencing it,
 * or when deletion fails for other reasons.
 */
public class CategoryDeletionException extends RuntimeException {
    public CategoryDeletionException(String categoryId) {
        super("Category " + categoryId + " cannot be deleted because it has associated products.");
    }

    public CategoryDeletionException(String categoryId, Throwable cause) {
        super("Failed to delete category " + categoryId, cause);
    }
}
