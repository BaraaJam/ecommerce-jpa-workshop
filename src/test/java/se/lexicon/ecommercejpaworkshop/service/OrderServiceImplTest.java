package se.lexicon.ecommercejpaworkshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lexicon.ecommercejpaworkshop.dto.OrderItemRequest;
import se.lexicon.ecommercejpaworkshop.dto.OrderRequest;
import se.lexicon.ecommercejpaworkshop.dto.OrderResponse;
import se.lexicon.ecommercejpaworkshop.entity.*;
import se.lexicon.ecommercejpaworkshop.exception.ResourceNotFoundException;
import se.lexicon.ecommercejpaworkshop.mapper.OrderMapper;
import se.lexicon.ecommercejpaworkshop.repository.CustomerRepository;
import se.lexicon.ecommercejpaworkshop.repository.OrderRepository;
import se.lexicon.ecommercejpaworkshop.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Spy
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer customer;
    private Product product;
    private OrderRequest validOrderRequest;

    @BeforeEach
    void setUp() {
        Address address = new Address("Main Street 1", "Stockholm", "12345");
        address.setId(1L);

        customer = new Customer("John", "Doe", "john@example.com", address);
        customer.setId(1L);

        Category category = new Category("Electronics");
        category.setId(1L);

        product = new Product("Gaming Mouse", new BigDecimal("49.99"), category);
        product.setId(10L);

        OrderItemRequest itemRequest = new OrderItemRequest(10L, 2);
        validOrderRequest = new OrderRequest(1L, List.of(itemRequest));
    }

    // ==========================================
    // Place Order Tests
    // ==========================================

    @Test
    @DisplayName("placeOrder: Success - Should place order, calculate prices, and return OrderResponse")
    void placeOrder_Success() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(100L);
            order.setOrderDate(Instant.now());
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                order.getOrderItems().get(0).setId(500L);
            }
            return order;
        });

        // Act
        OrderResponse response = orderService.placeOrder(validOrderRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.customerId()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).productId()).isEqualTo(10L);
        assertThat(response.items().get(0).productName()).isEqualTo("Gaming Mouse");
        assertThat(response.items().get(0).quantity()).isEqualTo(2);
        assertThat(response.items().get(0).priceAtPurchase()).isEqualByComparingTo("49.99");

        verify(customerRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(10L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("placeOrder: Customer Not Found - Should throw ResourceNotFoundException")
    void placeOrder_CustomerNotFound_ThrowsException() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.placeOrder(validOrderRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found with id: 1");

        verify(productRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("placeOrder: Product Not Found - Should throw ResourceNotFoundException")
    void placeOrder_ProductNotFound_ThrowsException() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.placeOrder(validOrderRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with id: 10");

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("placeOrder: Null Request - Should throw IllegalArgumentException")
    void placeOrder_NullRequest_ThrowsException() {
        assertThatThrownBy(() -> orderService.placeOrder(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("placeOrder: Empty Items - Should throw IllegalArgumentException")
    void placeOrder_EmptyItems_ThrowsException() {
        OrderRequest emptyItemsRequest = new OrderRequest(1L, List.of());

        assertThatThrownBy(() -> orderService.placeOrder(emptyItemsRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
