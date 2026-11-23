package se.lexicon.ecommercejpaworkshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.ecommercejpaworkshop.dto.OrderItemRequest;
import se.lexicon.ecommercejpaworkshop.dto.OrderRequest;
import se.lexicon.ecommercejpaworkshop.dto.OrderResponse;
import se.lexicon.ecommercejpaworkshop.entity.Customer;
import se.lexicon.ecommercejpaworkshop.entity.Order;
import se.lexicon.ecommercejpaworkshop.entity.Product;
import se.lexicon.ecommercejpaworkshop.exception.ResourceNotFoundException;
import se.lexicon.ecommercejpaworkshop.mapper.OrderMapper;
import se.lexicon.ecommercejpaworkshop.repository.CustomerRepository;
import se.lexicon.ecommercejpaworkshop.repository.OrderRepository;
import se.lexicon.ecommercejpaworkshop.repository.ProductRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            CustomerRepository customerRepository,
                            ProductRepository productRepository,
                            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        // 1. INPUT: Validate arguments
        if (request == null) {
            throw new IllegalArgumentException("OrderRequest cannot be null");
        }
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        // 2. PROCESS: Fetch Customer entity (or throw ResourceNotFoundException)
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.customerId()));

        // 3. PROCESS: Fetch each Product entity and capture current state
        Map<Long, Product> productMap = new HashMap<>();
        for (OrderItemRequest itemRequest : request.items()) {
            Long productId = itemRequest.productId();
            if (!productMap.containsKey(productId)) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
                productMap.put(productId, product);
            }
        }

        // 4. PERSIST: Map to Order Entity (with OrderItems) and save to Database
        Order order = orderMapper.toEntity(request, customer, productMap);
        Order savedOrder = orderRepository.save(order);

        // 5. OUTPUT: Map Entity back to Response DTO
        return orderMapper.toResponse(savedOrder);
    }
}
