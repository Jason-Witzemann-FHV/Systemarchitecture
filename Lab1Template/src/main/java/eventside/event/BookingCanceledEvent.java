package eventside.event;


import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public class BookingCanceledEvent extends Event {

    private final UUID bookingId;
    private final LocalDate arrivalDate;
    private final LocalDate departureDate;
    private final Map<String, Integer> rooms;

    public BookingCanceledEvent(UUID bookingId, LocalDate arrivalDate, LocalDate departureDate, Map<String, Integer> rooms) {
        this.bookingId = bookingId;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.rooms = rooms;
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

    public Map<String, Integer> getRooms() {
        return rooms;
    }
}
