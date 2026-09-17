package se.lexicon.ecommercejpaworkshop.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "orderItems")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus orderStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems =  new ArrayList<>();

    public Order(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Order(Instant orderDate, OrderStatus orderStatus) {
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

    @PrePersist
    protected void onCreate() {
        if (this.orderDate == null) {
            this.orderDate = Instant.now();
        }
        if (this.orderItems == null || this.orderItems.isEmpty()) {
            throw new IllegalStateException("An order must contain at least one order item before it can be saved.");
        }
    }

    public void addOrderItem(OrderItem orderItem) {
        if  (orderItem == null) {
            throw new IllegalArgumentException("OrderItem cannot be null");
        }
        if (!this.orderItems.contains(orderItem)) {
            this.orderItems.add(orderItem);
            orderItem.setOrder(this);
        }
    }

    public void removeOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            throw new IllegalArgumentException("OrderItem cannot be null");
        }
        if (this.orderItems.remove(orderItem)) {
            orderItem.setOrder(null);
        }
    }

}
