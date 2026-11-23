package se.lexicon.ecommercejpaworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import se.lexicon.ecommercejpaworkshop.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // ==========================================
    // Required Queries
    // ==========================================

    // 1. Find category by name
    Optional<Category> findByNameIgnoreCase(String name);

    // 2. Check if a category exist by name
    boolean existsByNameIgnoreCase(String name);

    // ==========================================
    // Optional / Advanced Queries
    // ==========================================

    // 3. Find categories whose name contains a given keyword
    List<Category> findByNameContainingIgnoreCase(String name);

    // 4. Count how many categories exist
    // Note: Can also use inherited JpaRepository.count().
    // @Query is needed here because Spring cannot derive 'countAll()' without an entity property.
    @Query("SELECT COUNT(c) FROM Category c")
    long countAll();
}
