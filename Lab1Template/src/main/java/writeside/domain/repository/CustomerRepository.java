package writeside.domain.repository;

import writeside.domain.model.Customer;

import java.util.Optional;

public interface CustomerRepository {

    void createCustomer(Customer customer);

    Optional<Customer> getCustomer(String socialCreditNr);
}
