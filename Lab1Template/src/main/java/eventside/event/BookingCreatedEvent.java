package eventside.event;

import java.time.LocalDate;
import java.util.UUID;
import java.util.Set;

public class BookingCreatedEvent extends Event {

    private final UUID bookingId;
    private final LocalDate arrivalDate;
    private final LocalDate departureDate;
    private final Set<String> rooms;
    private final String customer;

    public BookingCreatedEvent(UUID bookingId, LocalDate arrivalDate, LocalDate departureDate, Set<String> rooms, String customer) {
        this.bookingId = bookingId;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.rooms = rooms;
        this.customer = customer;
    }


    public UUID getBookingId() {
        return bookingId;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public Set<String> getRooms() {
        return rooms;
    }

    public String getCustomer() {
        return customer;
    }

}
