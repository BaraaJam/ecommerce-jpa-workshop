package se.lexicon.ecommercejpaworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lexicon.ecommercejpaworkshop.entity.Product;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ==========================================
    // Required Queries
    // ==========================================

    // 1. Find products by their category name
    List<Product> findByCategory_NameIgnoreCase(String categoryName);

    // 2. Find products within a specific price range
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // ==========================================
    // Optional / Advanced Queries
    // ==========================================

    // 3. Find products whose name contains a given keyword
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // 4. Find products cheaper than a given price
    List<Product> findByPriceLessThan(BigDecimal price);

    // 5. Find products ordered by price (ascending or descending)
    List<Product> findAllByOrderByPriceAsc();
    List<Product> findAllByOrderByPriceDesc();

    // 6. Count how many products exist in a specific category
    long countByCategory_NameIgnoreCase(String categoryName);

    // 7. Find products by category ID
    List<Product> findByCategory_Id(Long categoryId);
}