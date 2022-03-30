package writeside.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import writeside.domain.model.Booking;
import writeside.domain.model.Customer;
import writeside.domain.repository.BookingRepository;
import writeside.domain.repository.CustomerRepository;
import writeside.domain.repository.RoomRepository;

import java.time.LocalDate;
import java.util.*;

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

        Set<String> roomSet = new HashSet<>(rooms);

        // Validate Data
        try {
            customerRepository.getCustomer(scnr).orElseThrow(() -> new IllegalArgumentException("customer for id " + scnr + " not found"));
            roomSet.forEach(r -> roomRepository.getRoom(r).orElseThrow(() -> new IllegalArgumentException("room with nr " + r + " not found")));
            if (departureDate.isBefore(arrivalDate) || departureDate.isEqual(arrivalDate)) throw new IllegalArgumentException("Arrival and Departure date not valid");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }

        // Create Domain Object
        Booking booking = new Booking(
                UUID.randomUUID(),
                arrivalDate,
                departureDate,
                roomSet,
                scnr
        );

        // Add to Booking list
        bookingRepository.createBooking(booking);

        return true;
    }

    @Override
    public boolean cancel(UUID bookingId) {
        try {
            bookingRepository.cancelBooking(bookingId);
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
