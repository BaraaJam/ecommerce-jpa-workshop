package se.lexicon.ecommercejpaworkshop.dto;

import se.lexicon.ecommercejpaworkshop.entity.OrderStatus;

import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Instant orderDate,
        OrderStatus status,
        Long customerId,
        List<OrderItemResponse> items
) {}
