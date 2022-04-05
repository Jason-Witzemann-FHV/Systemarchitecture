package writeside.application;

import eventside.event.BookingCanceledEvent;
import eventside.event.BookingCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import writeside.application.command.BookRoomsCommand;
import writeside.application.command.CancelRoomCommand;
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
    public boolean book(BookRoomsCommand bookRooms) {

        Set<String> roomSet = new HashSet<>(bookRooms.getRooms());

        // Validate Data
        Customer customer = null;
        try {
            customer = customerRepository.getCustomer(bookRooms.getSocialSecurityNumber()).orElseThrow(() -> new IllegalArgumentException("customer for id " + bookRooms.getSocialSecurityNumber() + " not found"));
            roomSet.forEach(r -> roomRepository.getRoom(r).orElseThrow(() -> new IllegalArgumentException("room with nr " + r + " not found")));
            if (!bookRooms.getDepartureDate().isAfter(bookRooms.getArrivalDate())) throw new IllegalArgumentException("Arrival and Departure date not valid");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }

        // Create Domain Object
        Booking booking = new Booking(
                UUID.randomUUID(),
                bookRooms.getArrivalDate(),
                bookRooms.getDepartureDate(),
                roomSet,
                bookRooms.getSocialSecurityNumber()
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
    public boolean cancel(CancelRoomCommand cancelRoom) {
        AtomicBoolean bookingIsPresent = new AtomicBoolean(false);

        bookingRepository.bookingById(cancelRoom.getBookingId()).ifPresent( booking -> {
            bookingRepository.cancelBooking(cancelRoom.getBookingId());
            BookingCanceledEvent event = new BookingCanceledEvent(
                     cancelRoom.getBookingId(),
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
