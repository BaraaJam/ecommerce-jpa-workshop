package se.lexicon.ecommercejpaworkshop.mapper;

import org.springframework.stereotype.Component;
import se.lexicon.ecommercejpaworkshop.dto.OrderItemRequest;
import se.lexicon.ecommercejpaworkshop.dto.OrderItemResponse;
import se.lexicon.ecommercejpaworkshop.dto.OrderRequest;
import se.lexicon.ecommercejpaworkshop.dto.OrderResponse;
import se.lexicon.ecommercejpaworkshop.entity.Customer;
import se.lexicon.ecommercejpaworkshop.entity.Order;
import se.lexicon.ecommercejpaworkshop.entity.OrderItem;
import se.lexicon.ecommercejpaworkshop.entity.OrderStatus;
import se.lexicon.ecommercejpaworkshop.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class OrderMapper {

    // Converts Order Entity -> OrderResponse DTO
    public OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemResponse> itemResponses = (order.getOrderItems() != null)
                ? order.getOrderItems().stream().map(this::toItemResponse).toList()
                : List.of();

        Long customerId = (order.getCustomer() != null) ? order.getCustomer().getId() : null;

        return new OrderResponse(
                order.getId(),
                order.getOrderDate(),
                order.getOrderStatus(),
                customerId,
                itemResponses
        );
    }

    // Helper: Converts OrderItem Entity -> OrderItemResponse DTO
    public OrderItemResponse toItemResponse(OrderItem item) {
        if (item == null) {
            return null;
        }

        Long productId = (item.getProduct() != null) ? item.getProduct().getId() : null;
        String productName = (item.getProduct() != null) ? item.getProduct().getName() : null;

        return new OrderItemResponse(
                item.getId(),
                productId,
                productName,
                item.getQuantity(),
                item.getPriceAtPurchase()
        );
    }

    // Converts OrderRequest (DTO) + Customer Entity + Product Map -> Order Entity
    public Order toEntity(OrderRequest request, Customer customer, Map<Long, Product> productMap) {
        if (request == null) {
            return null;
        }

        Order order = new Order(OrderStatus.CREATED);
        order.setCustomer(customer);

        if (request.items() != null && productMap != null) {
            for (OrderItemRequest itemRequest : request.items()) {
                Product product = productMap.get(itemRequest.productId());
                if (product != null) {
                    BigDecimal priceAtPurchase = product.getPrice();
                    OrderItem orderItem = new OrderItem(product, itemRequest.quantity(), priceAtPurchase);
                    order.addOrderItem(orderItem);
                }
            }
        }

        return order;
    }
}
