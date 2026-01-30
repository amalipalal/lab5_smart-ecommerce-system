package com.example.ecommerce_system;

import com.example.ecommerce_system.dto.customer.CustomerRequestDto;
import com.example.ecommerce_system.dto.customer.CustomerResponseDto;
import com.example.ecommerce_system.exception.customer.CustomerNotFoundException;
import com.example.ecommerce_system.model.Customer;
import com.example.ecommerce_system.service.CustomerService;
import com.example.ecommerce_system.store.CustomerStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerStore customerStore;

    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("Should get customer by id successfully")
    void shouldGetCustomerByIdSuccessfully() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder()
                .customerId(id)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+233123456789")
                .isActive(true)
                .createdAt(Instant.now())
                .build();

        when(customerStore.getCustomer(id)).thenReturn(Optional.of(customer));

        CustomerResponseDto response = customerService.getCustomer(id);

        Assertions.assertEquals(id, response.getCustomerId());
        Assertions.assertEquals("John", response.getFirstName());
        Assertions.assertEquals("john@example.com", response.getEmail());
        verify(customerStore).getCustomer(id);
    }

    @Test
    @DisplayName("Should throw error when customer not found by id")
    void shouldThrowWhenCustomerNotFoundById() {
        UUID id = UUID.randomUUID();

        when(customerStore.getCustomer(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomer(id)
        );

        verify(customerStore).getCustomer(id);
    }

    @Test
    @DisplayName("Should get all customers successfully")
    void shouldGetAllCustomersSuccessfully() {
        List<Customer> customers = List.of(
                Customer.builder()
                        .customerId(UUID.randomUUID())
                        .firstName("John")
                        .lastName("Doe")
                        .email("john@example.com")
                        .phone("+233123456789")
                        .isActive(true)
                        .createdAt(Instant.now())
                        .build(),
                Customer.builder()
                        .customerId(UUID.randomUUID())
                        .firstName("Jane")
                        .lastName("Smith")
                        .email("jane@example.com")
                        .phone("+233987654321")
                        .isActive(true)
                        .createdAt(Instant.now())
                        .build()
        );

        when(customerStore.getAllCustomers(10, 0)).thenReturn(customers);

        List<CustomerResponseDto> result = customerService.getAllCustomers(10, 0);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("John", result.get(0).getFirstName());
        Assertions.assertEquals("Jane", result.get(1).getFirstName());
        verify(customerStore).getAllCustomers(10, 0);
    }

    @Test
    @DisplayName("Should search customers successfully")
    void shouldSearchCustomersSuccessfully() {
        String query = "john";
        List<Customer> customers = List.of(
                Customer.builder()
                        .customerId(UUID.randomUUID())
                        .firstName("John")
                        .lastName("Doe")
                        .email("john@example.com")
                        .phone("+233123456789")
                        .isActive(true)
                        .createdAt(Instant.now())
                        .build()
        );

        when(customerStore.searchCustomers(query, 10, 0)).thenReturn(customers);

        List<CustomerResponseDto> result = customerService.searchCustomers(query, 10, 0);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("John", result.get(0).getFirstName());
        verify(customerStore).searchCustomers(query, 10, 0);
    }

    @Test
    @DisplayName("Should return empty list when no customers match search")
    void shouldReturnEmptyListWhenNoCustomersMatchSearch() {
        String query = "nonexistent";

        when(customerStore.searchCustomers(query, 10, 0)).thenReturn(List.of());

        List<CustomerResponseDto> result = customerService.searchCustomers(query, 10, 0);

        Assertions.assertEquals(0, result.size());
        verify(customerStore).searchCustomers(query, 10, 0);
    }

    @Test
    @DisplayName("Should update customer phone successfully")
    void shouldUpdateCustomerPhoneSuccessfully() {
        UUID id = UUID.randomUUID();
        CustomerRequestDto request = new CustomerRequestDto("+233111222333", null);

        Customer existing = Customer.builder()
                .customerId(id)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+233123456789")
                .isActive(true)
                .createdAt(Instant.now())
                .build();

        when(customerStore.getCustomer(id)).thenReturn(Optional.of(existing));

        customerService.updateCustomer(id, request);

        verify(customerStore).getCustomer(id);
        verify(customerStore).updateCustomer(argThat(customer ->
                customer.getPhone().equals("+233111222333") &&
                        customer.isActive()
        ));
    }

    @Test
    @DisplayName("Should update customer status successfully")
    void shouldUpdateCustomerStatusSuccessfully() {
        UUID id = UUID.randomUUID();
        CustomerRequestDto request = new CustomerRequestDto(null, false);

        Customer existing = Customer.builder()
                .customerId(id)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+233123456789")
                .isActive(true)
                .createdAt(Instant.now())
                .build();

        when(customerStore.getCustomer(id)).thenReturn(Optional.of(existing));

        customerService.updateCustomer(id, request);

        verify(customerStore).getCustomer(id);
        verify(customerStore).updateCustomer(argThat(customer ->
                customer.getPhone().equals("+233123456789") &&
                        !customer.isActive()
        ));
    }

    @Test
    @DisplayName("Should update both phone and status successfully")
    void shouldUpdateBothPhoneAndStatusSuccessfully() {
        UUID id = UUID.randomUUID();
        CustomerRequestDto request = new CustomerRequestDto("+233999888777", false);

        Customer existing = Customer.builder()
                .customerId(id)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+233123456789")
                .isActive(true)
                .createdAt(Instant.now())
                .build();

        when(customerStore.getCustomer(id)).thenReturn(Optional.of(existing));

        customerService.updateCustomer(id, request);

        verify(customerStore).getCustomer(id);
        verify(customerStore).updateCustomer(argThat(customer ->
                customer.getPhone().equals("+233999888777") &&
                        !customer.isActive()
        ));
    }

    @Test
    @DisplayName("Should throw error when updating non-existing customer")
    void shouldThrowWhenUpdatingNonExistingCustomer() {
        UUID id = UUID.randomUUID();
        CustomerRequestDto request = new CustomerRequestDto("+233111222333", null);

        when(customerStore.getCustomer(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.updateCustomer(id, request)
        );

        verify(customerStore).getCustomer(id);
        verify(customerStore, never()).updateCustomer(any());
    }

    @Test
    @DisplayName("Should preserve existing values when only updating phone")
    void shouldPreserveExistingValuesWhenOnlyUpdatingPhone() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now().minusSeconds(86400);
        CustomerRequestDto request = new CustomerRequestDto("+233111222333", null);

        Customer existing = Customer.builder()
                .customerId(id)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+233123456789")
                .isActive(true)
                .createdAt(createdAt)
                .build();

        when(customerStore.getCustomer(id)).thenReturn(Optional.of(existing));

        customerService.updateCustomer(id, request);

        verify(customerStore).updateCustomer(argThat(customer ->
                customer.getFirstName().equals("John") &&
                        customer.getLastName().equals("Doe") &&
                        customer.getEmail().equals("john@example.com") &&
                        customer.isActive() &&
                        customer.getCreatedAt().equals(createdAt)
        ));
    }

    @Test
    @DisplayName("Should preserve existing values when only updating status")
    void shouldPreserveExistingValuesWhenOnlyUpdatingStatus() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now().minusSeconds(86400);
        CustomerRequestDto request = new CustomerRequestDto(null, false);

        Customer existing = Customer.builder()
                .customerId(id)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .phone("+233987654321")
                .isActive(true)
                .createdAt(createdAt)
                .build();

        when(customerStore.getCustomer(id)).thenReturn(Optional.of(existing));

        customerService.updateCustomer(id, request);

        verify(customerStore).updateCustomer(argThat(customer ->
                customer.getFirstName().equals("Jane") &&
                        customer.getLastName().equals("Smith") &&
                        customer.getEmail().equals("jane@example.com") &&
                        customer.getPhone().equals("+233987654321") &&
                        customer.getCreatedAt().equals(createdAt)
        ));
    }

    @Test
    @DisplayName("Should handle pagination in get all customers")
    void shouldHandlePaginationInGetAllCustomers() {
        List<Customer> customers = List.of(
                Customer.builder()
                        .customerId(UUID.randomUUID())
                        .firstName("Customer1")
                        .lastName("Last1")
                        .email("customer1@example.com")
                        .phone("+233123456789")
                        .isActive(true)
                        .createdAt(Instant.now())
                        .build(),
                Customer.builder()
                        .customerId(UUID.randomUUID())
                        .firstName("Customer2")
                        .lastName("Last2")
                        .email("customer2@example.com")
                        .phone("+233987654321")
                        .isActive(true)
                        .createdAt(Instant.now())
                        .build()
        );

        when(customerStore.getAllCustomers(5, 10)).thenReturn(customers);

        List<CustomerResponseDto> result = customerService.getAllCustomers(5, 10);

        Assertions.assertEquals(2, result.size());
        verify(customerStore).getAllCustomers(5, 10);
    }

    @Test
    @DisplayName("Should handle pagination in search customers")
    void shouldHandlePaginationInSearchCustomers() {
        String query = "customer";
        List<Customer> customers = List.of(
                Customer.builder()
                        .customerId(UUID.randomUUID())
                        .firstName("Customer1")
                        .lastName("Last1")
                        .email("customer1@example.com")
                        .phone("+233123456789")
                        .isActive(true)
                        .createdAt(Instant.now())
                        .build()
        );

        when(customerStore.searchCustomers(query, 5, 10)).thenReturn(customers);

        List<CustomerResponseDto> result = customerService.searchCustomers(query, 5, 10);

        Assertions.assertEquals(1, result.size());
        verify(customerStore).searchCustomers(query, 5, 10);
    }

    @Test
    @DisplayName("Should preserve customer id when updating")
    void shouldPreserveCustomerIdWhenUpdating() {
        UUID id = UUID.randomUUID();
        CustomerRequestDto request = new CustomerRequestDto("+233111222333", false);

        Customer existing = Customer.builder()
                .customerId(id)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+233123456789")
                .isActive(true)
                .createdAt(Instant.now())
                .build();

        when(customerStore.getCustomer(id)).thenReturn(Optional.of(existing));

        customerService.updateCustomer(id, request);

        verify(customerStore).updateCustomer(argThat(customer ->
                customer.getCustomerId().equals(id)
        ));
    }

    @Test
    @DisplayName("Should map customer fields correctly in response")
    void shouldMapCustomerFieldsCorrectlyInResponse() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Customer customer = Customer.builder()
                .customerId(id)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+233123456789")
                .isActive(true)
                .createdAt(createdAt)
                .build();

        when(customerStore.getCustomer(id)).thenReturn(Optional.of(customer));

        CustomerResponseDto response = customerService.getCustomer(id);

        Assertions.assertEquals(id, response.getCustomerId());
        Assertions.assertEquals("John", response.getFirstName());
        Assertions.assertEquals("Doe", response.getLastName());
        Assertions.assertEquals("john@example.com", response.getEmail());
        Assertions.assertEquals("+233123456789", response.getPhone());
        Assertions.assertTrue(response.isActive());
        Assertions.assertEquals(createdAt, response.getCreatedAt());
    }
}
