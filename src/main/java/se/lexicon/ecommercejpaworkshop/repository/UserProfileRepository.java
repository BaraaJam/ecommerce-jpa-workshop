package se.lexicon.ecommercejpaworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lexicon.ecommercejpaworkshop.entity.UserProfile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    // ==========================================
    // Required Queries
    // ==========================================

    // 1. Find a profile by their nickname
    Optional<UserProfile> findByNickname(String nickname);

    // 2. Search for profiles by partial phone number
    List<UserProfile> findByPhoneNumberContaining(String phoneNumber);

    // ==========================================
    // Optional / Advanced Queries
    // ==========================================

    // 3. Find profiles where bio is not null
    List<UserProfile> findByBioIsNotNull();

    // 4. Find profiles by nickname starting with a prefix
    List<UserProfile> findByNicknameStartingWith(String prefix);

    // 5. Find profiles created after a specific date
    List<UserProfile> findByCustomerCreatedAtAfter(Instant date);

    // 6. Count profiles with a specific phone number prefix
    long countByPhoneNumberStartingWith(String prefix);
}