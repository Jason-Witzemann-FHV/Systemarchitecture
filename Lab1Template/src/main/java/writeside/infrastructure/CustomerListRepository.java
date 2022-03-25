package writeside.infrastructure;

import org.springframework.stereotype.Repository;
import writeside.domain.model.Customer;
import writeside.domain.repository.CustomerRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class CustomerListRepository implements CustomerRepository {

    private final Map<String, Customer> customers = new HashMap<>();

    @Override
    public void createCustomer(Customer customer) {
        customers.put(customer.getSocialCreditNr(), customer);
    }

    @Override
    public Optional<Customer> getCustomer(String socialCreditNr) {
        return Optional.ofNullable(customers.getOrDefault(socialCreditNr, null));
    }

}
