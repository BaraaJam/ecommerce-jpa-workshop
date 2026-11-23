package se.lexicon.ecommercejpaworkshop.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "customer")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false,unique = true, length = 100)
    private String nickname;

    @Column(nullable = false, length = 100)
    private String phoneNumber;

    @Column(length = 500)
    private String bio;

    @OneToOne(mappedBy = "profile")
    private Customer customer;

    public UserProfile(String nickname, String phoneNumber, String bio) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
    }
}
