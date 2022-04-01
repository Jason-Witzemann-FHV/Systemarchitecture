package readside.dto;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;
import java.util.Set;

public class BookedStay {

    private final UUID bookingId;
    private final LocalDate from;
    private final LocalDate to;
    private final Set<String> rooms;
    private final String customerName;

    public BookedStay(UUID bookingId, LocalDate from, LocalDate to, Set<String> rooms, String customerName) {
        this.bookingId = bookingId;
        this.from = from;
        this.to = to;
        this.rooms = rooms;
        this.customerName = customerName;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public Set<String> getRooms() {
        return Collections.unmodifiableSet(rooms);
    }

    public String getCustomerName() {
        return customerName;
    }
}
