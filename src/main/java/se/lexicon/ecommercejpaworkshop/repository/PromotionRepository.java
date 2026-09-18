package se.lexicon.ecommercejpaworkshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se.lexicon.ecommercejpaworkshop.entity.Promotion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    // ==========================================
    // Required Queries
    // ==========================================

    // 1. Find promotions that are active on a given date
    // (startDate <= date and (endDate >= date or endDate is null))
    @Query("SELECT p FROM Promotion p WHERE p.startDate <= :date AND (p.endDate >= :date OR p.endDate IS NULL)")
    List<Promotion> findActivePromotionsOnDate(@Param("date") LocalDate date);

    // ==========================================
    // Optional / Advanced Queries
    // ==========================================

    // 2. Find promotions by code
    Optional<Promotion> findByCodeIgnoreCase(String code);

    // 3. Find promotions starting after a given date
    List<Promotion> findByStartDateAfter(LocalDate date);

    // 4. Find promotions ending before a given date
    List<Promotion> findByEndDateBefore(LocalDate date);

    // 5. Find promotions that have no end date
    List<Promotion> findByEndDateIsNull();

    // 6. Find promotions active today
    @Query("SELECT p FROM Promotion p WHERE p.startDate <= CURRENT_DATE AND (p.endDate >= CURRENT_DATE OR p.endDate IS NULL)")
    List<Promotion> findActivePromotionsToday();
}
