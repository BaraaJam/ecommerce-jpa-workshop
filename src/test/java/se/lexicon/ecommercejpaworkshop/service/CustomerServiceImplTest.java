package se.lexicon.ecommercejpaworkshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import se.lexicon.ecommercejpaworkshop.dto.CustomerRequest;
import se.lexicon.ecommercejpaworkshop.dto.CustomerResponse;
import se.lexicon.ecommercejpaworkshop.entity.Address;
import se.lexicon.ecommercejpaworkshop.entity.Customer;
import se.lexicon.ecommercejpaworkshop.exception.DuplicateEmailException;
import se.lexicon.ecommercejpaworkshop.exception.ResourceNotFoundException;
import se.lexicon.ecommercejpaworkshop.mapper.CustomerMapper;
import se.lexicon.ecommercejpaworkshop.repository.CustomerRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Spy
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private CustomerRequest validRequest;
    private Customer existingCustomer;

    @BeforeEach
    void setUp() {
        validRequest = new CustomerRequest(
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                "Main Street 1",
                "Stockholm",
                "12345"
        );

        Address address = new Address("Main Street 1", "Stockholm", "12345");
        address.setId(1L);

        existingCustomer = new Customer("John", "Doe", "john.doe@example.com", address);
        existingCustomer.setId(1L);
    }

    // ==========================================
    // Register Tests
    // ==========================================

    @Test
    @DisplayName("register: Success - Should save and return CustomerResponse")
    void register_Success() {
        // Arrange
        when(customerRepository.existsByEmail(validRequest.email())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            saved.setId(1L);
            if (saved.getAddress() != null) saved.getAddress().setId(1L);
            return saved;
        });

        // Act
        CustomerResponse response = customerService.register(validRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.fullName()).isEqualTo("John Doe");
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.addressResponse()).isNotNull();
        assertThat(response.addressResponse().city()).isEqualTo("Stockholm");

        verify(customerRepository, times(1)).existsByEmail(validRequest.email());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("register: Duplicate Email - Should throw DuplicateEmailException")
    void register_DuplicateEmail_ThrowsException() {
        // Arrange
        when(customerRepository.existsByEmail(validRequest.email())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> customerService.register(validRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email is already registered");

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("register: Null Request - Should throw IllegalArgumentException")
    void register_NullRequest_ThrowsException() {
        assertThatThrownBy(() -> customerService.register(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ==========================================
    // findById Tests
    // ==========================================

    @Test
    @DisplayName("findById: Success - Should return CustomerResponse")
    void findById_Success() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));

        // Act
        CustomerResponse response = customerService.findById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.fullName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("findById: Not Found - Should throw ResourceNotFoundException")
    void findById_NotFound_ThrowsException() {
        // Arrange
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> customerService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found with id: 99");
    }

    @Test
    @DisplayName("findById: Null ID - Should throw IllegalArgumentException")
    void findById_NullId_ThrowsException() {
        assertThatThrownBy(() -> customerService.findById(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ==========================================
    // Update Tests
    // ==========================================

    @Test
    @DisplayName("update: Success - Should update customer details")
    void update_Success() {
        // Arrange
        CustomerRequest updateRequest = new CustomerRequest(
                "Johnny",
                "Smith",
                "john.doe@example.com", // Same email
                "newpassword",
                "Second Street 2",
                "Gothenburg",
                "54321"
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CustomerResponse response = customerService.update(1L, updateRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.fullName()).isEqualTo("Johnny Smith");
        assertThat(response.addressResponse().city()).isEqualTo("Gothenburg");
    }

    @Test
    @DisplayName("update: Email Conflict - Should throw DuplicateEmailException")
    void update_EmailConflict_ThrowsException() {
        // Arrange
        CustomerRequest updateRequest = new CustomerRequest(
                "John",
                "Doe",
                "taken@example.com", // New email
                "password",
                "Street",
                "City",
                "123"
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByEmail("taken@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> customerService.update(1L, updateRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email is already registered");

        verify(customerRepository, never()).save(any(Customer.class));
    }
}
