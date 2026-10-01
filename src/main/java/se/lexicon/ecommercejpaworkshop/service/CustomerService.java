package se.lexicon.ecommercejpaworkshop.service;

import se.lexicon.ecommercejpaworkshop.dto.CustomerRequest;
import se.lexicon.ecommercejpaworkshop.dto.CustomerResponse;

public interface CustomerService {
    CustomerResponse register(CustomerRequest request);
    CustomerResponse findById(Long id);
    CustomerResponse update(Long id, CustomerRequest request);
}
