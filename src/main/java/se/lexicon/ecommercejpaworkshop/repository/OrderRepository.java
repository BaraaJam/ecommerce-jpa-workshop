package se.lexicon.ecommercejpaworkshop.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.lexicon.ecommercejpaworkshop.entity.Order;
import se.lexicon.ecommercejpaworkshop.entity.OrderStatus;
import se.lexicon.ecommercejpaworkshop.entity.Product;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ==========================================
    // Required Queries
    // ==========================================

    // 1. Find all orders belonging to a specific customer ID
    List<Order> findByCustomer_Id(Long customerId);

    // 2. Find orders by status and avoid N+1 problem by eagerly fetching orderItems (and customer)
    @EntityGraph(attributePaths = {"orderItems", "customer"})
    List<Order> findAllByOrderStatus(OrderStatus orderStatus);

    // ==========================================
    // Optional / Advanced Queries
    // ==========================================

    // 3. Find orders created after a specific date
    List<Order> findByOrderDateAfter(Instant date);

    // 4. Find orders created between two dates
    List<Order> findByOrderDateBetween(Instant startDate, Instant endDate);

    // 5. Find orders that contain a specific product
    List<Order> findDistinctByOrderItems_Product(Product product);
    List<Order> findDistinctByOrderItems_Product_Id(Long productId);

    // 6. Count orders by status
    long countByOrderStatus(OrderStatus orderStatus);

    // 7. Find orders by customer ID and status
    List<Order> findByCustomer_IdAndOrderStatus(Long customerId, OrderStatus orderStatus);
}
