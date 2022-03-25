package writeside.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import writeside.domain.model.Customer;
import writeside.domain.repository.BookingRepository;
import writeside.domain.repository.CustomerRepository;
import writeside.domain.repository.RoomRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService{

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public boolean book(String scnr, List<String> rooms, LocalDate arrivalDate, LocalDate departureDate) {

        Customer customer = customerRepository.getCustomer(scnr).orElseThrow(() -> new IllegalArgumentException("customer for id " + scnr + " not found"));

        return false;
    }

    @Override
    public boolean cancel(UUID bookingId) {
        return false;
    }
}
