package se.lexicon.ecommercejpaworkshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.lexicon.ecommercejpaworkshop.dto.CustomerRequest;
import se.lexicon.ecommercejpaworkshop.dto.CustomerResponse;
import se.lexicon.ecommercejpaworkshop.entity.Address;
import se.lexicon.ecommercejpaworkshop.entity.Customer;
import se.lexicon.ecommercejpaworkshop.exception.DuplicateEmailException;
import se.lexicon.ecommercejpaworkshop.exception.ResourceNotFoundException;
import se.lexicon.ecommercejpaworkshop.mapper.CustomerMapper;
import se.lexicon.ecommercejpaworkshop.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    @Transactional
    public CustomerResponse register(CustomerRequest request) {
        // 1. INPUT: Validate arguments
        if (request == null) {
            throw new IllegalArgumentException("CustomerRequest cannot be null");
        }

        // 2. PROCESS: Enforce business rules
        if (customerRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException("Email is already registered: " + request.email());
        }

        // 3. PERSIST: DTO -> Entity -> Database
        Customer customer = customerMapper.toEntity(request);
        Customer savedCustomer = customerRepository.save(customer);

        // 4. OUTPUT: Entity -> Response DTO
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        // 1. INPUT: Check that id is valid
        if (id == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        // 2. PROCESS: Fetch from repo or throw ResourceNotFoundException
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        // 3. OUTPUT: Map entity to response DTO
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        // 1. INPUT: Validate arguments
        if (id == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        if (request == null) {
            throw new IllegalArgumentException("CustomerRequest cannot be null");
        }

        // 2. PROCESS: Fetch existing entity or throw ResourceNotFoundException
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        // Business Rule: Check email uniqueness if email has changed
        if (!request.email().equalsIgnoreCase(customer.getEmail())) {
            if (customerRepository.existsByEmail(request.email())) {
                throw new DuplicateEmailException("Email is already registered: " + request.email());
            }
        }

        // 3. PERSIST: Mutate entity fields & save to database
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setEmail(request.email());

        if (customer.getAddress() != null) {
            customer.getAddress().setStreet(request.street());
            customer.getAddress().setCity(request.city());
            customer.getAddress().setZipCode(request.zipCode());
        } else {
            customer.setAddress(new Address(request.street(), request.city(), request.zipCode()));
        }

        Customer updatedCustomer = customerRepository.save(customer);

        // 4. OUTPUT: Map updated entity to response DTO
        return customerMapper.toResponse(updatedCustomer);
    }
}
