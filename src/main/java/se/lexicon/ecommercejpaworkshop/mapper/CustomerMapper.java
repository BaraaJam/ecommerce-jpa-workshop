package se.lexicon.ecommercejpaworkshop.mapper;

import org.springframework.stereotype.Component;
import se.lexicon.ecommercejpaworkshop.dto.AddressResponse;
import se.lexicon.ecommercejpaworkshop.dto.CustomerRequest;
import se.lexicon.ecommercejpaworkshop.dto.CustomerResponse;
import se.lexicon.ecommercejpaworkshop.entity.Address;
import se.lexicon.ecommercejpaworkshop.entity.Customer;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request) {
        if (request == null) {
            return null;
        }

        Address address = new Address(
                request.street(),
                request.city(),
                request.zipCode()
        );

        return new Customer(
                request.firstName(),
                request.lastName(),
                request.email(),
                address
        );
    }

    public CustomerResponse toResponse(Customer customer) {
        if (customer == null) {
            return null;
        }

        AddressResponse addressResponse = null;
        if (customer.getAddress() != null) {
            addressResponse = new AddressResponse(
                    customer.getAddress().getId(),
                    customer.getAddress().getStreet(),
                    customer.getAddress().getCity(),
                    customer.getAddress().getZipCode()
            );
        }

        String fullName = customer.getFirstName() + " " + customer.getLastName();

        return new CustomerResponse(
                customer.getId(),
                fullName,
                customer.getEmail(),
                addressResponse
        );
    }
}