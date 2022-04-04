package writeside.application;

import eventside.event.BookingCanceledEvent;
import eventside.event.BookingCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import writeside.domain.model.Booking;
import writeside.domain.model.Customer;
import writeside.domain.model.Room;
import writeside.domain.repository.BookingRepository;
import writeside.domain.repository.CustomerRepository;
import writeside.domain.repository.RoomRepository;
import writeside.domain.repository.WriteSideEventPublisher;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService{

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private WriteSideEventPublisher writeSideEventPublisher;

    @Override
    public boolean book(String scnr, List<String> rooms, LocalDate arrivalDate, LocalDate departureDate) {

        Set<String> roomSet = new HashSet<>(rooms);

        // Validate Data
        Customer customer = null;
        try {
            customer = customerRepository.getCustomer(scnr).orElseThrow(() -> new IllegalArgumentException("customer for id " + scnr + " not found"));
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
        BookingCreatedEvent event = new BookingCreatedEvent(
                booking.getBookingId(),
                booking.getArrivalDate(),
                booking.getDepartureDate(),
                booking.getRooms(),
                customer.getName()
        );
        writeSideEventPublisher.publishBookingCreatedEvent(event);

        return true;
    }

    @Override
    public boolean cancel(UUID bookingId) {
        AtomicBoolean bookingIsPresent = new AtomicBoolean(false);

        bookingRepository.bookingById(bookingId).ifPresent( booking -> {
            bookingRepository.cancelBooking(bookingId);
            BookingCanceledEvent event = new BookingCanceledEvent(
                     bookingId,
                     booking.getArrivalDate(),
                     booking.getDepartureDate(),
                     booking.getRooms()
                             .stream()
                             .map(roomRepository::getRoom)
                             .map(Optional::get)
                             .collect(Collectors.toMap(Room::getRoomNumber, Room::getNumberOfBeds))
            );
            writeSideEventPublisher.publishBookingCanceledEvent(event);
            bookingIsPresent.set(true);
        });

        return bookingIsPresent.get();
    }
}
