package se.lexicon.ecommercejpaworkshop.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"products"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "promotions")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToMany(mappedBy = "promotions")
    private Set<Product> products = new HashSet<>();

    public Promotion(String code, LocalDate startDate, LocalDate endDate) {
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
