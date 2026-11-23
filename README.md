# E-commerce JPA Workshop

A Spring Boot backend for a small online shop, built step by step during the Spring Data JPA workshop at **Lexicon Växjö**. It covers how entities relate to each other, how to query them with Spring Data repositories, and how a service layer with DTOs and transactions sits on top.

## Tech stack

Java 25 · Spring Boot 4 · Spring Data JPA (Hibernate) · Bean Validation · H2 (dev) · JUnit

## Domain model

| Entity | Relations |
|---|---|
| Customer | one-to-one **UserProfile** and **Address** · one-to-many **Order** |
| Order | many-to-one **Customer** · one-to-many **OrderItem** · status via `OrderStatus` |
| OrderItem | many-to-one **Order** and **Product** |
| Product | many-to-one **Category** · many-to-many **Promotion** |
| Category, Promotion, Address, UserProfile | supporting entities |

## What is implemented

- **Repositories** with derived queries, e.g. `findByPriceBetween`, `findByCreatedAtBetween` and `countByOrderStatus`, plus JPQL `@Query` methods such as *active promotions on a date*.
- **Service layer** (`CustomerService`, `ProductService`, `OrderService`) with request/response DTOs and mappers, so entities are never exposed directly.
- **Order placement** in one `@Transactional` method: the order and its items are saved together or not at all.
- **Error handling** with `ResourceNotFoundException` and `DuplicateEmailException`.
- **Unit tests** for all three services.

## Run it

```sh
./mvnw spring-boot:run      # starts with the dev profile (H2 in memory)
./mvnw test                 # runs the unit tests
```

## Work history

The workshop was done in three parts on separate branches (`feature/jpa-part1` → `part2` → `part3`) and merged into `main` with a pull request.
