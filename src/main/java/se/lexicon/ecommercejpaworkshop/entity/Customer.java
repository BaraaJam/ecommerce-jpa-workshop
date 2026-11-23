package se.lexicon.ecommercejpaworkshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"profile", "orders"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(nullable = false, updatable = false, name = "created_at")
    private Instant createdAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private UserProfile profile;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders =  new ArrayList<>();

    public Customer(String firstName, String lastName, String email, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public void setProfile(UserProfile profile) {
        if (this.profile == profile) {
            return;
        }

        if (this.profile != null) {
            this.profile.setCustomer(null);
        }

        this.profile = profile;

        if (profile != null) {
            profile.setCustomer(this);
        }
    }

    public void addOrder(Order order) {
        if  (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (!this.orders.contains(order)) {
            this.orders.add(order);
            order.setCustomer(this);
        }
    }

    public void removeOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (this.orders.remove(order)) {
            order.setCustomer(null);
        }
    }
}
