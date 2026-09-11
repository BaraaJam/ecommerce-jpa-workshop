package se.lexicon.ecommercejpaworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lexicon.ecommercejpaworkshop.entity.Address;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // ==========================================
    // Required Queries
    // ==========================================

    // 1. Find all addresses in a specific zip code area
    List<Address> findByZipCode(String zipCode);

    // ==========================================
    // Optional / Advanced Queries
    // ==========================================

    // 2. Find all addresses in a specific city
    List<Address> findByCity(String city);

    // 3. Find addresses by street name
    List<Address> findByStreet(String street);

    // 4. Count how many customers live in a given zip code
    long countByZipCode(String zipCode);

    // 5. Find addresses where zip code starts with a prefix
    List<Address> findByZipCodeStartingWith(String prefix);
}