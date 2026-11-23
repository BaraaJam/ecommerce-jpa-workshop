package se.lexicon.ecommercejpaworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lexicon.ecommercejpaworkshop.entity.Customer;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // ==========================================
    // Required Queries
    // ==========================================

    // 1. Find a customer by their unique email
    Optional<Customer> findByEmail(String email);

    // 2. Find customers by last name (case-insensitive)
    List<Customer> findByLastNameIgnoreCase(String lastName);

    // 3. Find customers living in a specific city
    List<Customer> findByAddress_City(String city);

    // ==========================================
    // Optional / Advanced Queries
    // ==========================================

    // 4. Find customers whose email contains a given keyword
    List<Customer> findByEmailContaining(String keyword);

    // 5. Find customers created after a specific date
    List<Customer> findByCreatedAtAfter(Instant date);

    // 6.Find customers created between two dates
    List<Customer> findByCreatedAtBetween(Instant start, Instant end);

    // 7. Count how many customers live in a specific city
    long countByAddress_City(String city);

    // 8. Check if a customer exists by email
    boolean existsByEmail(String email);
}
