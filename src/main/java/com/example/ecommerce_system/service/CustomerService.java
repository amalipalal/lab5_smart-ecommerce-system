package com.example.ecommerce_system.service;

import com.example.ecommerce_system.dto.customer.CustomerRequestDto;
import com.example.ecommerce_system.dto.customer.CustomerResponseDto;
import com.example.ecommerce_system.exception.customer.CustomerNotFoundException;
import com.example.ecommerce_system.model.Customer;
import com.example.ecommerce_system.store.CustomerStore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerStore customerStore;

    /**
     * Retrieve a customer by id.
     *
     * Uses {@link com.example.ecommerce_system.store.CustomerStore#getCustomer(java.util.UUID)} and
     * throws {@link com.example.ecommerce_system.exception.customer.CustomerNotFoundException}
     * when no customer is found.
     *
     * @param customerId the customer identifier
     * @return a {@link com.example.ecommerce_system.dto.customer.CustomerResponseDto} for the found customer
     * @throws com.example.ecommerce_system.exception.customer.CustomerNotFoundException if the customer does not exist
     */
    public CustomerResponseDto getCustomer(UUID customerId) {
        Customer customer = this.customerStore.getCustomer(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId.toString()));
        return map(customer);
    }

    /**
     * Retrieve all customers with pagination.
     *
     * Delegates to {@link com.example.ecommerce_system.store.CustomerStore#getAllCustomers(int, int)}.
     *
     * @param limit maximum number of results
     * @param offset zero-based offset for paging
     * @return list of {@link com.example.ecommerce_system.dto.customer.CustomerResponseDto}
     */
    public List<CustomerResponseDto> getAllCustomers(int limit, int offset) {
        List<Customer> customers = this.customerStore.getAllCustomers(limit, offset);
        return customers.stream().map(this::map).toList();
    }

    /**
     * Search for customers by query string matching first name, last name, or email.
     *
     * Delegates to {@link com.example.ecommerce_system.store.CustomerStore#searchCustomers(String, int, int)}.
     *
     * @param query search query string
     * @param limit maximum number of results
     * @param offset zero-based offset for paging
     * @return list of {@link com.example.ecommerce_system.dto.customer.CustomerResponseDto}
     */
    public List<CustomerResponseDto> searchCustomers(String query, int limit, int offset) {
        List<Customer> customers = this.customerStore.searchCustomers(query, limit, offset);
        return customers.stream().map(this::map).toList();
    }

    /**
     * Update customer's details.
     *
     * Validates presence of the customer via {@link com.example.ecommerce_system.store.CustomerStore#getCustomer(java.util.UUID)} and
     * delegates persistence to {@link com.example.ecommerce_system.store.CustomerStore#updateCustomer(com.example.ecommerce_system.model.Customer)}.
     *
     * @param customerId the customer id to update
     * @param request the incoming {@link com.example.ecommerce_system.dto.customer.CustomerRequestDto} with isActive field
     * @return updated {@link com.example.ecommerce_system.dto.customer.CustomerResponseDto}
     * @throws com.example.ecommerce_system.exception.customer.CustomerNotFoundException if the target customer does not exist
     */
    public CustomerResponseDto updateCustomer(UUID customerId, CustomerRequestDto request) {
        Customer existing = this.customerStore.getCustomer(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId.toString()));

        Customer updated = Customer.builder()
                .customerId(existing.getCustomerId())
                .firstName(existing.getFirstName())
                .lastName(existing.getLastName())
                .email(existing.getEmail())
                .phone(request.getPhone() != null ? request.getPhone() : existing.getPhone())
                .isActive(request.getIsActive() != null ? request.getIsActive() : existing.isActive())
                .createdAt(existing.getCreatedAt())
                .build();

        this.customerStore.updateCustomer(updated);
        return map(updated);
    }

    private CustomerResponseDto map(Customer customer) {
        return CustomerResponseDto.builder()
                .customerId(customer.getCustomerId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .isActive(customer.isActive())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}
