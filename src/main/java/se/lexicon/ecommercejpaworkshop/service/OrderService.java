package se.lexicon.ecommercejpaworkshop.service;

import se.lexicon.ecommercejpaworkshop.dto.OrderRequest;
import se.lexicon.ecommercejpaworkshop.dto.OrderResponse;

public interface OrderService {

    OrderResponse placeOrder(OrderRequest request);
}
